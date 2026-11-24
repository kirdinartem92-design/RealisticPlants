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
    }

    /**
     * Checks whether a plant can grow.
     */
    public static boolean canGrow(PlantData plant, float temperature) {

        if (plant == null || plant.isDead()) {
            return false;
        }

        PlantDefinition definition = plant.getDefinition();

        if (definition == null) {
            return false;
        }

        if (plant.getHealth() < MIN_HEALTH_FOR_GROWTH) {
            return false;
        }

        if (plant.getMoisture() < definition.getMinimumMoisture()) {
            return false;
        }

        if (temperature < definition.getMinTemperature()
                || temperature > definition.getMaxTemperature()) {
            return false;
        }

        return PlantClimateRules.getGrowthMultiplier(temperature) > 0;
    }

    /**
     * Calculates fractional growth progress.
     */
    public static double getGrowthAmount(
            PlantData plant,
            float temperature
    ) {

        if (!canGrow(plant, temperature)) {
            return 0.0;
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
                PlantClimateRules.getGrowthMultiplier(temperature);

        double moistureMultiplier = moisture / 100.0;
        double healthMultiplier = health / 100.0;

        double result = temperatureMultiplier
                * moistureMultiplier
                * healthMultiplier
                * 2.0;

        if (!Double.isFinite(result)) {
            return 0.0;
        }

        return Math.max(0.0, result);
    }

    /**
     * Legacy integer growth method.
     */
    public static int getGrowthPoints(
            PlantData plant,
            float temperature
    ) {

        double amount = getGrowthAmount(plant, temperature);

        return (int) Math.round(amount);
    }
}
