
package com.realisticplants;

/**
 * Manages flowering, harvesting and repeated harvest cycles.
 */
public class PlantHarvest {

    private PlantHarvest() {
    }

    public static void startFlowering(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return;
        }

        if (plant.getHealth() >= 50
                && !plant.isFlowering()
                && !plant.isHarvestReady()) {
            plant.setFlowering(true);
        }
    }

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

    public static boolean harvest(PlantData plant) {
        if (plant == null || plant.isDead()) {
            return false;
        }

        if (!plant.isHarvestReady()) {
            return false;
        }

        plant.setHarvestReady(false);

        // The plant remains alive and can flower again.
        return true;
    }
}
