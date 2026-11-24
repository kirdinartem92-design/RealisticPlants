package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * Automatically prioritizes artificial grow lights when natural sky light
 * is insufficient. The system affects only the seven crops added by this mod.
 *
 * The block's vanilla light emission remains static; this class controls the
 * crop-growth benefit according to the natural light available at the plant.
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
        boolean hasStrongNaturalLight = skyLight >= NATURAL_LIGHT_THRESHOLD;

        // Natural daylight is preferred. Artificial grow lights provide
        // their stronger bonus only when the plant lacks sufficient sky light.
        if (hasStrongNaturalLight) {
            return DAYLIGHT_MULTIPLIER;
        }

        if (hasNearbyPurpleGrowLight(level, plantPos)) {
            return GROWTH_MULTIPLIER;
        }

        return 1.0;
    }

    /**
     * Checks a bounded spherical area without loading neighboring chunks.
     */
    private static boolean hasNearbyPurpleGrowLight(
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
                    if (level.hasChunkAt(lightPos)
                            && level.getBlockState(lightPos)
                                    .is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
