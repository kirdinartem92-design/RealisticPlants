package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Automatically controls greenhouse grow lights and their crop-growth bonus.
 * Lights are always active at night and activate during daytime when natural
 * sky light is insufficient. Only the seven crops added by this mod are used.
 */
public final class PurpleGrowLightEnvironment {

    private static final int RADIUS = 4;
    private static final int NATURAL_LIGHT_THRESHOLD = 12;
    private static final double GROWTH_MULTIPLIER = 1.25;
    private static final double DAYLIGHT_MULTIPLIER = 1.05;

    private static final Set<String> SUPPORTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    private PurpleGrowLightEnvironment() {
    }

    /**
     * Switches all grow lights in the detected greenhouse together.
     * At night they are always on; during daytime they turn on if any
     * supported crop in the greenhouse receives insufficient sky light.
     */
    public static void updateGrowLights(
            Level level,
            BlockPos plantPos,
            boolean inGreenhouse
    ) {
        if (level == null || plantPos == null || !inGreenhouse) {
            return;
        }

        int north = GreenhouseEnvironment.findWallDistance(level, plantPos, 0, -1);
        int south = GreenhouseEnvironment.findWallDistance(level, plantPos, 0, 1);
        int west = GreenhouseEnvironment.findWallDistance(level, plantPos, -1, 0);
        int east = GreenhouseEnvironment.findWallDistance(level, plantPos, 1, 0);
        if (north < 0 || south < 0 || west < 0 || east < 0) {
            return;
        }

        // Minecraft 26.3 exposes the current global sky darkness through Level.
        // A value of 4 or more means it is dark enough for nighttime lighting.
        boolean night = level.getSkyDarken() >= 4;
        boolean lightNeeded = night;

        if (!lightNeeded) {
            for (int dx = -west + 1; dx < east && !lightNeeded; dx++) {
                for (int dy = -2; dy <= 2 && !lightNeeded; dy++) {
                    for (int dz = -north + 1; dz < south; dz++) {
                        BlockPos cropPos = plantPos.offset(dx, dy, dz);
                        if (!level.hasChunkAt(cropPos)
                                || !(level.getBlockEntity(cropPos)
                                        instanceof CropPlantBlockEntity crop)) {
                            continue;
                        }

                        if (SUPPORTED_CROPS.contains(
                                crop.getPlantData().getDefinition().getId())
                                && level.getBrightness(LightLayer.SKY, cropPos)
                                        < NATURAL_LIGHT_THRESHOLD) {
                            lightNeeded = true;
                            break;
                        }
                    }
                }
            }
        }

        for (int dx = -west + 1; dx < east; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -north + 1; dz < south; dz++) {
                    BlockPos lightPos = plantPos.offset(dx, dy, dz);
                    if (!level.hasChunkAt(lightPos)) {
                        continue;
                    }

                    BlockState state = level.getBlockState(lightPos);
                    if (!state.is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        continue;
                    }

                    boolean active = state.getValue(PurpleGrowLightBlock.ACTIVE);
                    if (active != lightNeeded) {
                        level.setBlock(
                                lightPos,
                                state.setValue(PurpleGrowLightBlock.ACTIVE, lightNeeded),
                                3
                        );
                    }
                }
            }
        }
    }

    public static double getGrowthMultiplier(
            Level level,
            BlockPos plantPos,
            PlantDefinition definition
    ) {
        if (level == null || plantPos == null || definition == null
                || !SUPPORTED_CROPS.contains(definition.getId())) {
            return 1.0;
        }

        int skyLight = level.getBrightness(LightLayer.SKY, plantPos);
        if (skyLight >= NATURAL_LIGHT_THRESHOLD) {
            return DAYLIGHT_MULTIPLIER;
        }

        if (hasNearbyActivePurpleGrowLight(level, plantPos)) {
            return GROWTH_MULTIPLIER;
        }

        return 1.0;
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
