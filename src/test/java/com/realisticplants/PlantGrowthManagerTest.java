package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Regression tests for the shared moisture and plant-health rules.
 */
class PlantGrowthManagerTest {

    @Test
    void wateringAndDryingStayWithinZeroToOneHundred() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));

        PlantGrowthManager.water(plant, 150);
        assertEquals(100, plant.getMoisture());

        PlantGrowthManager.drySoil(plant, 150);
        assertEquals(0, plant.getMoisture());

        PlantGrowthManager.water(plant, -10);
        PlantGrowthManager.drySoil(plant, -10);
        assertEquals(0, plant.getMoisture());
    }

    @Test
    void droughtDamageUsesTheIndividualCropResistance() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setMoisture(15);

        PlantGrowthManager.updateHealth(tomato);

        assertEquals(98, tomato.getHealth());

        PlantData broccoli = new PlantData(PlantRegistry.get("broccoli"));
        broccoli.setMoisture(14);

        PlantGrowthManager.updateHealth(broccoli);

        assertEquals(99, broccoli.getHealth());
    }

    @Test
    void healthRecoversOnlyAboveTheRecoveryThreshold() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setHealth(50);
        plant.setMoisture(30);

        PlantGrowthManager.updateHealth(plant);
        assertEquals(50, plant.getHealth());

        plant.setMoisture(31);
        PlantGrowthManager.updateHealth(plant);
        assertEquals(51, plant.getHealth());
    }

    @Test
    void deadPlantsCannotBeRevivedByWaterOrHealthUpdates() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setHealth(0);
        plant.setMoisture(0);

        PlantGrowthManager.water(plant, 100);
        PlantGrowthManager.updateHealth(plant);

        assertEquals(0, plant.getHealth());
        assertEquals(0, plant.getMoisture());
        assertEquals(PlantCondition.DEAD, plant.getCondition());
    }

    @Test
    void plantConditionReflectsMoistureAndHealth() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        assertEquals(PlantCondition.HEALTHY, plant.getCondition());

        plant.setMoisture(30);
        assertEquals(PlantCondition.WILTING, plant.getCondition());

        plant.setMoisture(15);
        assertEquals(PlantCondition.DRY, plant.getCondition());

        plant.setHealth(0);
        assertEquals(PlantCondition.DEAD, plant.getCondition());
    }
}
