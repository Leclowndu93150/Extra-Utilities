package com.leclowndu93150.extrautils2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import com.leclowndu93150.extrautils2.registry.ModDamageTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class SpikeBlock extends XUBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public enum Type {
        WOOD(1.0f),
        STONE(2.0f),
        COPPER(3.0f),
        IRON(4.0f),
        GOLD(2.0f),
        DIAMOND(8.0f),
        NETHERITE(10.0f),
        CREATIVE(8000.0f);

        public final float damage;

        Type(float damage) {
            this.damage = damage;
        }

        public boolean hurt(Level level, BlockPos pos, BlockState state, LivingEntity living) {
            switch (this) {
                case WOOD -> {
                    if (living.getHealth() <= damage) return false;
                    return living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), Math.min(damage, living.getHealth() - 0.5f));
                }
                case COPPER -> {
                    boolean did = living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), damage);
                    if (did) {
                        living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                                net.minecraft.world.effect.MobEffects.POISON, 100, 0));
                    }
                    return did;
                }
                case GOLD -> {
                    boolean did = living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), damage);
                    if (did) {
                        living.getPersistentData().putBoolean("xu2_spike_gold", true);
                    }
                    return did;
                }
                case DIAMOND -> {
                    float min = Math.min(damage, living.getHealth() - 1.0e-4f);
                    boolean did = living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), min);
                    if (living.getHealth() <= 0.001f && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                        var fake = FakePlayerFactory.getMinecraft(serverLevel);
                        living.skipDropExperience();
                        did |= living.hurt(level.damageSources().playerAttack(fake), damage * 1000.0f);
                    }
                    return did;
                }
                case NETHERITE -> {
                    living.setRemainingFireTicks(100);
                    return living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), damage);
                }
                case CREATIVE -> {
                    living.skipDropExperience();
                    int i = 0;
                    boolean did = false;
                    while (i < 100 && living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE_CREATIVE), damage)) {
                        i++;
                        did = true;
                    }
                    return did;
                }
                default -> {
                    return living.hurt(ModDamageTypes.source(level, ModDamageTypes.SPIKE), damage);
                }
            }
        }

        public void addTooltip(ItemStack stack, Item.TooltipContext ctx, java.util.List<Component> tooltip, TooltipFlag flag) {
            switch (this) {
                case WOOD -> tooltip.add(Component.translatable("tooltip.extrautils2.spike.wood").withStyle(ChatFormatting.GRAY));
                case COPPER -> tooltip.add(Component.translatable("tooltip.extrautils2.spike.copper").withStyle(ChatFormatting.GRAY));
                case GOLD -> tooltip.add(Component.translatable("tooltip.extrautils2.spike.gold").withStyle(ChatFormatting.GRAY));
                case DIAMOND -> tooltip.add(Component.translatable("tooltip.extrautils2.spike.diamond").withStyle(ChatFormatting.GRAY));
                case NETHERITE -> tooltip.add(Component.translatable("tooltip.extrautils2.spike.netherite").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static final VoxelShape SHAPE = Shapes.box(0.03125, 0.03125, 0.03125, 0.96875, 0.96875, 0.96875);
    private static final VoxelShape COLLISION_DOWN = Shapes.box(0.03125, 0.0, 0.03125, 0.96875, 0.96875, 0.96875);
    private static final VoxelShape COLLISION_UP = Shapes.box(0.03125, 0.03125, 0.03125, 0.96875, 1.0, 0.96875);
    private static final VoxelShape COLLISION_NORTH = Shapes.box(0.03125, 0.03125, 0.0, 0.96875, 0.96875, 0.96875);
    private static final VoxelShape COLLISION_SOUTH = Shapes.box(0.03125, 0.03125, 0.03125, 0.96875, 0.96875, 1.0);
    private static final VoxelShape COLLISION_WEST = Shapes.box(0.0, 0.03125, 0.03125, 0.96875, 0.96875, 0.96875);
    private static final VoxelShape COLLISION_EAST = Shapes.box(0.03125, 0.03125, 0.03125, 1.0, 0.96875, 0.96875);

    public final Type spikeType;

    public SpikeBlock(Type type, BlockBehaviour.Properties props) {
        super(props);
        this.spikeType = type;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Entity entity = ctx instanceof EntityCollisionContext ecc ? ecc.getEntity() : null;
        if (entity != null && isIgnored(entity)) return Shapes.block();
        return switch (state.getValue(FACING)) {
            case DOWN -> COLLISION_DOWN;
            case UP -> COLLISION_UP;
            case NORTH -> COLLISION_NORTH;
            case SOUTH -> COLLISION_SOUTH;
            case WEST -> COLLISION_WEST;
            case EAST -> COLLISION_EAST;
        };
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) return;
        if (isIgnored(entity)) return;
        if (!(entity instanceof LivingEntity living)) return;
        hurtEntity(level, pos, state, living);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && state.getValue(FACING) == Direction.UP && entity instanceof LivingEntity living && !isIgnored(entity)) {
            hurtEntity(level, pos, state, living);
        }
        super.stepOn(level, pos, state, entity);
    }

    private void hurtEntity(Level level, BlockPos pos, BlockState state, LivingEntity living) {
        if (spikeType.hurt(level, pos, state, living)) {
            living.setDeltaMovement(0.0, living.getDeltaMovement().y, 0.0);
            living.hurtMarked = true;
        }
    }

    private boolean isIgnored(Entity entity) {
        return entity instanceof ItemEntity || entity instanceof ExperienceOrb;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        spikeType.addTooltip(stack, context, tooltip, flag);
    }
}
