
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
        applyHeat(plant, temperature, 0);
    }

    /**
     * Applies normal moisture consumption
     * and additional drying caused by heat.
     */
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

        int totalDrying =
                Math.max(0, baseConsumption) + heatDrying;

        PlantGrowthManager.drySoil(
                plant,
                totalDrying
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
