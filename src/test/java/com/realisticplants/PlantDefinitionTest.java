package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for per-crop configuration and shared plant state.
 */
class PlantDefinitionTest {

    @Test
    void cropsKeepTheirOwnTemperatureRangesAndGrowthIntervals() {
        PlantDefinition tomato = PlantRegistry.get("tomato");
        PlantDefinition broccoli = PlantRegistry.get("broccoli");

        assertEquals(0.15f, tomato.getMinTemperature());
        assertEquals(1.6f, tomato.getMaxTemperature());
        assertEquals(1200, tomato.getGrowthInterval());

        assertEquals(0.10f, broccoli.getMinTemperature());
        assertEquals(1.45f, broccoli.getMaxTemperature());
        assertEquals(1450, broccoli.getGrowthInterval());
    }

    @Test
    void moistureAndGrowthIntervalsCanBeConfiguredIndependently() {
        PlantDefinition definition = new PlantDefinition(
                "test_crop",
                6, 4, 3,
                800, 200, 2,
                0.2f, 1.4f,
                25, 20, true,
                1, 4, 2, 0.3f
        );

        assertEquals(800, definition.getGrowthInterval());
        assertEquals(200, definition.getMoistureInterval());
    }

    @Test
    void invalidTemperatureRangesAreRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new PlantDefinition(
                        "invalid_crop",
                        6, 4, 3,
                        800, 2,
                        1.4f, 0.2f,
                        25, 20, true,
                        1, 4, 2, 0.3f
                )
        );

        assertThrows(IllegalArgumentException.class, () ->
                new PlantDefinition(
                        "invalid_crop",
                        6, 4, 3,
                        800, 2,
                        Float.NaN, 1.4f,
                        25, 20, true,
                        1, 4, 2, 0.3f
                )
        );
    }

    @Test
    void growthProgressNeverBecomesNegativeOrNonFinite() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));

        plant.setGrowthProgress(-5.0);
        assertEquals(0.0, plant.getGrowthProgress());

        plant.setGrowthProgress(Double.NaN);
        assertEquals(0.0, plant.getGrowthProgress());

        plant.setGrowthProgress(Double.POSITIVE_INFINITY);
        assertEquals(0.0, plant.getGrowthProgress());

        plant.setGrowthProgress(2.5);
        assertEquals(2.5, plant.getGrowthProgress());
        assertTrue(Double.isFinite(plant.getGrowthProgress()));
    }
}
