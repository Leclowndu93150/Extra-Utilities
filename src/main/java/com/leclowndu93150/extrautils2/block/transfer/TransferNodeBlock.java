package com.leclowndu93150.extrautils2.block.transfer;

import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.block.machine.MachineBlock;
import com.leclowndu93150.extrautils2.blockentity.transfer.FluidTransferNodeBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.transfer.ItemTransferNodeBlockEntity;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class TransferNodeBlock extends XUEntityBlock.FacingAll {

    public static final BooleanProperty NORTH = TransferPipeBlock.NORTH;
    public static final BooleanProperty SOUTH = TransferPipeBlock.SOUTH;
    public static final BooleanProperty EAST = TransferPipeBlock.EAST;
    public static final BooleanProperty WEST = TransferPipeBlock.WEST;
    public static final BooleanProperty UP = TransferPipeBlock.UP;
    public static final BooleanProperty DOWN = TransferPipeBlock.DOWN;

    private static final BooleanProperty[] CONNECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    private static final VoxelShape PIPE_CENTER = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape[] PIPE_ARMS = {
            Block.box(6, 0, 6, 10, 6, 10),
            Block.box(6, 10, 6, 10, 16, 10),
            Block.box(6, 6, 0, 10, 10, 6),
            Block.box(6, 6, 10, 10, 10, 16),
            Block.box(0, 6, 6, 6, 10, 10),
            Block.box(10, 6, 6, 16, 10, 10)
    };

    private static final VoxelShape[] PLATE_SHAPES = {
            Block.box(1, 0, 1, 15, 3, 15),
            Block.box(1, 13, 1, 15, 16, 15),
            Block.box(1, 1, 0, 15, 15, 3),
            Block.box(1, 1, 13, 15, 15, 16),
            Block.box(0, 1, 1, 3, 15, 15),
            Block.box(13, 1, 1, 16, 15, 15)
    };

    private final NodeType nodeType;
    private final boolean retrieval;

    public TransferNodeBlock(Properties props, NodeType nodeType, boolean retrieval) {
        super(props);
        this.nodeType = nodeType;
        this.retrieval = retrieval;
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN);
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public boolean isRetrieval() {
        return retrieval;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction facing = ctx.getClickedFace().getOpposite();
        BlockState state = defaultBlockState().setValue(FACING, facing);
        if (ctx.getLevel() instanceof Level lv) {
            state = computeConnections(lv, ctx.getClickedPos(), state, facing);
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (level instanceof Level lv) {
            return computeConnections(lv, pos, state, state.getValue(FACING));
        }
        return state;
    }

    private BlockState computeConnections(Level level, BlockPos pos, BlockState state, Direction facing) {
        for (Direction dir : Direction.values()) {
            boolean connected = dir != facing && canConnectTo(level, pos, dir);
            state = state.setValue(CONNECTIONS[dir.ordinal()], connected);
        }
        return state;
    }

    private boolean canConnectTo(Level level, BlockPos pos, Direction dir) {
        BlockPos neighbor = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighbor);
        return neighborState.getBlock() instanceof TransferPipeBlock
                || neighborState.getBlock() instanceof TransferNodeBlock;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        BlockState newState = computeConnections(level, pos, state, state.getValue(FACING));
        if (!newState.equals(state)) {
            level.setBlock(pos, newState, 3);
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        VoxelShape shape = Shapes.or(PIPE_CENTER, PLATE_SHAPES[facing.ordinal()]);
        for (int d = 0; d < 6; d++) {
            if (state.getValue(CONNECTIONS[d])) {
                shape = Shapes.or(shape, PIPE_ARMS[d]);
            }
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return switch (nodeType) {
            case ITEMS -> new ItemTransferNodeBlockEntity(pos, state);
            case FLUIDS -> new FluidTransferNodeBlockEntity(pos, state);
            default -> null;
        };
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return switch (nodeType) {
            case ITEMS -> (BlockEntityTicker<T>) XUEntityBlock.createTicker(type, ModBlockEntities.ITEM_TRANSFER_NODE.get(), ItemTransferNodeBlockEntity::tick);
            case FLUIDS -> (BlockEntityTicker<T>) XUEntityBlock.createTicker(type, ModBlockEntities.FLUID_TRANSFER_NODE.get(), FluidTransferNodeBlockEntity::tick);
            default -> null;
        };
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider menu) {
            player.openMenu(menu, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof ServerPlayer sp && level.getBlockEntity(pos) instanceof MachineBlock.IGpMachine gp) {
            int freq = GpManager.INSTANCE.assignFrequency(sp);
            gp.setOwnerFrequency(freq);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof MachineBlock.IDroppableInventory droppable) {
                for (IItemHandler handler : droppable.getDroppableInventories()) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
