package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.gui.CreativeChestMenu;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class CreativeChestBlockEntity extends XUBlockEntity implements MenuProvider, LidBlockEntity {

    private ItemStack heldStack = ItemStack.EMPTY;
    private final ChestLidController chestLidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
            signalOpenCount(level, pos, state, newCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof CreativeChestMenu menu && menu.tile == CreativeChestBlockEntity.this;
        }
    };

    private final IItemHandler handler = new IItemHandler() {
        @Override public int getSlots() { return 1; }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (heldStack.isEmpty()) return ItemStack.EMPTY;
            ItemStack display = heldStack.copy();
            display.setCount(display.getMaxStackSize());
            return display;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (heldStack.isEmpty()) return ItemStack.EMPTY;
            ItemStack result = heldStack.copy();
            result.setCount(Math.min(amount, result.getMaxStackSize()));
            return result;
        }

        @Override public int getSlotLimit(int slot) { return 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return false; }
    };

    public CreativeChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_CHEST.get(), pos, state);
    }

    public ItemStack getHeldStack() { return heldStack; }

    public void setHeldStack(ItemStack stack) {
        this.heldStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);
        setChanged();
        sync();
    }

    public IItemHandler getHandler() { return handler; }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, CreativeChestBlockEntity be) {
        be.chestLidController.tickLid();
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    public void startOpen(Player player) {
        if (level != null && !remove && !player.isSpectator()) {
            openersCounter.incrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (level != null && !remove && !player.isSpectator()) {
            openersCounter.decrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    public void recheckOpen() {
        if (level != null && !remove) {
            openersCounter.recheckOpeners(level, worldPosition, getBlockState());
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.extrautils2.creative_chest");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return new CreativeChestMenu(id, playerInv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!heldStack.isEmpty()) tag.put("HeldStack", heldStack.save(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        heldStack = tag.contains("HeldStack")
                ? ItemStack.parse(provider, tag.getCompound("HeldStack")).orElse(ItemStack.EMPTY)
                : ItemStack.EMPTY;
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            chestLidController.shouldBeOpen(type > 0);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public float getOpenNess(float partialTick) {
        return chestLidController.getOpenness(partialTick);
    }

    private void signalOpenCount(Level level, BlockPos pos, BlockState state, int count) {
        level.blockEvent(pos, state.getBlock(), 1, count);
    }

    private static void playSound(Level level, BlockPos pos, net.minecraft.sounds.SoundEvent sound) {
        level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
    }
}
