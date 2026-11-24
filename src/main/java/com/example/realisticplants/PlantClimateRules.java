package com.realisticplants;

/**
 * Pure climate rules shared by every crop.
 *
 * This class deliberately has no Minecraft dependencies so its rules
 * can be verified with ordinary JVM unit tests.
 */
public final class PlantClimateRules {

    private PlantClimateRules() {
    }

    /**
     * Returns the growth multiplier for a Minecraft biome temperature.
     */
    public static double getGrowthMultiplier(float temperature) {
        if (!Float.isFinite(temperature)) {
            return 0.0;
        }

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
