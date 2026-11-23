
package com.realisticplants;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RareVegetableItem extends Item {

    public enum Variant {
        GOLDEN,
        MEGA
    }

    private final Variant variant;

    public RareVegetableItem(Properties properties, Variant variant) {
        super(properties);
        this.variant = variant;
    }

    @Override
    public ItemStack finishUsingItem(
            ItemStack stack,
            Level level,
            LivingEntity entity
    ) {

        ItemStack result =
                super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {

            if (variant == Variant.GOLDEN) {

                // Golden vegetable:
                // Regeneration for 12 seconds.
                entity.addEffect(
                        new MobEffectInstance(
                                MobEffects.REGENERATION,
                                20 * 12,
                                0
                        )
                );

                // Absorption for 20 seconds.
                entity.addEffect(
                        new MobEffectInstance(
                                MobEffects.ABSORPTION,
                                20 * 20,
                                0
                        )
                );

            } else if (variant == Variant.MEGA) {

                // Mega vegetable:
                // Regeneration II for 20 seconds.
                entity.addEffect(
                        new MobEffectInstance(
                                MobEffects.REGENERATION,
                                20 * 20,
                                1
                        )
                );

                // Resistance for 8 seconds.
                entity.addEffect(
                        new MobEffectInstance(
                                MobEffects.RESISTANCE,
                                20 * 8,
                                0
                        )
                );

                // Absorption II for 30 seconds.
                entity.addEffect(
                        new MobEffectInstance(
                                MobEffects.ABSORPTION,
                                20 * 30,
                                1
                        )
                );
            }
        }

        return result;
    }
}
