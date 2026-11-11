# ION + Anubis (Minecraft 1.21.11)

This branch builds ION as an Anubis add-on. Anubis remains the base client: its Click GUI, PvP modules, visuals, settings, and other built-in modules are kept. ION's unique modules are registered in Anubis's native module list and use Anubis's native settings screen.

## Install

1. Install Fabric for Minecraft 1.21.11.
2. Download the original Anubis 1.21.11 JAR from the [Anubis release](https://github.com/skibidimihaiok/67/releases/edit/Anubis) and put it in your `mods` folder.
3. Download the `ION-ANUBIS-ADDON` artifact from the successful GitHub Actions build and put its JAR in the same `mods` folder.
4. Do not install the older standalone ION JAR at the same time. The companion requires Anubis and skips ION modules that Anubis already provides.

Open Anubis's own Click GUI. ION-only modules will be listed in the matching Anubis categories.

## Build output

GitHub Actions uploads:
- `ION-ANUBIS-ADDON`: the ION companion mod.
- `anubis-decompiled-source`: decompiled Anubis review source.
- `ion-java-source`: ION Java source used for the build.

The add-on bridge is compile-checked by CI. Actual in-game behaviour still needs to be tested in Minecraft 1.21.11.
