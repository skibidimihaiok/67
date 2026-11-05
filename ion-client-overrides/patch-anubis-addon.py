from pathlib import Path
import json
import re

root = Path("ion-client/src/main/java/com/suschunkfinder")

# Expose the two small pieces of ION setting metadata needed by Anubis' UI bridge.
number = root / "NumberSetting.java"
text = number.read_text()
if "public double step()" not in text:
    anchor = "public double max() { return max; }"
    if anchor not in text:
        raise SystemExit("NumberSetting max() anchor missing")
    text = text.replace(anchor, anchor + "\n    public double step() { return step; }", 1)
number.write_text(text)

mode = root / "ModeSetting.java"
text = mode.read_text()
if "public String[] modes()" not in text:
    anchor = "public String mode() {\n\t\treturn modes[index()];\n\t}"
    if anchor not in text:
        raise SystemExit("ModeSetting mode() anchor missing")
    extra = anchor + """

    public String[] modes() { return modes.clone(); }

    public void setMode(String value) {
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].equals(value)) {
                setter.accept(i);
                return;
            }
        }
    }
"""
    text = text.replace(anchor, extra, 1)
mode.write_text(text)

# In Anubis mode, only initialise modules that Anubis does not already ship.
manager = root / "ModuleManager.java"
text = manager.read_text()
if "public static void initForAnubisAddon()" not in text:
    text = text.replace(
        "private static boolean loading;",
        "private static boolean loading;\n    private static boolean initialized;",
        1
    )
    old = "public static void init() {"
    if old not in text:
        raise SystemExit("ModuleManager init() anchor missing")
    text = text.replace(old, """public static void init() { init(false); }

    /** Called by the Anubis add-on entrypoint; keeps Anubis' own modules authoritative. */
    public static void initForAnubisAddon() { init(true); }

    private static void init(boolean anubisAddon) {
        if (initialized) return;
        initialized = true;""", 1)

    match = re.search(r"(?m)^([ \t]*)load\(\);\n[ \t]*ModuleKeybinds\.register\(\);", text)
    if match:
        indent = match.group(1)
        replacement = (
            indent + "if (anubisAddon) MODULES.removeIf(ModuleManager::isAnubisDuplicate);\n"
            + indent + "load();\n"
            + indent + "if (!anubisAddon) ModuleKeybinds.register();"
        )
        text = text[:match.start()] + replacement + text[match.end():]
    else:
        load_match = re.search(r"(?m)^([ \t]*)load\(\);", text)
        if not load_match:
            raise SystemExit("ModuleManager load() anchor missing")
        indent = load_match.group(1)
        replacement = (
            indent + "if (anubisAddon) MODULES.removeIf(ModuleManager::isAnubisDuplicate);\n"
            + indent + "load();"
        )
        text = text[:load_match.start()] + replacement + text[load_match.end():]
        text = text.replace(
            "        ModuleKeybinds.register();",
            "        if (!anubisAddon) ModuleKeybinds.register();",
            1
        )

    imports_anchor = "import java.util.Map;"
    if imports_anchor not in text:
        raise SystemExit("ModuleManager Map import anchor missing")
    text = text.replace(imports_anchor, imports_anchor + "\nimport java.util.Locale;\nimport java.util.Set;", 1)

    helper = """
    private static final Set<String> ANUBIS_OWNED = Set.of(
        "suschunkfinder", "playeresp", "mobesp", "tracers", "storageesp", "blockesp",
        "holeesp", "nametags", "spawnernametags", "radar", "regionmap", "armorhud",
        "potions", "fullbright", "zoom", "customfov", "customcrosshair", "customglint",
        "hitparticles", "freelook", "freecam", "autowalk", "autojump", "autorelog",
        "antivanish", "playerbypass2", "killaura", "sprint", "speed", "spider",
        "nofall", "triggerbot", "autototem", "aura", "automace", "autocrystal",
        "chatmacro", "spotify", "weathernotifier", "coordsnapper", "stafflist",
        "spawnerprotect", "nameprotect", "fastuse", "autotpa", "coordinateshud",
        "fpshud", "targethighlight", "targethud", "totemcounter", "trajectories",
        "pearlpredict", "projectilepredict", "swinganimation", "hudmanager", "hud",
        "watermark", "info", "keystrokes", "arraylist", "notifications"
    );

    private static boolean isAnubisDuplicate(ClientModule module) {
        String key = module.name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return ANUBIS_OWNED.contains(key);
    }

"""
    marker = "public static <T extends ClientModule> T find("
    index = text.find(marker)
    if index < 0:
        raise SystemExit("ModuleManager find() insertion anchor missing")
    text = text[:index] + helper + text[index:]
manager.write_text(text)

# Make this mod an Anubis add-on at runtime, while compiling against (but not bundling) Anubis.
metadata = Path("ion-client/src/main/resources/fabric.mod.json")
data = json.loads(metadata.read_text())
entrypoints = data.setdefault("entrypoints", {})
entrypoints["anubis"] = ["com.suschunkfinder.anubisaddon.IonAnubisAddon"]
depends = data.setdefault("depends", {})
depends["anubisplus"] = "*"
metadata.write_text(json.dumps(data, indent=2) + "\n")

gradle = Path("ion-client/build.gradle")
build = gradle.read_text()
if "files('/tmp/anubis-client.jar')" not in build and 'files("/tmp/anubis-client.jar")' not in build:
    build += """

// Compile against Anubis' extension API without bundling or replacing the Anubis client.
dependencies {
    compileOnly files("/tmp/anubis-client.jar")
}
"""
gradle.write_text(build)

print("Prepared ION as an Anubis add-on, bridged settings, and filtered duplicate modules.")
