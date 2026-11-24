package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Adds a quiet positional operating hum to active greenhouse fans.
 * The sound is emitted server-side so every nearby player hears it with
 * Minecraft's normal block-volume and distance attenuation.
 */
public class GreenhouseFanBlockEntity extends BlockEntity {

    private static final long SOUND_INTERVAL = 40L;

    private float rotationAngle;
    private float previousRotationAngle;
    private float rotationSpeed;
    private float previousRotationSpeed;

    public float getInterpolatedRotation(float partialTick) {
        return previousRotationAngle
                + (rotationAngle - previousRotationAngle) * partialTick;
    }

    private void clientTick(boolean active) {
        previousRotationAngle = rotationAngle;
        previousRotationSpeed = rotationSpeed;

        float targetSpeed = active ? 18.0f : 0.0f;
        rotationSpeed += (targetSpeed - rotationSpeed) * 0.18f;
        rotationAngle = (rotationAngle + rotationSpeed) % 360.0f;
    }

    private static final DeferredRegister<BlockEntityType<?>> TYPES =
            DeferredRegister.create(
                    Registries.BLOCK_ENTITY_TYPE,
                    RealisticPlants.MOD_ID
            );

    public static final Supplier<BlockEntityType<GreenhouseFanBlockEntity>> TYPE =
            TYPES.register("greenhouse_fan", () ->
                    new BlockEntityType<>(
                            GreenhouseFanBlockEntity::new,
                            ModBlocks.GREENHOUSE_FAN.get()
                    ));

    public GreenhouseFanBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
    }

    public static void tick(
            Level level,
            BlockPos pos,
            BlockState state,
            GreenhouseFanBlockEntity fan
    ) {
        if (fan == null) {
            return;
        }

        boolean active = state.getValue(GreenhouseFanBlock.ACTIVE);
        if (level.isClientSide()) {
            fan.clientTick(active);
            return;
        }

        if (!active
                || Math.floorMod(level.getGameTime(), SOUND_INTERVAL) != 0) {
            return;
        }

        level.playSound(
                null,
                pos,
                SoundEvents.WEATHER_RAIN,
                SoundSource.BLOCKS,
                0.08f,
                0.55f
        );
    }
}
