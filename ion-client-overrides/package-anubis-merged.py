#!/usr/bin/env python3
"""Package ION inside the real Anubis 0.9.9.1 Minecraft 1.21.11 release.

Anubis.Beta.0.9.9.1.zip extracts to an exploded Fabric mod directory. Its
native GUI, client classes, metadata, assets, and bundled dependencies are
kept; the ION add-on is registered as an additional nested Fabric JAR.
"""
from pathlib import Path
import io
import json
import subprocess
import zipfile

RELEASE_URL = (
    "https://github.com/skibidimihaiok/67/releases/download/Anubis/"
    "Anubis.Beta.0.9.9.1.zip"
)
RELEASE_ZIP = Path("/tmp/anubis-release.zip")
BASE_DIR = Path("/tmp/anubis-release")
ADDON_DIR = Path("ion-client/build/libs")
OUTPUT = Path("dist/Anubis-ION-1.21.11.jar")
NESTED = "META-INF/jars/ion-client-anubis-addon.jar"
REQUIRED_ANUBIS_ID = "anubisplus"

if not (BASE_DIR / "fabric.mod.json").is_file():
    BASE_DIR.mkdir(parents=True, exist_ok=True)
    subprocess.run(
        ["curl", "-fL", "--retry", "3", "--retry-delay", "2", "-o", str(RELEASE_ZIP), RELEASE_URL],
        check=True,
    )
    subprocess.run(["unzip", "-q", "-o", str(RELEASE_ZIP), "-d", str(BASE_DIR)], check=True)

metadata_path = BASE_DIR / "fabric.mod.json"
if not metadata_path.is_file():
    raise SystemExit("The Anubis 0.9.9.1 release archive has no root fabric.mod.json.")

candidates = sorted(
    p for p in ADDON_DIR.glob("*.jar")
    if not any(tag in p.name.lower() for tag in ("sources", "javadoc", "dev"))
)
if not candidates:
    raise SystemExit("No built ION add-on JAR found.")
addon_path = candidates[0]

with metadata_path.open(encoding="utf-8") as handle:
    base_meta = json.load(handle)
if base_meta.get("id") != REQUIRED_ANUBIS_ID:
    raise SystemExit(
        "Wrong base release: expected Anubis mod id "
        f"{REQUIRED_ANUBIS_ID!r}, got {base_meta.get('id')!r}."
    )
if not str(base_meta.get("version", "")).startswith("0.9.9.1"):
    raise SystemExit(f"Unexpected Anubis base version: {base_meta.get('version')!r}")

with zipfile.ZipFile(addon_path, "r") as addon:
    if addon.testzip() is not None:
        raise SystemExit("The built ION add-on JAR is damaged.")
    addon_meta = json.loads(addon.read("fabric.mod.json"))
    if REQUIRED_ANUBIS_ID not in addon_meta.get("depends", {}):
        raise SystemExit("ION add-on does not declare its Anubis dependency.")
    mixin_entries = addon_meta.get("mixins", [])
    if not any(
        entry == "suschunkfinder.mixins.json"
        or (isinstance(entry, dict) and entry.get("config") == "suschunkfinder.mixins.json")
        for entry in mixin_entries
    ):
        raise SystemExit("ION metadata does not register its Freecam mixin config.")
    mixin_config = json.loads(addon.read("suschunkfinder.mixins.json"))
    if not {"CameraMixin", "TurnMixin"}.issubset(set(mixin_config.get("client", []))):
        raise SystemExit("Freecam camera and mouse-turn mixins are not both registered.")
    class_names = set(addon.namelist())
    for required_class in (
        "com/suschunkfinder/FreecamModule.class",
        "com/suschunkfinder/mixin/CameraMixin.class",
        "com/suschunkfinder/mixin/TurnMixin.class",
        "com/suschunkfinder/anubisaddon/IonAnubisAddon.class",
    ):
        if required_class not in class_names:
            raise SystemExit(f"ION add-on is missing {required_class}.")
    addon_bytes = addon_path.read_bytes()

nested_entries = base_meta.setdefault("jars", [])
if not isinstance(nested_entries, list):
    raise SystemExit("Anubis fabric.mod.json has an unexpected 'jars' format.")
nested_entries = [entry for entry in nested_entries if entry.get("file") != NESTED]
nested_entries.append({"file": NESTED})
base_meta["jars"] = nested_entries
# Only the display name is changed. Keep the Anubis mod id/version, entrypoints,
# native GUI classes, native mixins, and original bundled library files intact.
base_meta["name"] = "ION Client"
metadata_bytes = (json.dumps(base_meta, indent=2, ensure_ascii=False) + "\n").encode("utf-8")

base_files = sorted(path for path in BASE_DIR.rglob("*") if path.is_file())
expected_entrypoint = "net/anubis/client/AnubisClientMod.class"
if not any(path.relative_to(BASE_DIR).as_posix() == expected_entrypoint for path in base_files):
    raise SystemExit("Anubis native client entrypoint class is missing from the release files.")

OUTPUT.parent.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(OUTPUT, "w", compression=zipfile.ZIP_DEFLATED) as target:
    for path in base_files:
        name = path.relative_to(BASE_DIR).as_posix()
        if name == "fabric.mod.json" or name == NESTED:
            continue
        target.write(path, name)
    target.writestr("fabric.mod.json", metadata_bytes, compress_type=zipfile.ZIP_DEFLATED)
    target.writestr(NESTED, addon_bytes, compress_type=zipfile.ZIP_DEFLATED)

with zipfile.ZipFile(OUTPUT, "r") as merged:
    if merged.testzip() is not None:
        raise SystemExit("Combined Anubis/ION JAR failed ZIP integrity validation.")
    merged_meta = json.loads(merged.read("fabric.mod.json"))
    if merged_meta.get("id") != REQUIRED_ANUBIS_ID or merged_meta.get("name") != "ION Client":
        raise SystemExit("Combined JAR metadata is not correct.")
    if not any(entry.get("file") == NESTED for entry in merged_meta.get("jars", [])):
        raise SystemExit("ION nested JAR is not declared in Anubis metadata.")
    with zipfile.ZipFile(io.BytesIO(merged.read(NESTED)), "r") as embedded:
        if embedded.testzip() is not None:
            raise SystemExit("Embedded ION add-on JAR failed ZIP integrity validation.")

print(f"Created {OUTPUT} using Anubis {base_meta.get('version')} ({REQUIRED_ANUBIS_ID}).")
print(f"Kept Anubis's native GUI and client entrypoint; embedded ION mod {addon_meta.get('id')!r}.")
print("Freecam camera mixins are registered and validated in the embedded ION add-on.")
