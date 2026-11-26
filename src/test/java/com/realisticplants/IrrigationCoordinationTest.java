package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for crop-specific irrigation targets.
 */
class IrrigationCoordinationTest {

    @Test
    void targetKeepsAReasonableMinimumForLowMoistureCrops() {
        PlantData plant = plantWithMinimumMoisture(20);

        assertEquals(60, IrrigationCoordination.getTargetMoisture(plant));
    }

    @Test
    void targetScalesWithTheCropMinimumMoisture() {
        PlantData plant = plantWithMinimumMoisture(45);

        assertEquals(65, IrrigationCoordination.getTargetMoisture(plant));
    }

    @Test
    void targetNeverExceedsTheSharedUpperLimit() {
        PlantData plant = plantWithMinimumMoisture(90);

        assertEquals(80, IrrigationCoordination.getTargetMoisture(plant));
    }

    @Test
    void nullPlantUsesTheSafeDefaultTarget() {
        assertEquals(60, IrrigationCoordination.getTargetMoisture(null));
    }

    private static PlantData plantWithMinimumMoisture(int minimumMoisture) {
        PlantDefinition definition = new PlantDefinition(
                "test_crop",
                6, 4, 3,
                800, 200, 2,
                0.2f, 1.4f,
                minimumMoisture, 20, true,
                1, 4, 2, 0.3f
        );
        return new PlantData(definition);
    }
}
