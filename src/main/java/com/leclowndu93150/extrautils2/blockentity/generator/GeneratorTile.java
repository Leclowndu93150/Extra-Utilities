package com.leclowndu93150.extrautils2.blockentity.generator;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.api.power.IPassiveGp;
import com.leclowndu93150.extrautils2.block.generator.GeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.GeneratorType;
import com.leclowndu93150.extrautils2.blockentity.XUBlockEntity;
import com.leclowndu93150.extrautils2.power.GpManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GeneratorTile extends XUBlockEntity implements IGpSource, IPassiveGp {
    private float currentGp = 0f;
    private int ownerFrequency = 0;
    private boolean registered = false;

    public GeneratorTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, GeneratorTile tile) {
        if (level.isClientSide) return;

        if (!tile.registered && tile.ownerFrequency != 0) {
            GpManager.INSTANCE.addSource(tile);
            tile.registered = true;
        }

        GeneratorType type = ((GeneratorBlock) state.getBlock()).generatorType;
        float raw = type.computeGp(tile, level);
        float capped = type.applyEfficiencyLoss(raw);
        if (capped != tile.currentGp) {
            tile.currentGp = capped;
            GpManager.INSTANCE.markSourceDirty(tile);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (registered) {
            GpManager.INSTANCE.removeSource(this);
            registered = false;
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!level.isClientSide && ownerFrequency != 0 && !registered) {
            GpManager.INSTANCE.addSource(this);
            registered = true;
        }
    }

    public void setOwnerFrequency(int freq) {
        this.ownerFrequency = freq;
        setChanged();
    }

    @Override
    public float getGp() {
        return currentGp;
    }

    @Override
    public int frequency() {
        return ownerFrequency;
    }

    @Override
    public void onPowerChanged(boolean powered) {
    }

    @Override
    public boolean isLoaded() {
        return level != null && !level.isClientSide && !isRemoved();
    }

    @Override
    public @Nullable Level level() {
        return level;
    }

    @Override
    public @Nullable BlockPos getPos() {
        return worldPosition;
    }

    public GeneratorType getGeneratorType() {
        if (getBlockState().getBlock() instanceof GeneratorBlock gen) return gen.generatorType;
        return null;
    }

    @Override
    public String getSourceName() {
        return ((GeneratorBlock) getBlockState().getBlock()).generatorType.name();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putFloat("gp", currentGp);
        tag.putInt("ownerFreq", ownerFrequency);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        currentGp = tag.getFloat("gp");
        ownerFrequency = tag.getInt("ownerFreq");
    }
}
