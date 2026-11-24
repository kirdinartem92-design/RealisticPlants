# Optional colored lighting for the grow light

Realistic Plants does not require a colored-light renderer. Without an additional client mod, the grow light emits ordinary Minecraft block light and continues to provide its plant-growth bonus.

## Colored Light compatibility status

The optional **Colored Light** mod by comedy provides real RGB-style colored illumination on nearby block surfaces. It is client-side and does not need to be installed on a dedicated server.

For this project branch (Minecraft 26.3 / NeoForge), the candidate release is:
https://www.curseforge.com/minecraft/mc-mods/colored-light/files/9579126

**Important limitation discovered during source inspection:** the current Colored Light source derives colors from built-in block-name rules. Its current settings screen exposes global enable/intensity and stained-glass settings; it does not expose a per-block custom color picker. An unrecognized block name falls back to a warm light color. Therefore the current release does not yet let a player simply select `#A020F0` for `realisticplants:purple_grow_light`.

We must not claim that installing Colored Light alone makes the Realistic Plants lamp purple. A small, optional client compatibility integration (or an upstream feature in Colored Light) is still needed to assign our grow-light block the purple RGB value. This integration must remain optional so Realistic Plants continues to load and function when Colored Light is absent.

## Compatibility notes

Always use a Colored Light NeoForge build matching the exact Minecraft version and loader. Realistic Plants itself must also have a build for that Minecraft/NeoForge version.

Until the optional color integration is implemented and tested in-game, the grow light remains ordinary vanilla block-light color. Its plant-growth effect is independent of Colored Light.
