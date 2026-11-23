
package com.realisticplants;

import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class CucumberPlantBlock extends CropPlantBlock {

    public static final IntegerProperty AGE = CropPlantBlock.AGE;

    public static final EnumProperty<PlantCondition> CONDITION =
            CropPlantBlock.CONDITION;

    public CucumberPlantBlock(Properties properties) {
        super(properties);
    }

    private static int getSeedAmount(PlantData data) {
        int condition = Math.min(
                data.getHealth(),
                data.getMoisture()
        );

        int chance;

        if (condition >= 75) {
            chance = 50;
        } else if (condition >= 45) {
            chance = 30;
        } else {
            chance = 10;
        }

        return ThreadLocalRandom.current().nextInt(100) < chance ? 1 : 0;
    }

    private static void giveItem(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false, Prediction.SERVER_ONLY);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof CucumberPlantBlockEntity plant)) {
            return InteractionResult.PASS;
        }

        PlantData data = plant.getPlantData();

        int maxGrowthStage =
                data.getDefinition().getMaxGrowthStage();

        if (state.getValue(AGE) < maxGrowthStage) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {

            int harvestAmount =
                    PlantHarvest.getHarvestAmount(data);

            if (harvestAmount <= 0 || !PlantHarvest.harvest(data)) {
                return InteractionResult.PASS;
            }

            giveItem(
                    player,
                    new ItemStack(
                            ModItems.CUCUMBER.get(),
                            harvestAmount
                    )
            );

            int seedAmount = getSeedAmount(data);

            if (seedAmount > 0) {
                giveItem(
                        player,
                        new ItemStack(ModItems.CUCUMBER_SEEDS.get(), 1)
                );
            }

            PlantHarvest.RareHarvest rareHarvest =
                    PlantHarvest.rollRareHarvest();

            if (rareHarvest == PlantHarvest.RareHarvest.GOLDEN) {
                giveItem(
                        player,
                        new ItemStack(ModItems.GOLDEN_VEGETABLE.get(), 1)
                );
            } else if (rareHarvest == PlantHarvest.RareHarvest.MEGA) {
                giveItem(
                        player,
                        new ItemStack(ModItems.MEGA_VEGETABLE.get(), 1)
                );
            }

            RareHarvestEffects.play(level, player, pos, rareHarvest);

            plant.resetHarvestCycle();
            plant.setChanged();
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(
            BlockPos pos,
            BlockState state) {
        return new CucumberPlantBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {

        if (level.isClientSide()) {
            return null;
        }

        if (type != CucumberPlantBlockEntity.TYPE.get()) {
            return null;
        }

        return (world, pos, blockState, blockEntity) ->
                CucumberPlantBlockEntity.tick(
                        world,
                        pos,
                        blockState,
                        (CucumberPlantBlockEntity) blockEntity
                );
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false;
    }
}
