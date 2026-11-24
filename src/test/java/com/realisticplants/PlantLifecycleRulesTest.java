package com.realisticplants;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests for shared health and moisture lifecycle requirements.
 */
class PlantLifecycleRulesTest {

    @Test
    void floweringStartsOnlyAfterARealGrowthTransitionReachesItsStage() {
        assertFalse(PlantLifecycleRules.shouldStartFlowering(3, 4, false));
        assertTrue(PlantLifecycleRules.shouldStartFlowering(4, 4, false));
        assertTrue(PlantLifecycleRules.shouldStartFlowering(5, 4, false));
        assertFalse(PlantLifecycleRules.shouldStartFlowering(5, 4, true));
    }

    @Test
    void lifecycleRequiresMinimumHealth() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(29);

        assertFalse(PlantLifecycleRules.canContinueLifecycle(tomato));

        tomato.setHealth(30);
        assertTrue(PlantLifecycleRules.canContinueLifecycle(tomato));
    }

    @Test
    void lifecycleUsesEachCropsMinimumMoisture() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setMoisture(19);

        assertFalse(PlantLifecycleRules.canContinueLifecycle(tomato));

        tomato.setMoisture(20);
        assertTrue(PlantLifecycleRules.canContinueLifecycle(tomato));

        PlantData broccoli = new PlantData(PlantRegistry.get("broccoli"));
        broccoli.setMoisture(29);
        assertFalse(PlantLifecycleRules.canContinueLifecycle(broccoli));

        broccoli.setMoisture(30);
        assertTrue(PlantLifecycleRules.canContinueLifecycle(broccoli));
    }

    @Test
    void nullAndDeadPlantsCannotContinueLifecycle() {
        assertFalse(PlantLifecycleRules.canContinueLifecycle(null));

        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(0);

        assertFalse(PlantLifecycleRules.canContinueLifecycle(tomato));
    }

    @Test
    void floweringAndHarvestRequireValidLifecycleConditions() {
        PlantData tomato = new PlantData(PlantRegistry.get("tomato"));
        tomato.setHealth(29);

        PlantHarvest.startFlowering(tomato);
        assertFalse(tomato.isFlowering());

        tomato.setHealth(100);
        PlantHarvest.startFlowering(tomato);
        assertTrue(tomato.isFlowering());

        tomato.setMoisture(19);
        PlantHarvest.produceHarvest(tomato);
        assertFalse(tomato.isHarvestReady());

        tomato.setMoisture(20);
        PlantHarvest.produceHarvest(tomato);
        assertTrue(tomato.isHarvestReady());
    }
}
