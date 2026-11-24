package com.realisticplants;

/**
 * Shared health and moisture requirements for important plant lifecycle
 * transitions such as flowering and harvest formation.
 */
public final class PlantLifecycleRules {

    public static final int MIN_HEALTH_FOR_LIFECYCLE = 30;

    private PlantLifecycleRules() {
    }

    /**
     * Returns whether a plant is healthy and hydrated enough to flower
     * or form a harvest. Temperature is handled by growth rules separately.
     */
    public static boolean canContinueLifecycle(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return false;
        }

        PlantDefinition definition = plant.getDefinition();
        if (definition == null) {
            return false;
        }

        return plant.getHealth() >= MIN_HEALTH_FOR_LIFECYCLE
                && plant.getMoisture() >= definition.getMinimumMoisture();
    }
}
