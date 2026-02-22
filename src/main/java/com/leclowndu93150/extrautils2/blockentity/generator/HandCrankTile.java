package com.leclowndu93150.extrautils2.blockentity.generator;

import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HandCrankTile extends GeneratorTile {
    private float crankTime = 0f;

    public HandCrankTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HandCrankTile tile) {
        if (tile.crankTime > 0f) {
            tile.crankTime = Math.max(0f, tile.crankTime - 0.05f);
            if (!level.isClientSide) tile.sync();
        }
        GeneratorTile.tick(level, pos, state, tile);
    }

    public InteractionResult onUse(Player player) {
        if (!level.isClientSide) {
            crankTime = 1f;
            sync();
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public float getCrankTime() {
        return crankTime;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("crankTime", crankTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        crankTime = tag.getFloat("crankTime");
    }
}
