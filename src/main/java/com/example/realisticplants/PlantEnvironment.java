
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
     * Returns the growth multiplier based on temperature.
     */
    public static double getGrowthMultiplier(float temperature) {

        if (temperature < 5.0f) {
            return 0.0;
        }

        if (temperature < 15.0f) {
            return 0.5;
        }

        if (temperature <= 30.0f) {
            return 1.0;
        }

        if (temperature <= 35.0f) {
            return 0.5;
        }

        return 0.0;
    }
}
