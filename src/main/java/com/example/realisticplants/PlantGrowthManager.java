package com.realisticplants;

/**
 * Manages moisture and health for individual plants.
 *
 * This manager is shared by all plant types.
 * Individual plant behavior is controlled by PlantDefinition.
 */
public class PlantGrowthManager {

    private static final int MIN_MOISTURE = 0;
    private static final int MAX_MOISTURE = 100;

    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 100;

    private static final int BASE_DROUGHT_DAMAGE = 2;

    private static final int BASE_CRITICAL_MOISTURE = 20;
    private static final int RECOVERY_MOISTURE = 30;

    private PlantGrowthManager() {
    }

    /**
     * Waters a plant.
     */
    public static void water(PlantData plant, int amount) {
        if (plant == null || amount <= 0 || plant.isDead()) {
            return;
        }

        long newMoisture = (long) plant.getMoisture() + amount;

        plant.setMoisture(
                (int) Math.min(MAX_MOISTURE, newMoisture)
        );
    }

    /**
     * Reduces soil moisture over time.
     */
    public static void drySoil(PlantData plant, int amount) {
        if (plant == null || amount <= 0 || plant.isDead()) {
            return;
        }

        long newMoisture = (long) plant.getMoisture() - amount;

        plant.setMoisture(
                (int) Math.max(MIN_MOISTURE, newMoisture)
        );
    }

    /**
     * Updates plant health according to moisture
     * and individual drought resistance.
     */
    public static void updateHealth(PlantData plant) {
        updateHealth(plant, Float.NaN);
    }

    /**
     * Updates health from moisture and, for this mod's seven new crops only,
     * excessive heat. Vanilla crop definitions retain the old health rules.
     */
    public static void updateHealth(PlantData plant, float temperature) {
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

        // Higher resistance reduces drought damage.
        int droughtDamage = Math.max(
                0,
                BASE_DROUGHT_DAMAGE - droughtResistance / 25
        );

        // Higher resistance allows plants to survive
        // at lower soil moisture levels.
        int criticalMoisture = Math.max(
                0,
                BASE_CRITICAL_MOISTURE - droughtResistance / 5
        );

        if (moisture < criticalMoisture) {

            plant.setHealth(
                    Math.max(
                            MIN_HEALTH,
                            health - droughtDamage
                    )
            );

        } else if (moisture > RECOVERY_MOISTURE
                && health < MAX_HEALTH) {

            plant.setHealth(
                    Math.min(
                            MAX_HEALTH,
                            health + 1
                    )
            );
        }

        // Apply heat damage after drought recovery so a well-watered plant
        // cannot cancel overheating damage in the same update.
        boolean modCrop = switch (definition.getId()) {
            case "tomato", "cucumber", "zucchini", "eggplant",
                    "bell_pepper", "chili_pepper", "broccoli" -> true;
            default -> false;
        };
        if (modCrop && Float.isFinite(temperature)
                && temperature > definition.getMaxTemperature()) {
            float excess = temperature - definition.getMaxTemperature();
            int heatDamage = excess >= 0.5f ? 2 : 1;
            plant.setHealth(Math.max(
                    MIN_HEALTH,
                    plant.getHealth() - heatDamage
            ));
        }
    }
}
