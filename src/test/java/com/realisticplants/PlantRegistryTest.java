package com.realisticplants;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regression tests ensuring every registered crop has a valid,
 * independent configuration for the shared plant lifecycle.
 */
class PlantRegistryTest {

    private static final Set<String> EXPECTED_CROPS = Set.of(
            "tomato",
            "cucumber",
            "potato",
            "carrot",
            "beetroot",
            "zucchini",
            "eggplant",
            "bell_pepper",
            "chili_pepper",
            "broccoli"
    );

    @Test
    void registryContainsEverySupportedCropExactlyOnce() {
        Set<String> actualIds = PlantRegistry.getAll().stream()
                .map(PlantDefinition::getId)
                .collect(Collectors.toSet());

        assertEquals(EXPECTED_CROPS, actualIds);
        assertEquals(EXPECTED_CROPS.size(), PlantRegistry.getAll().size());
    }

    @Test
    void everyCropHasValidGrowthFloweringAndHarvestSettings() {
        for (PlantDefinition definition : PlantRegistry.getAll()) {
            assertNotNull(definition);
            assertTrue(definition.getGrowthInterval() > 0, definition.getId());
            assertTrue(definition.getMoistureInterval() > 0, definition.getId());
            assertTrue(definition.getMaxGrowthStage() >= 1, definition.getId());
            assertTrue(
                    definition.getFloweringStage() <= definition.getMaxGrowthStage(),
                    definition.getId()
            );
            assertTrue(
                    definition.getRegrowthStage() <= definition.getMaxGrowthStage(),
                    definition.getId()
            );
            assertTrue(
                    definition.getMinHarvestAmount() >= 1,
                    definition.getId()
            );
            assertTrue(
                    definition.getMaxHarvestAmount()
                            >= definition.getMinHarvestAmount(),
                    definition.getId()
            );
        }
    }

    @Test
    void fruitingAndNonFruitingCropsHaveIntentionalRegrowthSettings() {
        assertTrue(PlantRegistry.get("tomato").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("cucumber").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("zucchini").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("eggplant").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("bell_pepper").regrowsAfterHarvest());
        assertTrue(PlantRegistry.get("chili_pepper").regrowsAfterHarvest());

        assertFalse(PlantRegistry.get("potato").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("carrot").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("beetroot").regrowsAfterHarvest());
        assertFalse(PlantRegistry.get("broccoli").regrowsAfterHarvest());
    }

    @Test
    void moistureAndGrowthIntervalsAreCurrentlyIndependentPerCrop() {
        for (PlantDefinition definition : PlantRegistry.getAll()) {
            assertEquals(
                    definition.getGrowthInterval(),
                    definition.getMoistureInterval(),
                    definition.getId()
            );
        }
    }
}
