package com.realisticplants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Water tank and periodic sprinkler for the seven crops added by this mod.
 * Vanilla Minecraft crops are deliberately excluded.
 */
public class IrrigationSprinklerBlockEntity extends BlockEntity {

    private static final int CAPACITY = 4000;
    private static final int WATER_PER_MOISTURE_POINT = 10;
    private static final int MOISTURE_PER_CYCLE = 2;
    private static final int RADIUS = 3;
    private static final long CYCLE_TICKS = 100L;
    private static final int MAX_CROPS_PER_CYCLE = 12;

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<IrrigationSprinklerBlockEntity>> TYPE =
            BLOCK_ENTITY_TYPES.register(
                    "irrigation_sprinkler",
                    () -> new BlockEntityType<>(
                            IrrigationSprinklerBlockEntity::new,
                            ModBlocks.IRRIGATION_SPRINKLER.get()
                    )
            );

    private int storedWater;
    private long sprayUntil;
    private float rotationAngle;
    private float previousRotationAngle;
    private float rotationSpeed;

    public float getInterpolatedRotation(float partialTick) {
        return previousRotationAngle + (rotationAngle - previousRotationAngle) * partialTick;
    }

    private void clientTick(Level level, BlockPos pos, boolean active) {
        previousRotationAngle = rotationAngle;
        float targetSpeed = active ? 14.0f : 0.0f;
        rotationSpeed += (targetSpeed - rotationSpeed) * 0.16f;
        rotationAngle = (rotationAngle + rotationSpeed) % 360.0f;
        if (active && level.getGameTime() % 2L == 0L) {
            double angle = Math.toRadians(rotationAngle);
            for (int nozzle = 0; nozzle < 4; nozzle++) {
                double a = angle + nozzle * Math.PI / 2.0;
                double dx = Math.cos(a), dz = Math.sin(a);
                double x = pos.getX() + 0.5 + dx * 0.30;
                double y = pos.getY() + 0.68;
                double z = pos.getZ() + 0.5 + dz * 0.30;
                level.addParticle(ParticleTypes.SPLASH, x, y, z, dx * 0.075,
                        0.045 + level.getRandom().nextDouble() * 0.035, dz * 0.075);
                if (level.getRandom().nextBoolean()) {
                    level.addParticle(ParticleTypes.FALLING_WATER, x, y, z,
                            dx * 0.035, -0.015, dz * 0.035);
                }
            }
        }
    }

    private void setSpraying(boolean spraying) {
        if (level == null || level.isClientSide()) return;
        BlockState current = level.getBlockState(worldPosition);
        if (current.is(ModBlocks.IRRIGATION_SPRINKLER.get())
                && current.getValue(IrrigationSprinklerBlock.ACTIVE) != spraying) {
            level.setBlock(worldPosition, current.setValue(IrrigationSprinklerBlock.ACTIVE, spraying), 3);
        }
        if (spraying) sprayUntil = level.getGameTime() + 90L;
    }

