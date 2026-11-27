package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests lifecycle-state invariants that must also hold after save loading.
 */
class PlantDataTest {

    @Test
    void deadPlantCannotRemainFloweringOrHarvestReady() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setHealth(0);
        plant.setFlowering(true);
        plant.setHarvestReady(true);

        plant.normalizeLifecycleState();

        assertTrue(plant.isDead());
        assertFalse(plant.isFlowering());
        assertFalse(plant.isHarvestReady());
    }

    @Test
    void harvestReadyRequiresFlowering() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setFlowering(false);
        plant.setHarvestReady(true);

        plant.normalizeLifecycleState();

        assertFalse(plant.isHarvestReady());
    }

    @Test
    void dryFloweringPlantKeepsFloweringAndHarvestReady() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setMoisture(0);
        plant.setFlowering(true);
        plant.setHarvestReady(true);

        plant.normalizeLifecycleState();

        assertTrue(plant.isFlowering());
        assertTrue(plant.isHarvestReady());
    }

    @Test
    void validFloweringAndHarvestReadyStateIsPreserved() {
        PlantData plant = new PlantData(PlantRegistry.get("tomato"));
        plant.setMoisture(60);
        plant.setHealth(80);
        plant.setFlowering(true);
        plant.setHarvestReady(true);

        plant.normalizeLifecycleState();

        assertTrue(plant.isFlowering());
        assertTrue(plant.isHarvestReady());
    }
}
