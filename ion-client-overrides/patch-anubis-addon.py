from pathlib import Path
import json
import re

root = Path("ion-client/src/main/java/com/suschunkfinder")

# Expose the two small pieces of ION setting metadata needed by Anubis' UI bridge.
number = root / "NumberSetting.java"
text = number.read_text()
# Keep the settings UI compatible with both the original ION source and the
# override copy. Insert any missing accessors after getInt(), independent of
# whitespace or whether the source already exposes min()/max().
import re
accessors = (
    "    public double min() { return min; }",
    "    public double max() { return max; }",
    "    public double step() { return step; }",
)
missing = [line for line in accessors if re.search(r"public double " + line.split()[3].split("(")[0] + r"\s*\(", text) is None]
if missing:
    match = re.search(r"(\t*public int getInt\(\)\s*\{[^}]*\})", text)
    if not match:
        raise SystemExit("NumberSetting getInt() anchor missing")
    text = text[:match.end()] + "\n" + "\n".join(missing) + text[match.end():]
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
    // Names and IDs already implemented by Anubis 0.9.9.1. Keep Anubis' originals authoritative.
    private static final Set<String> ANUBIS_OWNED = Set.of(
        "suschunkfinder",
        "clickgui",
        "playeresp",
        "mobesp",
        "tracers",
        "storageesp",
        "blockesp",
        "holeesp",
        "nametags",
        "spawnernametags",
        "radar",
        "regionmap",
        "armorhud",
        "potions",
        "fullbright",
        "zoom",
        "customfov",
        "customcrosshair",
        "customglint",
        "hitparticles",
        "autowalk",
        "autojump",
        "autorelog",
        "antivanish",
        "playerbypass2",
        "killaura",
        "sprint",
        "speed",
        "spider",
        "nofall",
        "autototem",
        "aura",
        "automace",
        "autocrystal",
        "chatmacro",
        "spotify",
        "weathernotifier",
        "coordsnapper",
        "stafflist",
        "spawnerprotect",
        "nameprotect",
        "fastuse",
        "autotpa",
        "coordinateshud",
        "fpshud",
        "targethighlight",
        "targethud",
        "totemcounter",
        "trajectories",
        "pearlpredict",
        "projectilepredict",
        "swinganimation",
        "hudmanager",
        "hud",
        "watermark",
        "info",
        "keystrokes",
        "arraylist",
        "notifications",
        "hotkeys",
        "performance",
        "aimassist",
        "anchoraura",
        "autocity",
        "autoinvtotem",
        "crystalaura",
        "crystalmacro",
        "crystaloptimizer",
        "fastxp",
        "friendguard",
        "hovertotem",
        "jumpreset",
        "keythrow",
        "shieldbreaker",
        "anchormacro",
        "spearswap",
        "wtap",
        "ahsell",
        "ahsniper",
        "autosell",
        "autotpahere",
        "orderahseller",
        "orderdropper",
        "panicpay",
        "tpmacro",
        "airstuck",
        "amethystbypass",
        "antitrap",
        "autoclicker",
        "autoeat",
        "autoreconnect",
        "autoreplenish",
        "blocknotifier",
        "discordpresence",
        "donutnofall",
        "fakestats",
        "homemeta",
        "legitautotool",
        "netheritefinder",
        "noinvleak",
        "playerbypass",
        "playerdetection",
        "quantum",
        "renderdistancetoggle",
        "riptide",
        "skinchange",
        "slowswing",
        "spam",
        "speedmine",
        "tridentutil",
        "boatfly",
        "boatnoclip",
        "clicktp",
        "elytrafly",
        "elytraswap",
        "entitycontrol",
        "entityspeed",
        "fly",
        "jesus",
        "nojumpdelay",
        "sneak",
        "tridentboost",
        "atmosphere",
        "blockentitydebug",
        "blockoverlay",
        "chunkscanner",
        "colorgrading",
        "floatingrelics",
        "handaura",
        "3dhand",
        "handpose",
        "itemesp",
        "jumpcircles",
        "lightdebug",
        "lowhealthwarning",
        "motionblur",
        "norender",
        "pearltrajectory",
        "primechunkfinder",
        "particles",
        "glowesp",
        "spawnernotifier",
        "targetesp",
        "timechanger",
        "totemspirit",
        "weaponinspect",
        "xray",
        "character3d"
    );

    private static boolean isAnubisDuplicate(ClientModule module) {
        String key = module.name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return ANUBIS_OWNED.contains(key) || key.contains("esp") || key.contains("tracer");
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
# Register camera and other client hooks as a real mixin configuration.
mixin_dir = root / "mixin"
required_mixins = {"CameraMixin", "TurnMixin"}
available_mixins = {p.stem for p in mixin_dir.glob("*.java")}
missing_mixins = required_mixins - available_mixins
if missing_mixins:
    raise SystemExit("Freecam mixin sources are missing: " + ", ".join(sorted(missing_mixins)))

camera = mixin_dir / "CameraMixin.java"
camera_source = camera.read_text()
camera_source = camera_source.replace(
    "import net.minecraft.world.level.BlockGetter;",
    "import net.minecraft.world.level.Level;"
)
camera_source = camera_source.replace(
    "BlockGetter level, Entity entity",
    "Level level, Entity entity"
)
camera_source = camera_source.replace(
    '@Inject(method = "setup", at = @At("TAIL"), require = 0)',
    '@Inject(method = "setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V", at = @At("TAIL"), require = 1)'
)
if "BlockGetter level" in camera_source or "Level level, Entity entity" not in camera_source:
    raise SystemExit("CameraMixin does not match the Minecraft 1.21.11 Camera.setup(Level, Entity, boolean, boolean, float) signature.")
if 'method = "setup(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/Entity;ZZF)V"' not in camera_source:
    raise SystemExit("CameraMixin does not have the 1.21.11 setup method descriptor.")
camera.write_text(camera_source)

mixin_names = [
    "CameraMixin", "TurnMixin", "FastUseMixin", "GlintMixin",
    "SkinProtectMixin", "SwingSpeedMixin", "TrimMixin"
]
mixin_names = [name for name in mixin_names if name in available_mixins]
mixin_config = {
    "required": False,
    "minVersion": "0.8",
    "package": "com.suschunkfinder.mixin",
    "compatibilityLevel": "JAVA_21",
    "client": mixin_names,
    "injectors": {"defaultRequire": 0}
}
resources = Path("ion-client/src/main/resources")
resources.mkdir(parents=True, exist_ok=True)
(resources / "suschunkfinder.mixins.json").write_text(
    json.dumps(mixin_config, indent=2) + "\n"
)
mixins = data.setdefault("mixins", [])
if not any(
    (entry == "suschunkfinder.mixins.json")
    or (isinstance(entry, dict) and entry.get("config") == "suschunkfinder.mixins.json")
    for entry in mixins
):
    mixins.append({
        "config": "suschunkfinder.mixins.json",
        "environment": "client"
    })

metadata.write_text(json.dumps(data, indent=2) + "\n")

# Give the new Left Shift binding a readable name in Controls.
language = resources / "assets/ionclient/lang/en_us.json"
language.parent.mkdir(parents=True, exist_ok=True)
translations = json.loads(language.read_text()) if language.is_file() else {}
translations["key.ionclient.open_gui"] = "Open ION Client GUI"
translations["key.categories.ionclient.controls"] = "ION Client"
language.write_text(json.dumps(translations, indent=2) + "\n")

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

print("Prepared ION as an Anubis add-on, registered Freecam camera mixins, and filtered duplicate modules.")
