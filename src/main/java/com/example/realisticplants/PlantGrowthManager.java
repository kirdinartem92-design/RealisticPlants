
package com.realisticplants;

/**
 * Manages moisture and health for individual plants.
 */
public class PlantGrowthManager {

    private static final int MIN_MOISTURE = 0;
    private static final int MAX_MOISTURE = 100;

    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 100;

    private static final int BASE_DROUGHT_DAMAGE = 2;

    private PlantGrowthManager() {
    }

    /**
     * Waters a plant.
     */
    public static void water(PlantData plant, int amount) {
        if (plant == null || amount <= 0 || plant.isDead()) {
            return;
        }

        plant.setMoisture(
                Math.min(
                        MAX_MOISTURE,
                        plant.getMoisture() + amount
                )
        );
    }

    /**
     * Reduces soil moisture over time.
     */
    public static void drySoil(PlantData plant, int amount) {
        if (plant == null || amount <= 0 || plant.isDead()) {
            return;
        }

        plant.setMoisture(
                Math.max(
                        MIN_MOISTURE,
                        plant.getMoisture() - amount
                )
        );
    }

    /**
     * Updates plant health according to moisture
     * and individual drought resistance.
     */
    public static void updateHealth(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return;
        }

        PlantDefinition definition = plant.getDefinition();

        if (definition == null) {
            return;
        }

        int moisture = plant.getMoisture();
        int health = plant.getHealth();

        int droughtResistance = Math.max(
                0,
                Math.min(100, definition.getDroughtResistance())
        );

        int droughtDamage = Math.max(
                0,
                BASE_DROUGHT_DAMAGE - droughtResistance / 25
        );

        int criticalMoisture = Math.max(
                0,
                20 - droughtResistance / 5
        );

        if (moisture < criticalMoisture) {
            plant.setHealth(
                    Math.max(
                            MIN_HEALTH,
                            health - droughtDamage
                    )
            );
        } else if (moisture > 30 && health < MAX_HEALTH) {
            plant.setHealth(
                    Math.min(
                            MAX_HEALTH,
                            health + 1
                    )
            );
        }
    }
}
