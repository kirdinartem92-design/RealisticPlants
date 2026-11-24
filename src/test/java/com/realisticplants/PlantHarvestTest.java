package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for the shared flowering and harvest lifecycle.
 */
class PlantHarvestTest {

    @Test
    void harvestCannotBeProducedBeforeFlowering() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));

        PlantHarvest.produceHarvest(tomato);

        assertFalse(tomato.isHarvestReady());
    }

    @Test
    void floweringPlantProducesHarvestWhenHealthyAndMoist() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));

        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);

        assertTrue(tomato.isFlowering());
        assertTrue(tomato.isHarvestReady());
    }

    @Test
    void dryOrDeadPlantsCannotProduceHarvest() {
        PlantData dryTomato = new PlantData(PlantRegistry.get("tomato"));
        dryTomato.setMoisture(0);
        PlantHarvest.startFlowering(dryTomato);
        PlantHarvest.produceHarvest(dryTomato);

        assertFalse(dryTomato.isHarvestReady());

        PlantData deadTomato = new PlantData(PlantRegistry.get("tomato"));
        deadTomato.setHealth(0);
        PlantHarvest.startFlowering(deadTomato);
        PlantHarvest.produceHarvest(deadTomato);

        assertFalse(deadTomato.isFlowering());
        assertFalse(deadTomato.isHarvestReady());
    }

    @Test
    void harvestAmountRespectsEachCropConfiguration() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantData cucumber = new PlantData(PlantRegistry.get("cucumber"));
        PlantData broccoli = new PlantData(PlantRegistry.get("broccoli"));

        for (PlantData plant : new PlantData[]{tomato, cucumber, broccoli}) {
            PlantHarvest.startFlowering(plant);
            PlantHarvest.produceHarvest(plant);
        }

        for (int i = 0; i < 100; i++) {
            int tomatoAmount = PlantHarvest.getHarvestAmount(tomato);
            int cucumberAmount = PlantHarvest.getHarvestAmount(cucumber);
            int broccoliAmount = PlantHarvest.getHarvestAmount(broccoli);

            assertTrue(tomatoAmount >= 1 && tomatoAmount <= 4);
            assertTrue(cucumberAmount >= 1 && cucumberAmount <= 5);
            assertTrue(broccoliAmount >= 1 && broccoliAmount <= 2);
        }
    }

    @Test
    void harvestAmountFallsToMinimumWhenPlantConditionIsPoor() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(40);
        tomato.setMoisture(40);

        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);

        for (int i = 0; i < 30; i++) {
            assertTrue(PlantHarvest.getHarvestAmount(tomato) >= 1);
            assertTrue(PlantHarvest.getHarvestAmount(tomato) <= 2);
        }
    }

    @Test
    void harvestingClearsReadyAndFloweringFlags() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);

        assertTrue(PlantHarvest.harvest(tomato));
        assertFalse(tomato.isHarvestReady());
        assertFalse(tomato.isFlowering());
        assertFalse(PlantHarvest.harvest(tomato));
    }

    @Test
    void deadPlantWithStaleHarvestFlagCannotBeHarvested() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);
        tomato.setHealth(0);

        assertEquals(0, PlantHarvest.getHarvestAmount(tomato));
        assertFalse(PlantHarvest.harvest(tomato));
    }

    @Test
    void dryPlantWithStaleHarvestFlagCannotBeHarvested() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);
        tomato.setMoisture(0);

        assertEquals(0, PlantHarvest.getHarvestAmount(tomato));
        assertFalse(PlantHarvest.harvest(tomato));
    }

    @Test
    void harvestReadyWithoutFloweringIsRejected() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHarvestReady(true);

        assertEquals(0, PlantHarvest.getHarvestAmount(tomato));
        assertFalse(PlantHarvest.harvest(tomato));
    }

    @Test
    void cropDefinitionsDistinguishRegrowthBehavior() {
        assertTrue(PlantRegistry.get("tomato").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("cucumber").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("potato").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("carrot").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("broccoli").regrowsAfterHarvest());
    }
}
