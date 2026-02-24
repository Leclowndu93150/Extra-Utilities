package com.leclowndu93150.extrautils2.blockentity.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HandCrankTile extends GeneratorTile {
    private static final double DELTA_OFFSET = Math.PI / 10.0;

    private float crankTime = 0f;
    private float renderOffset = 0f;

    public HandCrankTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HandCrankTile tile) {
        if (tile.crankTime > 0f) {
            if (level.isClientSide) {
                tile.renderOffset += (float)(tile.crankTime * DELTA_OFFSET);
            }
            tile.crankTime = Math.max(0f, tile.crankTime - 0.05f);
            if (!level.isClientSide) {
                tile.sync();
            }
        }
        GeneratorTile.tick(level, pos, state, tile);
    }

    public InteractionResult onUse(Player player) {
        if (!level.isClientSide) {
            crankTime = 1f;
            sync();
        } else {
            crankTime = 1f;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public float getCrankTime() {
        return crankTime;
    }

    public float getRenderOffset() {
        return renderOffset;
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
