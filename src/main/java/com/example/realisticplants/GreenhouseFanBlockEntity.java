package com.realisticplants;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Updates client rotor animation and delegates looping positional fan audio
 * to the client-only sound manager. The dedicated server never loads audio classes.
 */
public class GreenhouseFanBlockEntity extends BlockEntity {

    private float rotationAngle;
    private float previousRotationAngle;
    private float rotationSpeed;
    
    public float getInterpolatedRotation(float partialTick) {
        return previousRotationAngle
                + (rotationAngle - previousRotationAngle) * partialTick;
    }

    private void clientTick() {
        previousRotationAngle = rotationAngle;

        // The rotor remains visibly in motion whenever the greenhouse is powered.
        // ACTIVE still controls cooling and the positional fan sound.
        float targetSpeed = 18.0f;
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
            fan.clientTick();
            if (FMLEnvironment.getDist().isClient()) {
                RealisticPlantsClient.updateFanAudio(fan, active);
            }
        }
    }
}