    public IrrigationSprinklerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    public int getStoredWater() {
        return storedWater;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    public int addWater(int amount) {
        if (amount <= 0) {
            return 0;
        }

        int accepted = Math.min(amount, CAPACITY - storedWater);
        if (accepted > 0) {
            storedWater += accepted;
            setChanged();
        }
        return accepted;
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            IrrigationSprinklerBlockEntity sprinkler
    ) {
        if (sprinkler == null) return;
        if (level.isClientSide()) {
            sprinkler.clientTick(level, pos, state.getValue(IrrigationSprinklerBlock.ACTIVE));
            return;
        }
        if (sprinkler.storedWater <= 0) {
            sprinkler.setSpraying(false);
            return;
        }
        if (state.getValue(IrrigationSprinklerBlock.ACTIVE)
                && level.getGameTime() >= sprinkler.sprayUntil) sprinkler.setSpraying(false);
        if (level.getGameTime() % CYCLE_TICKS != 0) return;

        int waterUsed = 0;
        List<IrrigationTarget> targets = new ArrayList<>();
        Set<BlockPos> coveredCrops = new HashSet<>();

        // Collect every eligible crop first. Sorting by moisture means the
        // driest plants receive water before already healthier plants.
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                int distanceSquared = dx * dx + dz * dz;
                if (distanceSquared > RADIUS * RADIUS) {
                    continue;
                }

                BlockPos cropPos = pos.offset(dx, 0, dz);
                if (!level.hasChunkAt(cropPos)) {
                    continue;
                }

                BlockEntity candidate = level.getBlockEntity(cropPos);
                if (!(candidate instanceof CropPlantBlockEntity)) {
                    cropPos = pos.offset(dx, 1, dz);
                    if (!level.hasChunkAt(cropPos)) {
                        continue;
                    }
                    candidate = level.getBlockEntity(cropPos);
                }

                if (!(candidate instanceof CropPlantBlockEntity crop)
                        || !coveredCrops.add(cropPos.immutable())) {
                    continue;
                }

                PlantData plant = crop.getPlantData();
                if (plant.isDead()
                        || !IrrigationCoordination.supportsIrrigation(plant)
                        || plant.getMoisture() >= IrrigationCoordination.getTargetMoisture(plant)) {
                    continue;
                }

                targets.add(new IrrigationTarget(
                        cropPos, crop, distanceSquared, plant.getMoisture()
                ));
            }
        }

        targets.sort(
                Comparator.comparingInt(IrrigationTarget::moisture)
                        .thenComparingInt(IrrigationTarget::distanceSquared)
        );

        int wateredCrops = 0;
        for (IrrigationTarget target : targets) {
            if (wateredCrops >= MAX_CROPS_PER_CYCLE
                    || sprinkler.storedWater < WATER_PER_MOISTURE_POINT) {
                break;
            }

            PlantData plant = target.crop().getPlantData();
            int moistureNeeded = Math.min(
                    MOISTURE_PER_CYCLE,
                    IrrigationCoordination.getTargetMoisture(plant) - plant.getMoisture()
            );
            int cost = moistureNeeded * WATER_PER_MOISTURE_POINT;

            if (moistureNeeded <= 0 || sprinkler.storedWater < cost
                    || !IrrigationCoordination.tryClaim(
                            level, level.getGameTime(), target.pos())) {
                continue;
            }

            PlantGrowthManager.water(plant, moistureNeeded);
            sprinkler.storedWater -= cost;
            waterUsed += cost;
            target.crop().setChanged();
            wateredCrops++;
        }

        if (waterUsed > 0) {
            sprinkler.setChanged();
            sprinkler.setSpraying(true);
            level.playSound(
                    null,
                    pos,
                    ModSounds.SPRINKLER_SPRAY.get(),
                    SoundSource.BLOCKS,
                    IrrigationAudioBalance.getVolume(level, pos, 0.22f),
                    1.20f + level.getRandom().nextFloat() * 0.30f
            );
        }
    }

    private record IrrigationTarget(
            BlockPos pos,
            CropPlantBlockEntity crop,
            int distanceSquared,
            int moisture
    ) {
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("stored_water", storedWater);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        storedWater = Math.max(
                0,
                Math.min(CAPACITY, input.getIntOr("stored_water", 0))
        );
    }

    private static final String ITEM_WATER_KEY = "realisticplants:stored_water";

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        CompoundTag tag = new CompoundTag();
        tag.putInt(ITEM_WATER_KEY, storedWater);
        components.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        CustomData customData = components.get(DataComponents.CUSTOM_DATA);
        if (customData != null && customData.contains(ITEM_WATER_KEY)) {
            storedWater = Math.max(
                    0,
                    Math.min(CAPACITY, customData.copyTag().getIntOr(ITEM_WATER_KEY, 0))
            );
        }
    }
}
