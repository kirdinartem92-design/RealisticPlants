package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Regression tests for the shared flowering and harvest lifecycle. */
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
        PlantData[] plants = {
                new PlantData(PlantRegistry.get("tomato")),
                new PlantData(PlantRegistry.get("cucumber")),
                new PlantData(PlantRegistry.get("potato")),
                new PlantData(PlantRegistry.get("carrot")),
                new PlantData(PlantRegistry.get("beetroot")),
                new PlantData(PlantRegistry.get("zucchini")),
                new PlantData(PlantRegistry.get("eggplant")),
                new PlantData(PlantRegistry.get("bell_pepper")),
                new PlantData(PlantRegistry.get("chili_pepper")),
                new PlantData(PlantRegistry.get("broccoli"))
        };

        for (PlantData plant : plants) {
            PlantHarvest.startFlowering(plant);
            PlantHarvest.produceHarvest(plant);
            int min = plant.getDefinition().getMinHarvestAmount();
            int max = plant.getDefinition().getMaxHarvestAmount();
            for (int i = 0; i < 30; i++) {
                int amount = PlantHarvest.getHarvestAmount(plant);
                assertTrue(amount >= min && amount <= max);
            }
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
    void ripeHarvestRemainsCollectibleWhenMoistureFallsToZero() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        PlantHarvest.startFlowering(tomato);
        PlantHarvest.produceHarvest(tomato);
        tomato.setMoisture(0);
        assertEquals(1, PlantHarvest.getHarvestAmount(tomato));
        assertTrue(PlantHarvest.harvest(tomato));
    }

    @Test
    void ripeHarvestRemainsReadyAfterLifecycleNormalizationAtZeroMoisture() {
        PlantData cucumber = new PlantData(PlantRegistry.get("cucumber"));
        PlantHarvest.startFlowering(cucumber);
        PlantHarvest.produceHarvest(cucumber);
        cucumber.setMoisture(0);
        cucumber.normalizeLifecycleState();
        assertTrue(cucumber.isHarvestReady());
        assertTrue(PlantHarvest.harvest(cucumber));
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
