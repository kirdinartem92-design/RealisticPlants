package com.realisticplants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PurpleGrowLightBlock extends BaseEntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public PurpleGrowLightBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(ACTIVE, false).setValue(NORTH, false)
                .setValue(SOUTH, false).setValue(WEST, false).setValue(EAST, false));
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(ACTIVE, NORTH, SOUTH, WEST, EAST);
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos p=context.getClickedPos(); Level l=context.getLevel();
        return defaultBlockState().setValue(NORTH,l.getBlockState(p.north()).is(this))
                .setValue(SOUTH,l.getBlockState(p.south()).is(this))
                .setValue(WEST,l.getBlockState(p.west()).is(this)).setValue(EAST,l.getBlockState(p.east()).is(this));
    }
    @Override protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        BooleanProperty property=switch(direction) {
            case NORTH -> NORTH; case SOUTH -> SOUTH; case WEST -> WEST; case EAST -> EAST; default -> null;
        };
        return property==null ? super.updateShape(state,direction,neighborState,level,pos,neighborPos)
                : state.setValue(property,neighborState.is(this));
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new PurpleGrowLightBlockEntity(pos,state); }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if(level.isClientSide() || type!=PurpleGrowLightBlockEntity.TYPE.get()) return null;
        return (world,pos,blockState,blockEntity)->PurpleGrowLightBlockEntity.tick(world,pos,blockState,(PurpleGrowLightBlockEntity)blockEntity);
    }
    @Override protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
}