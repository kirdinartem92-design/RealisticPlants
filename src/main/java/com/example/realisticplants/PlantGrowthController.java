
package com.realisticplants;

/**
 * Controls the universal growth rules for all plant species.
 */
public final class PlantGrowthController {

    private PlantGrowthController() {
        // Utility class
    }

    /**
     * Checks whether a plant can grow under current conditions.
     */
    public static boolean canGrow(PlantData plant, float temperature) {

        if (plant == null || plant.isDead()) {
            return false;
        }

        PlantDefinition definition = plant.getDefinition();

        if (definition == null) {
            return false;
        }

        if (plant.getHealth() < 30) {
            return false;
        }

        if (plant.getMoisture() < definition.getMinimumMoisture()) {
            return false;
        }

        if (temperature < definition.getMinTemperature()
                || temperature > definition.getMaxTemperature()) {
            return false;
        }

        return PlantEnvironment.getGrowthMultiplier(temperature) > 0;
    }

    /**
     * Calculates how many growth points a plant receives.
     */
    public static int getGrowthPoints(PlantData plant, float temperature) {

        if (!canGrow(plant, temperature)) {
            return 0;
        }

        float multiplier =
                PlantEnvironment.getGrowthMultiplier(temperature);

        return Math.max(1, (int) (multiplier * 2));
    }
}
