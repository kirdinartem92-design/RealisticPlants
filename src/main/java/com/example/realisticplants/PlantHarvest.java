
package com.realisticplants;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles flowering and repeated harvesting of plants.
 */
public class PlantHarvest {

    private PlantHarvest() {
    }

    /**
     * Starts the flowering stage.
     */
    public static void startFlowering(PlantData plant) {
        if (plant == null) {
            return;
        }

        if (plant.getHealth() > 0) {
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
                && plant.getHealth() > 0
                && plant.getMoisture() > 0) {
            plant.setHarvestReady(true);
        }
    }

    /**
     * Calculates a random harvest amount
     * based on plant health and moisture.
     */
    public static int getHarvestAmount(PlantData plant) {
        if (plant == null || !plant.isHarvestReady()) {
            return 0;
        }

        int condition = Math.min(
                plant.getHealth(),
                plant.getMoisture()
        );

        int roll = ThreadLocalRandom.current().nextInt(100);

        if (condition >= 75) {
            if (roll < 35) return 2;
            if (roll < 75) return 3;
            return 4;
        }

        if (condition >= 45) {
            if (roll < 25) return 1;
            if (roll < 75) return 2;
            return 3;
        }

        return roll < 65 ? 1 : 2;
    }

    /**
     * Harvests the plant without destroying it.
     */
    public static boolean harvest(PlantData plant) {
        if (plant == null || !plant.isHarvestReady()) {
            return false;
        }

        plant.setHarvestReady(false);
        plant.setFlowering(false);

        return true;
    }
}
