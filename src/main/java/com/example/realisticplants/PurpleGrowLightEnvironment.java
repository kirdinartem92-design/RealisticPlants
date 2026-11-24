package com.realisticplants;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * Finds nearby purple grow lights without changing vanilla crop behavior.
 * The bounded spherical scan runs only when a plant performs a growth update.
 */
public final class PurpleGrowLightEnvironment {

    private static final int RADIUS = 4;
    private static final double GROWTH_MULTIPLIER = 1.25;

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
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    if (dx * dx + dy * dy + dz * dz > radiusSquared) {
                        continue;
                    }

                    BlockPos lightPos = plantPos.offset(dx, dy, dz);
                    if (level.getBlockState(lightPos)
                            .is(ModBlocks.PURPLE_GROW_LIGHT.get())) {
                        return GROWTH_MULTIPLIER;
                    }
                }
            }
        }

        return 1.0;
    }
}
