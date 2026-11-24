package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for universal temperature-based growth rules.
 */
class PlantClimateRulesTest {

    private static final double DELTA = 0.000001;

    @Test
    void temperaturesBelowMinimumDoNotSupportGrowth() {
        assertEquals(0.0, PlantClimateRules.getGrowthMultiplier(0.14f), DELTA);
        assertEquals(0.0, PlantClimateRules.getGrowthMultiplier(-1.0f), DELTA);
    }

    @Test
    void coolTemperaturesUseHalfGrowthRate() {
        assertEquals(0.5, PlantClimateRules.getGrowthMultiplier(0.15f), DELTA);
        assertEquals(0.5, PlantClimateRules.getGrowthMultiplier(0.49f), DELTA);
    }

    @Test
    void moderateTemperaturesUseFullGrowthRate() {
        assertEquals(1.0, PlantClimateRules.getGrowthMultiplier(0.5f), DELTA);
        assertEquals(1.0, PlantClimateRules.getGrowthMultiplier(1.2f), DELTA);
    }

    @Test
    void hotTemperaturesUseHalfGrowthRateUntilUpperLimit() {
        assertEquals(
                0.5,
                PlantClimateRules.getGrowthMultiplier(Math.nextUp(1.2f)),
                DELTA
        );
        assertEquals(0.5, PlantClimateRules.getGrowthMultiplier(1.6f), DELTA);
        assertEquals(
                0.0,
                PlantClimateRules.getGrowthMultiplier(Math.nextUp(1.6f)),
                DELTA
        );
    }

    @Test
    void nonFiniteTemperaturesNeverSupportGrowth() {
        assertEquals(0.0, PlantClimateRules.getGrowthMultiplier(Float.NaN), DELTA);
        assertEquals(
                0.0,
                PlantClimateRules.getGrowthMultiplier(Float.POSITIVE_INFINITY),
                DELTA
        );
        assertEquals(
                0.0,
                PlantClimateRules.getGrowthMultiplier(Float.NEGATIVE_INFINITY),
                DELTA
        );
    }
}
