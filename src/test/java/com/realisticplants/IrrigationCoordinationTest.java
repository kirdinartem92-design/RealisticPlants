package com.realisticplants;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void irrigationSupportsOnlyTheSevenNewModCrops() {
        for (String id : List.of(
                "tomato", "cucumber", "zucchini", "eggplant",
                "bell_pepper", "chili_pepper", "broccoli"
        )) {
            assertTrue(
                    IrrigationCoordination.supportsIrrigation(
                            new PlantData(PlantRegistry.get(id))
                    ),
                    id + " should support mod irrigation"
            );
        }

        // These root-crop definitions are not part of the new-crop
        // irrigation system; vanilla crop mechanics remain independent.
        for (String id : List.of("potato", "carrot", "beetroot")) {
            assertFalse(
                    IrrigationCoordination.supportsIrrigation(
                            new PlantData(PlantRegistry.get(id))
                    ),
                    id + " must not be handled by mod irrigation"
            );
        }

        assertFalse(IrrigationCoordination.supportsIrrigation(null));
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
