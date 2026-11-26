package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for gradual moisture recovery from natural water.
 */
class PlantEnvironmentTest {

    @Test
    void nearbyWaterProvidesFiveMoistureAtZeroDistance() {
        assertEquals(5, PlantEnvironment.getNearbyWaterMoisture(0));
    }

    @Test
    void waterEffectDecreasesWithDistance() {
        assertEquals(4, PlantEnvironment.getNearbyWaterMoisture(1));
        assertEquals(3, PlantEnvironment.getNearbyWaterMoisture(2));
        assertEquals(2, PlantEnvironment.getNearbyWaterMoisture(3));
        assertEquals(2, PlantEnvironment.getNearbyWaterMoisture(4));
    }

    @Test
    void waterOutsideTheSupportedRadiusHasNoEffect() {
        assertEquals(0, PlantEnvironment.getNearbyWaterMoisture(-1));
        assertEquals(0, PlantEnvironment.getNearbyWaterMoisture(5));
    }
}
