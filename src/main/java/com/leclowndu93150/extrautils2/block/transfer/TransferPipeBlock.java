package com.leclowndu93150.extrautils2.block.transfer;

import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

public class TransferPipeBlock extends Block {

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final IntegerProperty BLOCKED = IntegerProperty.create("blocked", 0, 63);

    private static final BooleanProperty[] CONNECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    private static final VoxelShape CENTER = Block.box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape[] ARM_SHAPES = {
            Block.box(6, 0, 6, 10, 6, 10),
            Block.box(6, 10, 6, 10, 16, 10),
            Block.box(6, 6, 0, 10, 10, 6),
            Block.box(6, 6, 10, 10, 10, 16),
            Block.box(0, 6, 6, 6, 10, 10),
            Block.box(10, 6, 6, 16, 10, 10)
    };

    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64];

    static {
        for (int i = 0; i < 64; i++) {
            VoxelShape shape = CENTER;
            for (int d = 0; d < 6; d++) {
                if ((i & (1 << d)) != 0) {
                    shape = Shapes.or(shape, ARM_SHAPES[d]);
                }
            }
            SHAPE_CACHE[i] = shape;
        }
    }

    public TransferPipeBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any()
                .setValue(NORTH, false).setValue(SOUTH, false)
                .setValue(EAST, false).setValue(WEST, false)
                .setValue(UP, false).setValue(DOWN, false)
                .setValue(BLOCKED, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN, BLOCKED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return computeConnections(ctx.getLevel(), ctx.getClickedPos(), 0);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos) {
        if (level instanceof Level lv) {
            return computeConnections(lv, pos, state.getValue(BLOCKED));
        }
        return state;
    }

    private BlockState computeConnections(Level level, BlockPos pos, int blocked) {
        BlockState state = defaultBlockState().setValue(BLOCKED, blocked);
        for (Direction dir : Direction.values()) {
            boolean connected = (blocked & (1 << dir.ordinal())) == 0 && canConnectTo(level, pos, dir);
            state = state.setValue(CONNECTIONS[dir.ordinal()], connected);
        }
        return state;
    }

    public static boolean canConnectTo(Level level, BlockPos pos, Direction dir) {
        BlockPos neighbor = pos.relative(dir);
        BlockState neighborState = level.getBlockState(neighbor);
        if (neighborState.getBlock() instanceof TransferPipeBlock) return true;
        if (neighborState.getBlock() instanceof TransferNodeBlock) return true;
        if (level.getCapability(Capabilities.ItemHandler.BLOCK, neighbor, dir.getOpposite()) != null) return true;
        return level.getCapability(Capabilities.FluidHandler.BLOCK, neighbor, dir.getOpposite()) != null;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(ModItems.WRENCH.get())) {
            if (!level.isClientSide) {
                Direction face = hit.getDirection();
                toggleBlocked(level, pos, face);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int bits = 0;
        for (int d = 0; d < 6; d++) {
            if (state.getValue(CONNECTIONS[d])) bits |= (1 << d);
        }
        return SHAPE_CACHE[bits];
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        BlockState newState = computeConnections(level, pos, state.getValue(BLOCKED));
        if (!newState.equals(state)) {
            level.setBlock(pos, newState, 3);
        }
    }

    public static void toggleBlocked(Level level, BlockPos pos, Direction face) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof TransferPipeBlock pipe)) return;
        int blocked = state.getValue(BLOCKED);
        blocked ^= (1 << face.ordinal());
        BlockState newState = pipe.computeConnections(level, pos, blocked);
        level.setBlock(pos, newState, 3);
    }
}
