# Optional purple colored lighting for the grow light

Realistic Plants does not require a colored-light renderer. Without an additional client mod, the grow light emits ordinary Minecraft block light and continues to provide its plant-growth bonus.

## Recommended client mod

The optional **Colored Light** mod by comedy adds real colored illumination to nearby block surfaces. It is client-side, so it does not need to be installed on a dedicated server.

For Minecraft 26.3 with NeoForge, use the matching release:

https://www.curseforge.com/minecraft/mc-mods/colored-light/files/9579126

The release notes for Colored Light 1.0.2 explicitly add **Block Colors**: custom colors for any light source, including modded blocks, with search, a per-mod list, a hex/HSV editor, presets, and reset controls.

## Set the Realistic Plants grow light to purple

1. Install the NeoForge file for the exact Minecraft version you use.
2. Launch Minecraft with both Realistic Plants and Colored Light installed on the client.
3. Open **Mods → Colored Light → Config**.
4. Open the **Block Colors** section.
5. Search for **Realistic Plants** or the block ID `realisticplants:purple_grow_light`.
6. Select the purple grow-light block and set its color to **`#A020F0`** (a vivid violet).
7. Save/close the configuration screen. Adjust the global Color Intensity setting if you want a stronger or subtler tint.

The exact layout/wording can vary slightly between Colored Light builds. Its release notes confirm the custom per-block color editor, while the project README confirms the NeoForge config entry point.

## Compatibility and limitations

- Colored Light lists NeoForge support through Minecraft 26.3. Always install the file matching the exact Minecraft and NeoForge versions.
- Colored Light is client-side. Other players do not need it, but they will only see the purple colored illumination if they also use a compatible client-side colored-light renderer.
- The ordinary Minecraft block-light emission and Realistic Plants growth bonus remain independent of Colored Light.
- The purple tint is a client rendering effect; it does not change the server's light level, crop growth calculations, or world lighting rules.
- This setup is documented from the mod's published 1.0.2 release notes. The exact appearance still needs an in-game check with Realistic Plants installed.
