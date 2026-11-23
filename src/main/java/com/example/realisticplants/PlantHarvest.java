
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
     * Calculates harvest amount using the individual
     * harvest settings of the plant species.
     */
    public static int getHarvestAmount(PlantData plant) {

        if (plant == null || !plant.isHarvestReady()) {
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

            // Healthy plants can produce their full harvest.
            effectiveMax = maxAmount;

        } else if (condition >= 45) {

            // Weakened plants have a reduced maximum harvest.
            effectiveMax = minAmount
                    + (maxAmount - minAmount + 1) / 2;

        } else {

            // Poorly maintained plants produce the minimum.
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

        if (plant == null || !plant.isHarvestReady()) {
            return false;
        }

        plant.setHarvestReady(false);
        plant.setFlowering(false);

        return true;
    }
}
