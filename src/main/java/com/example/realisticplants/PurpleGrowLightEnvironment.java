package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

/**
 * Finds nearby purple grow lights without changing vanilla crop behavior.
 * The bounded spherical scan runs only when a plant performs a growth update.
 */
public final class PurpleGrowLightEnvironment {

    private static final int RADIUS = 4;
    private static final double GROWTH_MULTIPLIER = 1.25;
    private static final double DAYLIGHT_MULTIPLIER = 1.05;
    private static final double COMBINED_MULTIPLIER = 1.35;

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

        int radiusSquared = RADIUS * RADIUS;
        boolean hasPurpleGrowLight = false;
        for (int dx = -RADIUS; dx <= RADIUS && !hasPurpleGrowLight; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS && !hasPurpleGrowLight; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos lightPos = plantPos.offset(dx, dy, dz);
                    if (level.getBlockState(lightPos)
                            .is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        hasPurpleGrowLight = true;
                        break;
                    }
                }
            }
        }

        // Natural sky light gives a small bonus. Purple grow lights work
        // independently at night, and combine with daylight up to a safe cap.
        int skyLight = level.getBrightness(LightLayer.SKY, plantPos);
        boolean hasStrongDaylight = skyLight >= 12;

        if (hasPurpleGrowLight && hasStrongDaylight) {
            return COMBINED_MULTIPLIER;
        }
        if (hasPurpleGrowLight) {
            return GROWTH_MULTIPLIER;
        }
        if (hasStrongDaylight) {
            return DAYLIGHT_MULTIPLIER;
        }
        return 1.0;
    }
}
