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
        if (level.isClientSide()
                || fan == null
                || !state.getValue(GreenhouseFanBlock.ACTIVE)
                || Math.floorMod(level.getGameTime(), SOUND_INTERVAL) != 0) {
            return;
        }

        level.playSound(
                null,
                pos,
                SoundEvents.BLOCK_BEACON_AMBIENT,
                SoundSource.BLOCKS,
                0.16f,
                0.72f
        );
    }
}
