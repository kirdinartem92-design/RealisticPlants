package com.realisticplants;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class IrrigationSprinklerBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public IrrigationSprinklerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ACTIVE);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new IrrigationSprinklerBlockEntity(pos, state);
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type != IrrigationSprinklerBlockEntity.TYPE.get()) return null;
        return (world, pos, blockState, blockEntity) ->
                IrrigationSprinklerBlockEntity.tick(world, pos, blockState, (IrrigationSprinklerBlockEntity) blockEntity);
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level,
            BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!itemStack.is(Items.WATER_BUCKET)) return InteractionResult.TRY_WITH_EMPTY_HAND;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IrrigationSprinklerBlockEntity sprinkler)) return InteractionResult.PASS;
        if (sprinkler.getStoredWater() >= sprinkler.getCapacity()) return InteractionResult.TRY_WITH_EMPTY_HAND;
        if (!level.isClientSide()) {
            int accepted = sprinkler.addWater(1000);
            if (accepted > 0 && !player.getAbilities().instabuild) {
                itemStack.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (itemStack.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false, Prediction.SERVER_ONLY);
            }
        }
        return InteractionResult.SUCCESS;
    }
}