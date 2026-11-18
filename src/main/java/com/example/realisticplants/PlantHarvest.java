
package com.realisticplants;

/**
 * Manages flowering and harvesting.
 */
public class PlantHarvest {

    private PlantHarvest() {
    }

    /**
     * Starts flowering when the plant is healthy.
     */
    public static void startFlowering(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return;
        }

        if (plant.getHealth() >= 50) {
            plant.setFlowering(true);
        }
    }

    /**
     * Produces harvest after flowering.
     */
    public static void produceHarvest(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return;
        }

        if (plant.isFlowering()
                && plant.getHealth() >= 40
                && plant.getMoisture() >= 30) {

            plant.setHarvestReady(true);
            plant.setFlowering(false);
        }
    }

    /**
     * Collects the harvest.
     */
    public static boolean harvest(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return false;
        }

        if (!plant.isHarvestReady()) {
            return false;
        }

        plant.setHarvestReady(false);
        return true;
    }
}
