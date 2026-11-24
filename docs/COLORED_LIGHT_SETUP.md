# Optional purple lighting for the grow light

Realistic Plants keeps its grow light independent of any colored-light renderer. Without an additional client mod, the grow light still emits ordinary Minecraft block light and continues to provide its plant-growth bonus.

## True purple-colored illumination (optional)

For colored light on nearby blocks, install **Colored Light** by comedy on each player's client.

- Use the **NeoForge** build matching the exact Minecraft version.
- For this project branch (Minecraft 26.3 / NeoForge), use the [NeoForge 26.3 release](https://www.curseforge.com/minecraft/mc-mods/colored-light/files/9579126).
- Colored Light is client-side only. It does not need to be installed on a dedicated server.
- Start Minecraft and open **Mods → Colored Light → Config**.
- Open **Block Colors**. The mod supports custom colors for modded light-emitting blocks as well as vanilla blocks.
- Find the Realistic Plants entry for **Purple Grow Light** (block name: `purple_grow_light`) and set its color to a purple value such as **`#A020F0`**.
- Adjust **Color Intensity** to taste.

Colored Light stores its client settings in `config/coloredlight.json`. Its Block Colors editor supports a hex/HSV color picker, presets, and per-block colors. The exact layout of the editor can vary between releases.

## Compatibility notes

Colored Light's published NeoForge support currently includes Minecraft 1.21 through 26.3. Always install the file matching the Minecraft version and loader in use. Realistic Plants itself must also have a build for that Minecraft/NeoForge version.

If Colored Light is absent, the grow light remains usable: ordinary white block lighting and the mod's plant-growth effect continue to work. No hard dependency is added.
