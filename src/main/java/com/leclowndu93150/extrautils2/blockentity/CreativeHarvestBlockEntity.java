package com.leclowndu93150.extrautils2.blockentity;

import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CreativeHarvestBlockEntity extends XUBlockEntity {

    private BlockState mimicState = Blocks.AIR.defaultBlockState();

    public CreativeHarvestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_HARVEST.get(), pos, state);
    }

    public BlockState getMimicState() { return mimicState; }

    public boolean hasMimic() { return !mimicState.isAir(); }

    public void setMimicState(BlockState state) {
        this.mimicState = state == null ? Blocks.AIR.defaultBlockState() : state;
        setChanged();
        sync();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (hasMimic()) {
            tag.put("MimicState", NbtUtils.writeBlockState(mimicState));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("MimicState")) {
            mimicState = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("MimicState"));
        } else {
            mimicState = Blocks.AIR.defaultBlockState();
        }
    }
}
