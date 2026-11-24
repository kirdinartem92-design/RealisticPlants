package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for species-aware plant growth decisions.
 */
class PlantGrowthControllerTest {

    private static final double DELTA = 0.000001;

    @Test
    void eachCropUsesItsOwnTemperatureRange() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantData broccoli = new PlantData(PlantRegistry.get("broccoli"));

        assertTrue(PlantGrowthController.canGrow(tomato, 1.5f));
        assertFalse(PlantGrowthController.canGrow(broccoli, 1.5f));

        assertTrue(PlantGrowthController.canGrow(broccoli, 1.4f));
        assertFalse(PlantGrowthController.canGrow(tomato, 0.1f));
    }

    @Test
    void growthRequiresTheCropMinimumMoisture() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setMoisture(19);

        assertFalse(PlantGrowthController.canGrow(tomato, 0.8f));

        tomato.setMoisture(20);
        assertTrue(PlantGrowthController.canGrow(tomato, 0.8f));
    }

    @Test
    void growthRequiresMinimumHealth() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(29);

        assertFalse(PlantGrowthController.canGrow(tomato, 0.8f));

        tomato.setHealth(30);
        assertTrue(PlantGrowthController.canGrow(tomato, 0.8f));
    }

    @Test
    void deadPlantsAndInvalidTemperaturesCannotGrow() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(0);

        assertFalse(PlantGrowthController.canGrow(tomato, 0.8f));
        assertEquals(0.0, PlantGrowthController.getGrowthAmount(tomato, 0.8f), DELTA);

        PlantData healthyTomato = new PlantData(PlantRegistry.get("tomato"));
        assertFalse(PlantGrowthController.canGrow(healthyTomato, Float.NaN));
        assertFalse(PlantGrowthController.canGrow(
                healthyTomato,
                Float.POSITIVE_INFINITY
        ));
    }

    @Test
    void growthAmountScalesWithMoistureAndHealth() {
        PlantData fullPlant = new PlantData(PlantRegistry.get("tomato"));
        assertEquals(
                2.0,
                PlantGrowthController.getGrowthAmount(fullPlant, 0.8f),
                DELTA
        );

        PlantData reducedPlant = new PlantData(PlantRegistry.get("tomato"));
        reducedPlant.setMoisture(50);
        reducedPlant.setHealth(50);

        assertEquals(
                0.5,
                PlantGrowthController.getGrowthAmount(reducedPlant, 0.8f),
                DELTA
        );
    }

    @Test
    void temperatureMultiplierAlsoScalesGrowthAmount() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));

        assertEquals(
                2.0,
                PlantGrowthController.getGrowthAmount(tomato, 0.8f),
                DELTA
        );
        assertEquals(
                1.0,
                PlantGrowthController.getGrowthAmount(tomato, 0.2f),
                DELTA
        );
    }
}
