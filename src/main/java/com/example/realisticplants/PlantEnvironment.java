
package com.realisticplants;

/**
 * Handles environmental effects on plants.
 */
public class PlantEnvironment {

    private PlantEnvironment() {
    }

    /**
     * Applies rain to plant moisture.
     */
    public static void applyRain(PlantData plant, int amount) {
        if (plant == null || plant.isDead()) {
            return;
        }

        PlantGrowthManager.water(plant, amount);
    }

    /**
     * Applies temperature-dependent soil drying.
     */
    public static void applyHeat(
            PlantData plant,
            float temperature
    ) {
        if (plant == null || plant.isDead()) {
            return;
        }

        int dryingAmount;

        if (temperature < 0.5f) {
            dryingAmount = 0;
        } else if (temperature <= 1.2f) {
            dryingAmount = 1;
        } else if (temperature <= 1.6f) {
            dryingAmount = 2;
        } else {
            dryingAmount = 3;
        }

        PlantGrowthManager.drySoil(
                plant,
                dryingAmount
        );
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
