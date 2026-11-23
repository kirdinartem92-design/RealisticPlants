
package com.realisticplants;

/**
 * Manages plant seed production.
 */
public class PlantSeeds {

    private PlantSeeds() {
    }

    /**
     * Calculates the number of seeds from a harvest.
     */
    public static int calculateSeeds(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return 0;
        }

        if (plant.getHealth() >= 80) {
            return 3;
        }

        if (plant.getHealth() >= 50) {
            return 2;
        }

        if (plant.getHealth() >= 30) {
            return 1;
        }

        return 0;
    }

    /**
     * Checks whether a plant can produce seeds.
     */
    public static boolean canProduceSeeds(PlantData plant) {
        return plant != null
                && !plant.isDead()
                && plant.isHarvestReady();
    }
}
