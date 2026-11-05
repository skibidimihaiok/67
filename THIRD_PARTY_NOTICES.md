# Third-party notices

ION's Anubis companion is designed to run alongside the Anubis 1.21.11 client (Fabric mod ID `anubisplus`). It uses Anubis's documented add-on entrypoint to register ION-only modules in Anubis's native module list and Click GUI. It does not bundle or replace Anubis's classes, GUI, PvP modules, assets, or nested dependencies.

The Anubis release supplied with this repository includes its `LICENSE_anubis` file and is licensed under GPL-3.0-only. The build retains the complete license text in the ION companion JAR at `META-INF/licenses/ANUBIS-GPL-3.0.txt`. Anubis itself remains a separate prerequisite mod; install the original Anubis 1.21.11 JAR alongside the ION companion.

ION modules already provided by Anubis are excluded from the companion registry so Anubis's native implementations remain in control. ION-only modules are adapted into Anubis's module/settings API; their original implementations remain in the ION source tree and the CI source artifact.
