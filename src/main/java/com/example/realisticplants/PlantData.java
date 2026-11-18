package com.realisticplants;

/**
 * Stores the individual state of a plant.
 * Designed to support different plant species.
 */
public class PlantData {

    private int moisture;
    private int health;

    private boolean flowering;
    private boolean harvestReady;

    public PlantData() {
        this.moisture = 100;
        this.health = 100;
        this.flowering = false;
        this.harvestReady = false;
    }

    public int getMoisture() {
        return moisture;
    }

    public void setMoisture(int moisture) {
        this.moisture = clamp(moisture);
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = clamp(health);
    }

    public boolean isFlowering() {
        return flowering;
    }

    public void setFlowering(boolean flowering) {
        this.flowering = flowering;
    }

    public boolean isHarvestReady() {
        return harvestReady;
    }

    public void setHarvestReady(boolean harvestReady) {
        this.harvestReady = harvestReady;
    }

    public boolean isWilted() {
        return health > 0 && health <= 30;
    }

    public boolean isDead() {
        return health <= 0;
    }

    /**
     * Determines the current plant condition.
     */
    public PlantCondition getCondition() {
        if (health <= 0) {
            return PlantCondition.DEAD;
        }

        if (health <= 15) {
            return PlantCondition.DRY;
        }

        if (health <= 30) {
            return PlantCondition.WILTING;
        }

        return PlantCondition.HEALTHY;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}

