
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
     * Applies heat and dries the soil.
     */
    public static void applyHeat(PlantData plant, int amount) {
        if (plant == null || plant.isDead()) {
            return;
        }

        PlantGrowthManager.drySoil(plant, amount);
    }

    /**
     * Returns the growth multiplier based on Minecraft biome temperature.
     *
     * Cold biomes slow down plant growth.
     * Moderate temperatures provide optimal growth.
     * Very hot biomes cause heat stress.
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
