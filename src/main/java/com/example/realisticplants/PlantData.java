package com.realisticplants;

import java.util.Objects;

/**
 * Stores the individual state of a plant.
 * Each plant is linked to its species definition.
 */
public class PlantData {

    private final PlantDefinition definition;

    private int moisture;
    private int health;
    private double growthProgress;
    private boolean flowering;
    private boolean harvestReady;

    public PlantData() {
        this(PlantRegistry.get("tomato"));
    }

    public PlantData(PlantDefinition definition) {
        this.definition = Objects.requireNonNull(
                definition,
                "Plant definition cannot be null"
        );
        this.moisture = 100;
        this.health = 100;
        this.growthProgress = 0.0;
        this.flowering = false;
        this.harvestReady = false;
    }

    public PlantDefinition getDefinition() {
        return definition;
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

    public double getGrowthProgress() {
        return growthProgress;
    }

    public void setGrowthProgress(double growthProgress) {
        if (!Double.isFinite(growthProgress)) {
            this.growthProgress = 0.0;
            return;
        }
        this.growthProgress = Math.max(0.0, growthProgress);
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

    /**
     * A dead plant cannot remain flowering or harvest-ready. A ripe harvest
     * stays ready while the plant is alive, even if its moisture reaches zero.
     */
    public void normalizeLifecycleState() {
        if (isDead()) {
            flowering = false;
            harvestReady = false;
            return;
        }
        if (!flowering) {
            harvestReady = false;
        }
    }

    public boolean isWilted() {
        return !isDead() && (health <= 30 || moisture <= 30);
    }

    public boolean isDead() {
        return health <= 0;
    }

    public PlantCondition getCondition() {
        if (health <= 0) {
            return PlantCondition.DEAD;
        }
        if (health <= 15 || moisture <= 15) {
            return PlantCondition.DRY;
        }
        if (health <= 30 || moisture <= 30) {
            return PlantCondition.WILTING;
        }
        return PlantCondition.HEALTHY;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
