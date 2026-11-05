#!/usr/bin/env python3
"""Package the ION Anubis add-on inside the exact released Anubis 1.21.11 JAR.

The original Anubis classes/resources are copied byte-for-byte. Only its
fabric.mod.json is updated to declare ION as a nested Fabric mod.
"""
from pathlib import Path
import json
import subprocess
import zipfile

BASE_URL = "https://github.com/skibidimihaiok/67/releases/download/Anubis/1.21.11.jar"
BASE = Path("/tmp/anubis-base-1.21.11.jar")
ADDON_DIR = Path("ion-client/build/libs")
OUTPUT = Path("dist/Anubis-ION-1.21.11.jar")
NESTED = "META-INF/jars/ion-client-anubis-addon.jar"
REQUIRED_ANUBIS_ID = "anubisplus"

BASE.parent.mkdir(parents=True, exist_ok=True)
subprocess.run(["curl", "-fL", "--retry", "3", "--retry-delay", "2", "-o", str(BASE), BASE_URL], check=True)

candidates = sorted(
    p for p in ADDON_DIR.glob("*.jar")
    if not any(tag in p.name.lower() for tag in ("sources", "javadoc", "dev"))
)
if not candidates:
    raise SystemExit("No built ION add-on JAR found.")
addon_path = candidates[0]

with zipfile.ZipFile(BASE, "r") as z:
    if z.testzip() is not None:
        raise SystemExit("The downloaded Anubis 1.21.11 JAR is damaged.")
    try:
        base_meta = json.loads(z.read("fabric.mod.json"))
    except KeyError:
        raise SystemExit("The official Anubis 1.21.11 JAR has no fabric.mod.json.")

with zipfile.ZipFile(addon_path, "r") as z:
    if z.testzip() is not None:
        raise SystemExit("The built ION add-on JAR is damaged.")
    try:
        addon_meta = json.loads(z.read("fabric.mod.json"))
    except KeyError:
        raise SystemExit("The ION add-on JAR has no fabric.mod.json.")
    addon_bytes = addon_path.read_bytes()

base_ids = {base_meta.get("id", "")}
base_ids.update(base_meta.get("provides", []))
depends = addon_meta.get("depends", {})
if REQUIRED_ANUBIS_ID not in base_ids or REQUIRED_ANUBIS_ID not in depends:
    raise SystemExit(
        "Anubis/ION dependency mismatch: expected the original base JAR to provide "
        f"{REQUIRED_ANUBIS_ID!r} and the add-on to depend on it. "
        f"Base id={base_meta.get('id')!r}, addon depends={depends!r}"
    )

nested_entries = base_meta.setdefault("jars", [])
if not isinstance(nested_entries, list):
    raise SystemExit("Anubis fabric.mod.json has an unexpected 'jars' format.")
nested_entries = [entry for entry in nested_entries if entry.get("file") != NESTED]
nested_entries.append({"file": NESTED})
base_meta["jars"] = nested_entries
metadata_bytes = (json.dumps(base_meta, indent=2, ensure_ascii=False) + "\n").encode("utf-8")

OUTPUT.parent.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(BASE, "r") as source, zipfile.ZipFile(OUTPUT, "w") as target:
    for info in source.infolist():
        if info.filename in ("fabric.mod.json", NESTED):
            continue
        target.writestr(info, source.read(info.filename))
    target.writestr("fabric.mod.json", metadata_bytes, compress_type=zipfile.ZIP_DEFLATED)
    target.writestr(NESTED, addon_bytes, compress_type=zipfile.ZIP_DEFLATED)

with zipfile.ZipFile(OUTPUT, "r") as merged:
    if merged.testzip() is not None:
        raise SystemExit("Combined JAR failed ZIP integrity validation.")
    merged_meta = json.loads(merged.read("fabric.mod.json"))
    embedded = merged.read(NESTED)
    if not embedded:
        raise SystemExit("ION nested JAR is empty.")
    if not any(entry.get("file") == NESTED for entry in merged_meta.get("jars", [])):
        raise SystemExit("ION nested JAR is not declared in Anubis metadata.")

print(f"Created {OUTPUT} from the official Anubis 1.21.11 JAR.")
print(f"Kept Anubis mod id {base_meta.get('id')!r}; embedded ION mod id {addon_meta.get('id')!r}.")
print("Original Anubis classes/resources, including its KillAura and Mace, were not rewritten.")
