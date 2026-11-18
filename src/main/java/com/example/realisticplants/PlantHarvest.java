
package com.realisticplants;

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
