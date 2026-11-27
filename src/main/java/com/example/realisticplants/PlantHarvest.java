package com.realisticplants;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles flowering, repeated harvesting
 * and rare vegetable discoveries.
 */
public class PlantHarvest {

    public enum RareHarvest {
        NONE,
        GOLDEN,
        MEGA
    }

    private PlantHarvest() {
    }

    public static RareHarvest rollRareHarvest() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        if (random.nextInt(2000) == 0) {
            return RareHarvest.MEGA;
        }
        if (random.nextInt(1000) == 0) {
            return RareHarvest.GOLDEN;
        }
        return RareHarvest.NONE;
    }

    public static void startFlowering(PlantData plant) {
        if (!PlantLifecycleRules.canContinueLifecycle(plant)) {
            return;
        }
        plant.setFlowering(true);
        plant.setHarvestReady(false);
    }

    public static void produceHarvest(PlantData plant) {
        if (plant == null || !plant.isFlowering()) {
            return;
        }
        if (PlantLifecycleRules.canContinueLifecycle(plant)) {
            plant.setHarvestReady(true);
        } else {
            plant.setHarvestReady(false);
        }
    }

    /**
     * Once ripe, a crop remains collectible even if it later becomes dry.
     * Only a dead plant or an invalid flowering/ready state blocks harvest.
     */
    private static boolean canHarvest(PlantData plant) {
        return plant != null
                && !plant.isDead()
                && plant.isFlowering()
                && plant.isHarvestReady();
    }

    public static int getHarvestAmount(PlantData plant) {
        if (!canHarvest(plant)) {
            return 0;
        }

        PlantDefinition definition = plant.getDefinition();
        int minAmount = definition.getMinHarvestAmount();
        int maxAmount = definition.getMaxHarvestAmount();
        int condition = Math.min(plant.getHealth(), plant.getMoisture());

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

    public static boolean harvest(PlantData plant) {
        if (!canHarvest(plant)) {
            return false;
        }
        plant.setHarvestReady(false);
        plant.setFlowering(false);
        return true;
    }
}
