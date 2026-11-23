package com.realisticplants;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Configurable crop block. Species-specific behavior is supplied by its ID
 * and produce/seed item suppliers; growth itself remains in CropPlantBlockEntity.
 */
public class NewCropPlantBlock extends CropPlantBlock {

    private final String plantId;
    private final Supplier<? extends Item> produceItem;
    private final Supplier<? extends Item> seedsItem;

    public NewCropPlantBlock(
            Properties properties,
            String plantId,
            Supplier<? extends Item> produceItem,
            Supplier<? extends Item> seedsItem
    ) {
        super(properties);
        this.plantId = plantId;
        this.produceItem = produceItem;
        this.seedsItem = seedsItem;
    }

    private static void giveItem(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false, Prediction.SERVER_ONLY);
        }
    }

    private static boolean producesSeed(PlantData data) {
        int condition = Math.min(data.getHealth(), data.getMoisture());
        int chance = condition >= 75 ? 50 : condition >= 45 ? 30 : 10;
        return ThreadLocalRandom.current().nextInt(100) < chance;
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult
    ) {
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof NewCropPlantBlockEntity plant)) {
            return InteractionResult.PASS;
        }

        PlantData data = plant.getPlantData();
        int maxGrowthStage = data.getDefinition().getMaxGrowthStage();

        if (state.getValue(AGE) < maxGrowthStage
                || !data.isHarvestReady()) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            int harvestAmount = PlantHarvest.getHarvestAmount(data);

            if (harvestAmount <= 0 || !PlantHarvest.harvest(data)) {
                return InteractionResult.PASS;
            }

            giveItem(player, new ItemStack(produceItem.get(), harvestAmount));

            if (producesSeed(data)) {
                giveItem(player, new ItemStack(seedsItem.get(), 1));
            }

            PlantHarvest.RareHarvest rareHarvest =
                    PlantHarvest.rollRareHarvest();

            if (rareHarvest == PlantHarvest.RareHarvest.GOLDEN) {
                giveItem(player, new ItemStack(ModItems.GOLDEN_VEGETABLE.get(), 1));
            } else if (rareHarvest == PlantHarvest.RareHarvest.MEGA) {
                giveItem(player, new ItemStack(ModItems.MEGA_VEGETABLE.get(), 1));
            }

            RareHarvestEffects.play(level, player, pos, rareHarvest);

            if (data.getDefinition().regrowsAfterHarvest()) {
                plant.resetHarvestCycle();
                plant.setChanged();
            } else {
                level.removeBlock(pos, false);
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NewCropPlantBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide() || type != NewCropPlantBlockEntity.TYPE.get()) {
            return null;
        }

        return (world, pos, blockState, blockEntity) ->
                NewCropPlantBlockEntity.tick(
                        world,
                        pos,
                        blockState,
                        (NewCropPlantBlockEntity) blockEntity
                );
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return false;
    }

    public String getPlantId() {
        return plantId;
    }
}
