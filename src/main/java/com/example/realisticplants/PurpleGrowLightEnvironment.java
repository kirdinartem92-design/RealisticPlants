package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Controls grow lights for mod crops both inside and outside greenhouses.
 * A lamp activates whenever a supported crop is nearby, and also at night
 * even when no crop is nearby. Active lamps grant a growth bonus in either place.
 */
public final class PurpleGrowLightEnvironment {

    private static final int RADIUS = 4;
    private static final double GROWTH_MULTIPLIER = 1.25;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato", "cucumber", "zucchini", "eggplant",
            "bell_pepper", "chili_pepper", "broccoli"
    );

    private PurpleGrowLightEnvironment() {
    }

    /**
     * Updates every lamp in range. The greenhouse flag is retained for
     * compatibility with existing callers, but lamps are not greenhouse-only.
     */
    public static void updateGrowLights(
            Level level,
            BlockPos plantPos,
            boolean inGreenhouse
    ) {
        if (level == null || plantPos == null) {
            return;
        }

        int radiusSquared = RADIUS * RADIUS;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos lightPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(lightPos)) {
                        continue;
                    }

                    BlockState state = level.getBlockState(lightPos);
                    if (!state.is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        continue;
                    }

                    boolean shouldBeActive = shouldLampBeActive(level, lightPos);
                    if (state.getValue(PurpleGrowLightBlock.ACTIVE) != shouldBeActive) {
                        level.setBlock(
                                lightPos,
                                state.setValue(PurpleGrowLightBlock.ACTIVE, shouldBeActive),
                                3
                        );
                    }
                }
            }
        }
    }

    private static boolean shouldLampBeActive(Level level, BlockPos lightPos) {
        // Keep the lamp on at night, even if no crop is nearby.
        if (level.getSkyDarken() >= 4) {
            return true;
        }

        // A nearby supported crop is enough to activate the lamp at any time
        // of day. This avoids relying on sky-light sampling, which can vary
        // with weather, shade, and the exact position of the crop block.
        int radiusSquared = RADIUS * RADIUS;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos cropPos = lightPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(cropPos)
                            || !(level.getBlockEntity(cropPos)
                                    instanceof CropPlantBlockEntity crop)) {
                        continue;
                    }

                    if (SUPPORTED_CROPS.contains(crop.getPlantData().getDefinition().getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static double getGrowthMultiplier(
            Level level,
            BlockPos plantPos,
            PlantDefinition definition,
            boolean inGreenhouse
    ) {
        if (level == null || plantPos == null || definition == null
                || !SUPPORTED_CROPS.contains(definition.getId())) {
            return 1.0;
        }

        return hasNearbyActivePurpleGrowLight(level, plantPos)
                ? GROWTH_MULTIPLIER
                : 1.0;
    }

    private static boolean hasNearbyActivePurpleGrowLight(
            Level level,
            BlockPos plantPos
    ) {
        int radiusSquared = RADIUS * RADIUS;
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos lightPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(lightPos)) {
                        continue;
                    }

                    BlockState state = level.getBlockState(lightPos);
                    if (state.is(ModBlocks.PURPLE_GROW_LIGHT.get())
                            && state.getValue(PurpleGrowLightBlock.ACTIVE)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}