package com.leclowndu93150.extrautils2.block.machine;

import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.power.GpManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class MachineBlock extends XUEntityBlock.Facing {

    public static final BooleanProperty ACTIVE = BlockStateProperties.LIT;

    public MachineBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any()
                .setValue(ACTIVE, false)
                .setValue(FACING, net.minecraft.core.Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof ServerPlayer sp && level.getBlockEntity(pos) instanceof IGpMachine gp) {
            int freq = GpManager.INSTANCE.assignFrequency(sp);
            gp.setOwnerFrequency(freq);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof MenuProvider menu) {
            player.openMenu(menu, buf -> buf.writeBlockPos(pos));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof IDroppableInventory droppable) {
                for (IItemHandler handler : droppable.getDroppableInventories()) {
                    for (int i = 0; i < handler.getSlots(); i++) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(i));
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    public interface IGpMachine {
        void setOwnerFrequency(int freq);
    }

    public interface IDroppableInventory {
        IItemHandler[] getDroppableInventories();
    }
}
