package com.realisticplants;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles flowering, repeated harvesting
 * and rare vegetable discoveries.
 */
public class PlantHarvest {

    /**
     * Possible rare harvest discoveries.
     */
    public enum RareHarvest {
        NONE,
        GOLDEN,
        MEGA
    }

    private PlantHarvest() {
    }

    /**
     * Rolls for a rare vegetable.
     *
     * Mega vegetable: 1 in 2000.
     * Golden vegetable: 1 in 1000.
     *
     * Only one rare vegetable can be found
     * during a single harvest.
     */
    public static RareHarvest rollRareHarvest() {

        ThreadLocalRandom random =
                ThreadLocalRandom.current();

        // Check the rarer mega vegetable first.
        if (random.nextInt(2000) == 0) {
            return RareHarvest.MEGA;
        }

        // Then check the golden vegetable.
        if (random.nextInt(1000) == 0) {
            return RareHarvest.GOLDEN;
        }

        return RareHarvest.NONE;
    }

    /**
     * Starts the flowering stage.
     */
    public static void startFlowering(PlantData plant) {
        if (plant == null) {
            return;
        }

        if (!plant.isDead()) {
            plant.setFlowering(true);
            plant.setHarvestReady(false);
        }
    }

    /**
     * Makes the harvest ready after flowering.
     */
    public static void produceHarvest(PlantData plant) {
        if (plant == null) {
            return;
        }

        if (plant.isFlowering()
                && !plant.isDead()
                && plant.getMoisture() > 0) {
            plant.setHarvestReady(true);
        }
    }

    /**
     * Checks whether the plant currently has a valid harvest.
     */
    private static boolean canHarvest(PlantData plant) {
        return plant != null
                && !plant.isDead()
                && plant.getMoisture() > 0
                && plant.isFlowering()
                && plant.isHarvestReady();
    }

    /**
     * Calculates harvest amount using the individual
     * harvest settings of the plant species.
     */
    public static int getHarvestAmount(PlantData plant) {

        if (!canHarvest(plant)) {
            return 0;
        }

        PlantDefinition definition =
                plant.getDefinition();

        int minAmount = definition.getMinHarvestAmount();
        int maxAmount = definition.getMaxHarvestAmount();

        int condition = Math.min(
                plant.getHealth(),
                plant.getMoisture()
        );

        int effectiveMax;

        if (condition >= 75) {

            effectiveMax = maxAmount;

        } else if (condition >= 45) {

            effectiveMax = minAmount
                    + (maxAmount - minAmount + 1) / 2;

        } else {

            effectiveMax = minAmount;
        }

        return ThreadLocalRandom.current().nextInt(
                minAmount,
                effectiveMax + 1
        );
    }

    /**
     * Harvests the plant without destroying it.
     */
    public static boolean harvest(PlantData plant) {

        if (!canHarvest(plant)) {
            return false;
        }

        plant.setHarvestReady(false);
        plant.setFlowering(false);

        return true;
    }
}
