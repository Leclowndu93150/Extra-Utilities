package com.leclowndu93150.extrautils2.block.generator;

import com.leclowndu93150.extrautils2.block.XUEntityBlock;
import com.leclowndu93150.extrautils2.blockentity.generator.GeneratorTile;
import com.leclowndu93150.extrautils2.blockentity.generator.HandCrankTile;
import com.leclowndu93150.extrautils2.power.GpManager;
import com.leclowndu93150.extrautils2.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GeneratorBlock extends XUEntityBlock {
    private static final VoxelShape PANEL_SHAPE = Shapes.box(0, 0, 0, 1, 0.25, 1);

    public final GeneratorType generatorType;

    public GeneratorBlock(GeneratorType type, BlockBehaviour.Properties props) {
        super(props);
        this.generatorType = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (generatorType == GeneratorType.SOLAR || generatorType == GeneratorType.LUNAR) {
            return PANEL_SHAPE;
        }
        return Shapes.block();
    }


    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (generatorType == GeneratorType.PLAYER_WIND_UP) {
            return new HandCrankTile(ModBlockEntities.HAND_CRANK.get(), pos, state);
        }
        return new GeneratorTile(ModBlockEntities.GENERATOR.get(), pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (generatorType == GeneratorType.PLAYER_WIND_UP) {
            return (BlockEntityTicker<T>) createTicker(type, ModBlockEntities.HAND_CRANK.get(), HandCrankTile::tick);
        }
        return (BlockEntityTicker<T>) createTicker(type, ModBlockEntities.GENERATOR.get(), GeneratorTile::tick);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof ServerPlayer sp && level.getBlockEntity(pos) instanceof GeneratorTile tile) {
            int freq = GpManager.INSTANCE.assignFrequency(sp);
            tile.setOwnerFrequency(freq);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (generatorType == GeneratorType.PLAYER_WIND_UP) {
            if (level.getBlockEntity(pos) instanceof HandCrankTile crank) {
                return crank.onUse(player);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hit);
    }
}
