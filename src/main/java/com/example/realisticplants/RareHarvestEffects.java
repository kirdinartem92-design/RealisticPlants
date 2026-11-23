
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class RareHarvestEffects {

    private RareHarvestEffects() {
    }

    public static void play(
            Level level,
            Player player,
            BlockPos pos,
            PlantHarvest.RareHarvest discovery
    ) {
        if (level.isClientSide()
                || player == null
                || discovery == null
                || discovery == PlantHarvest.RareHarvest.NONE) {
            return;
        }

        if (discovery == PlantHarvest.RareHarvest.GOLDEN) {

            player.sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.golden_found"
                    )
            );

            level.playSound(
                    null,
                    pos,
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundSource.PLAYERS,
                    0.8F,
                    1.2F
            );

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5D,
                        pos.getY() + 1.0D,
                        pos.getZ() + 0.5D,
                        18,
                        0.35D,
                        0.45D,
                        0.35D,
                        0.05D
                );
            }

        } else if (discovery == PlantHarvest.RareHarvest.MEGA) {

            player.sendSystemMessage(
                    Component.translatable(
                            "message.realisticplants.mega_found"
                    )
            );

            level.playSound(
                    null,
                    pos,
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundSource.PLAYERS,
                    1.0F,
                    0.75F
            );

            if (level instanceof ServerLevel serverLevel) {

                serverLevel.sendParticles(
                        ParticleTypes.END_ROD,
                        pos.getX() + 0.5D,
                        pos.getY() + 1.0D,
                        pos.getZ() + 0.5D,
                        36,
                        0.5D,
                        0.65D,
                        0.5D,
                        0.08D
                );

                serverLevel.sendParticles(
                        ParticleTypes.TOTEM_OF_UNDYING,
                        pos.getX() + 0.5D,
                        pos.getY() + 1.0D,
                        pos.getZ() + 0.5D,
                        16,
                        0.4D,
                        0.5D,
                        0.4D,
                        0.05D
                );
            }
        }
    }
}
