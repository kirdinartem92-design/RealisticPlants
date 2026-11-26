# Optional purple colored lighting for the grow light

Realistic Plants does not require a colored-light renderer. Without an additional client mod, the grow light emits ordinary Minecraft block light (level 12 while active) and continues to provide its plant-growth bonus.

## Recommended client mod

The optional **Colored Light** mod by comedy adds real colored illumination to nearby surfaces. It is client-side, so it does not need to be installed on a dedicated server.

For Minecraft 26.3 with NeoForge, use **Colored Light 1.1.0**:

https://www.curseforge.com/minecraft/mc-mods/colored-light/files/9799640

Version 1.1.0 supports custom colors for individual light-emitting blocks. It also colors nearby entities, dropped items, and particles, and colored illumination can reach across the render distance.

## Set the Realistic Plants grow light to purple

1. Install the NeoForge file matching your exact Minecraft version.
2. Launch Minecraft with both Realistic Plants and Colored Light installed on the client.
3. Open **Mods → Colored Light → Config**.
4. Open the **Block Colors** section.
5. Search for **Realistic Plants** or the block ID `realisticplants:purple_grow_light`.
6. Select the purple grow-light block and set its color to **`#A020F0`** (a vivid violet).
7. Save/close the configuration screen. Adjust the global Color Intensity setting if you want a stronger or subtler tint.

The exact layout or wording can vary slightly between Colored Light builds.

## Compatibility and limitations

- Colored Light is an optional client-side visual enhancement. Other players only see the colored illumination if they also use a compatible client-side renderer.
- Without Colored Light, the grow light still works normally and emits ordinary Minecraft block light at level 12 while active.
- The grow-light growth bonus is independent of Colored Light and remains available without it.
- Colored Light changes client rendering; it does not alter server-side crop growth calculations or world-light rules.
- Realistic Plants automatically keeps the purple grow light active at night, even when no supported crops are nearby. During the day, it only stays active when nearby supported crops need additional light.
- The exact purple appearance still needs an in-game check with Realistic Plants and Colored Light 1.1.0 installed.
