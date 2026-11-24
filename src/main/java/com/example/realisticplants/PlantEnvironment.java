package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;

/**
 * Handles environmental effects on plants.
 */
public class PlantEnvironment {

    private static final int NATURAL_WATER_RADIUS = 4;
    private static final int CLOSE_WATER_MOISTURE = 5;
    private static final int MIN_WATER_MOISTURE = 2;

    private PlantEnvironment() {
    }

    public static void applyRain(PlantData plant, int amount) {
        if (plant == null || plant.isDead()) {
            return;
        }
        PlantGrowthManager.water(plant, amount);
    }

    /**
     * Replenishes moisture from nearby water. The farmland block below
     * the plant is used as the center; only the strongest nearby source
     * is applied, so multiple water blocks never stack.
     */
    public static void applyNearbyWater(
            Level level,
            BlockPos plantPos,
            PlantData plant
    ) {
        if (level == null || plantPos == null
                || plant == null || plant.isDead()) {
            return;
        }

        BlockPos farmlandPos = plantPos.below();
        int moistureAmount = 0;

        for (int dx = -NATURAL_WATER_RADIUS;
                dx <= NATURAL_WATER_RADIUS; dx++) {
            for (int dz = -NATURAL_WATER_RADIUS;
                    dz <= NATURAL_WATER_RADIUS; dz++) {

                int distance = Math.max(Math.abs(dx), Math.abs(dz));
                if (distance > NATURAL_WATER_RADIUS) {
                    continue;
                }

                int candidateMoisture = Math.max(
                        MIN_WATER_MOISTURE,
                        CLOSE_WATER_MOISTURE - distance + 1
                );

                for (int dy = 0; dy <= 1; dy++) {
                    BlockPos waterPos = farmlandPos.offset(dx, dy, dz);
                    if (level.getFluidState(waterPos).is(FluidTags.WATER)) {
                        moistureAmount = Math.max(
                                moistureAmount,
                                candidateMoisture
                        );
                    }
                }
            }
        }

        if (moistureAmount > 0) {
            PlantGrowthManager.water(plant, moistureAmount);
        }
    }

    public static void applyHeat(
            PlantData plant,
            float temperature
    ) {
        applyHeat(plant, temperature, 0);
    }

    public static void applyHeat(
            PlantData plant,
            float temperature,
            int baseConsumption
    ) {
        if (plant == null || plant.isDead()) {
            return;
        }

        int heatDrying;
        if (temperature <= 1.2f) {
            heatDrying = 0;
        } else if (temperature <= 1.6f) {
            heatDrying = 1;
        } else {
            heatDrying = 2;
        }

        int totalDrying = Math.max(0, baseConsumption) + heatDrying;
        PlantGrowthManager.drySoil(plant, totalDrying);
    }

    /**
     * Returns the growth multiplier based on Minecraft biome temperature.
     */
    public static double getGrowthMultiplier(float temperature) {
        if (temperature < 0.15f) {
            return 0.0;
        }
        if (temperature < 0.5f) {
            return 0.5;
        }
        if (temperature <= 1.2f) {
            return 1.0;
        }
        if (temperature <= 1.6f) {
            return 0.5;
        }
        return 0.0;
    }
}
