package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for crop-specific irrigation targets.
 */
class IrrigationCoordinationTest {

    @Test
    void targetAddsASafetyMarginToTheConfiguredMinimum() {
        PlantData plant = plantWithMinimumMoisture(20);

        assertEquals(40, IrrigationCoordination.getTargetMoisture(plant));
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
    void registeredCropsReceiveDifferentIrrigationTargets() {
        assertEquals(
                40,
                IrrigationCoordination.getTargetMoisture(
                        new PlantData(PlantRegistry.get("tomato"))
                )
        );
        assertEquals(
                45,
                IrrigationCoordination.getTargetMoisture(
                        new PlantData(PlantRegistry.get("zucchini"))
                )
        );
        assertEquals(
                50,
                IrrigationCoordination.getTargetMoisture(
                        new PlantData(PlantRegistry.get("broccoli"))
                )
        );
    }

    @Test
    void nullPlantUsesTheSafeDefaultTarget() {
        assertEquals(40, IrrigationCoordination.getTargetMoisture(null));
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
