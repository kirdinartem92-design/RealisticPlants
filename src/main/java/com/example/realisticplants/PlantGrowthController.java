
package com.realisticplants;

/**
 * Controls the universal growth rules for all plant species.
 */
public final class PlantGrowthController {

    private static final int MIN_HEALTH_FOR_GROWTH = 30;

    private static final int MIN_MOISTURE = 0;
    private static final int MAX_MOISTURE = 100;

    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 100;

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

        int health = plant.getHealth();
        int moisture = plant.getMoisture();

        if (health < MIN_HEALTH_FOR_GROWTH) {
            return false;
        }

        if (moisture < definition.getMinimumMoisture()) {
            return false;
        }

        if (temperature < definition.getMinTemperature()
                || temperature > definition.getMaxTemperature()) {
            return false;
        }

        return PlantEnvironment.getGrowthMultiplier(temperature) > 0;
    }

    /**
     * Calculates growth points according to temperature,
     * soil moisture and plant health.
     */
    public static int getGrowthPoints(PlantData plant, float temperature) {

        if (!canGrow(plant, temperature)) {
            return 0;
        }

        int moisture = Math.max(
                MIN_MOISTURE,
                Math.min(MAX_MOISTURE, plant.getMoisture())
        );

        int health = Math.max(
                MIN_HEALTH,
                Math.min(MAX_HEALTH, plant.getHealth())
        );

        double temperatureMultiplier =
                PlantEnvironment.getGrowthMultiplier(temperature);

        double moistureMultiplier = moisture / 100.0;

        double healthMultiplier = health / 100.0;

        double totalMultiplier =
                temperatureMultiplier
                        * moistureMultiplier
                        * healthMultiplier;

        return Math.max(
                1,
                (int) Math.round(totalMultiplier * 2)
        );
    }
}
