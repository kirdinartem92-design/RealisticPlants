package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreenhouseControlPanelMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 31;

    public static final int PLANTS = 0;
    public static final int MOISTURE = 1;
    public static final int HEALTH = 2;
    public static final int SKY_LIGHT = 3;
    public static final int BLOCK_LIGHT = 4;
    public static final int SPRINKLERS = 5;
    public static final int DRIPPERS = 6;
    public static final int GROW_LIGHTS = 7;
    public static final int FANS = 8;
    public static final int STORED_WATER = 9;
    public static final int WATER_CAPACITY = 10;
    public static final int NEEDS_CARE = 11;
    public static final int DEAD = 12;
    public static final int WILTING = 13;
    public static final int DRY = 14;
    public static final int LOW_LIGHT = 15;
    public static final int WEAK = 16;
    public static final int OVERHEATED = 17;
    public static final int COLD = 18;
    public static final int DROUGHT = 19;
    public static final int TEMPERATURE_MILLI = 20;
    public static final int GROWTH_PERMILLE = 21;
    public static final int STORED_WATER_HIGH = 22;
    public static final int WATER_CAPACITY_HIGH = 23;
    public static final int TOMATOES = 24;
    public static final int CUCUMBERS = 25;
    public static final int ZUCCHINIS = 26;
    public static final int EGGPLANTS = 27;
    public static final int BELL_PEPPERS = 28;
    public static final int CHILI_PEPPERS = 29;
    public static final int BROCCOLI = 30;

    private final ContainerData data;
    private final ContainerLevelAccess access;
    private final Level level;
    private final BlockPos panelPos;
    private long lastRefreshTick = Long.MIN_VALUE;

    /** Client-side constructor. Values are populated by the server menu. */
    public GreenhouseControlPanelMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.NULL, null, null);
    }

    /** Server-side constructor bound to the actual panel position. */
    public GreenhouseControlPanelMenu(int containerId, Inventory inventory, Level level, BlockPos panelPos) {
        this(containerId, inventory, new SimpleContainerData(DATA_COUNT),
                ContainerLevelAccess.create(level, panelPos), level, panelPos);
        refreshData();
    }

    private GreenhouseControlPanelMenu(
            int containerId,
            Inventory inventory,
            ContainerData data,
            ContainerLevelAccess access,
            Level level,
            BlockPos panelPos
    ) {
        super(ModMenus.GREENHOUSE_CONTROL_PANEL.get(), containerId);
        checkContainerDataCount(data, DATA_COUNT);
        this.data = data;
        this.access = access;
        this.level = level;
        this.panelPos = panelPos;
        addDataSlots(data);
    }

    public int getValue(int index) {
        return data.get(index);
    }

    @Override
    public void broadcastChanges() {
        if (level != null && !level.isClientSide() && panelPos != null) {
            long now = level.getGameTime();
            if (lastRefreshTick == Long.MIN_VALUE || now - lastRefreshTick >= 20) {
                refreshData();
                lastRefreshTick = now;
            }
        }
        super.broadcastChanges();
    }

    private void refreshData() {
        if (level == null || panelPos == null || !level.hasChunkAt(panelPos)) {
            return;
        }

        GreenhouseInspectionData snapshot = GreenhouseInspectionData.capture(level, panelPos);
        int[] values = {
                snapshot.plantCount(),
                snapshot.averageMoisture(),
                snapshot.averageHealth(),
                snapshot.averageSkyLight(),
                snapshot.averageBlockLight(),
                snapshot.sprinklerCount(),
                snapshot.dripIrrigationCount(),
                snapshot.purpleGrowLightCount(),
                snapshot.fanCount(),
                snapshot.storedWater(),
                snapshot.totalWaterCapacity(),
                snapshot.plantsNeedingCare(),
                snapshot.deadPlants(),
                snapshot.wiltingPlants(),
                snapshot.dryPlants(),
                snapshot.lowLightPlants(),
                snapshot.weakPlants(),
                snapshot.overheatedPlants(),
                snapshot.coldPlants(),
                snapshot.droughtPlants(),
                Math.round(snapshot.averageTemperature() * 1000.0f),
                (int) Math.round(snapshot.averageGrowthMultiplier() * 1000.0),
                0, 0
        };

        for (int i = 0; i < values.length; i++) {
            if (i == STORED_WATER || i == WATER_CAPACITY) {
                continue;
            }
            // Other dashboard values are bounded and fit in a signed short.
            data.set(i, Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, values[i])));
        }

        // Split water totals into two 16-bit words so large greenhouses do not
        // lose their real reservoir totals when ContainerData synchronizes them.
        setInt32(STORED_WATER, STORED_WATER_HIGH, snapshot.storedWater());
        setInt32(WATER_CAPACITY, WATER_CAPACITY_HIGH, snapshot.totalWaterCapacity());
        data.set(TOMATOES, snapshot.tomatoCount());
        data.set(CUCUMBERS, snapshot.cucumberCount());
        data.set(ZUCCHINIS, snapshot.zucchiniCount());
        data.set(EGGPLANTS, snapshot.eggplantCount());
        data.set(BELL_PEPPERS, snapshot.bellPepperCount());
        data.set(CHILI_PEPPERS, snapshot.chiliPepperCount());
        data.set(BROCCOLI, snapshot.broccoliCount());
    }

    private void setInt32(int lowIndex, int highIndex, int value) {
        data.set(lowIndex, value & 0xFFFF);
        data.set(highIndex, value >>> 16);
    }

    /** Reassembles a 32-bit value from the two synchronized 16-bit words. */
    public int getInt32(int lowIndex, int highIndex) {
        return (data.get(highIndex) << 16) | (data.get(lowIndex) & 0xFFFF);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.GREENHOUSE_CONTROL_PANEL.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}
