package com.leclowndu93150.extrautils2.block;

import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public abstract class XUEntityBlock extends XUBlock implements EntityBlock {
    public XUEntityBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityTicker<T> createTicker(
            BlockEntityType<?> given,
            BlockEntityType<T> expected,
            BlockEntityTicker<? super T> ticker) {
        return given == expected ? (BlockEntityTicker<T>) ticker : null;
    }

    public static abstract class Facing extends XUBlock.Facing implements EntityBlock {
        public Facing(BlockBehaviour.Properties props) {
            super(props);
        }
    }

    public static abstract class FacingAll extends XUBlock.FacingAll implements EntityBlock {
        public FacingAll(BlockBehaviour.Properties props) {
            super(props);
        }
    }
}
