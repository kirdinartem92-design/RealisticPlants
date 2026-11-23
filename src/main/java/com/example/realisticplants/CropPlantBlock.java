
package com.realisticplants;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Prediction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class CropPlantBlock extends Block implements EntityBlock {

    public static final IntegerProperty AGE =
            IntegerProperty.create("age", 0, 6);

    public static final EnumProperty<PlantCondition> CONDITION =
            EnumProperty.create("condition", PlantCondition.class);

    /**
     * Selection shape follows the visible height of each growth stage.
     * The plant remains non-solid; this only makes tall foliage easier to target.
     */
    private static VoxelShape getPlantShape(int age) {
        int height = switch (age) {
            case 0 -> 3;
            case 1 -> 5;
            case 2 -> 9;
            case 3 -> 13;
            case 4 -> 17;
            case 5 -> 21;
            default -> 24;
        };

        return Block.box(1, 0, 1, 15, height, 15);
    }

    protected CropPlantBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(AGE, 0)
                        .setValue(CONDITION, PlantCondition.HEALTHY)
        );
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, CONDITION);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return getPlantShape(state.getValue(AGE));
    }

    @Override
    protected VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos) {
        return level.getBlockState(pos.below()).is(Blocks.FARMLAND);
    }

    /**
     * Общая обработка взаимодействия с растениями.
     */
    @Override
    protected InteractionResult useItemOn(
            ItemStack itemStack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult) {

        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (!(blockEntity instanceof CropPlantBlockEntity plant)) {
            return InteractionResult.PASS;
        }

        PlantData data = plant.getPlantData();
        PlantDefinition definition = data.getDefinition();

        // Костная мука ускоряет рост.
        if (itemStack.is(Items.BONE_MEAL)) {

            int age = state.getValue(AGE);
            int maxGrowthStage = definition.getMaxGrowthStage();
            int floweringStage = definition.getFloweringStage();

            if (age >= maxGrowthStage
                    || data.isDead()
                    || data.getHealth() <= 30
                    || data.getMoisture() <= 0) {
                return InteractionResult.PASS;
            }

            if (!level.isClientSide()) {
                int nextAge = Math.min(
                        age + 1,
                        maxGrowthStage
                );

                level.setBlock(
                        pos,
                        state.setValue(AGE, nextAge),
                        3
                );

                if (nextAge == floweringStage
                        && !data.isFlowering()) {
                    PlantHarvest.startFlowering(data);
                }

                if (nextAge == maxGrowthStage) {
                    PlantHarvest.produceHarvest(data);
                }

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }

                plant.setChanged();
            }

            return InteractionResult.SUCCESS;
        }

        // Полив обычным водным зельем: влажность +40.
        if (itemStack.is(Items.POTION)) {

            var potionContents =
                    itemStack.get(DataComponents.POTION_CONTENTS);

            if (potionContents == null
                    || !potionContents.is(Potions.WATER)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }

            if (!level.isClientSide()) {
                PlantGrowthManager.water(data, 40);
                plant.setChanged();

                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);

                    ItemStack bottle =
                            new ItemStack(Items.GLASS_BOTTLE);

                    if (itemStack.isEmpty()) {
                        player.setItemInHand(hand, bottle);
                    } else {
                        player.getInventory().placeItemBackInInventory(
                                bottle,
                                Prediction.SERVER_ONLY
                        );
                    }
                }
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }
}
