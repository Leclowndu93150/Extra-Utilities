package com.leclowndu93150.extrautils2.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class GoldenLassoItem extends Item {

    public static final String TAG_ENTITY = "CapturedEntity";
    private static final String TAG_ENTITY_NAME = "CapturedEntityName";
    private static final String TAG_ENTITY_HEALTH = "CapturedHealth";
    private static final String TAG_ENTITY_MAX_HEALTH = "CapturedMaxHealth";
    private static final String TAG_CURSED_PICKED_UP = "CursedLassoPickedUp";

    private final boolean cursed;

    public GoldenLassoItem(boolean cursed) {
        super(new Properties().stacksTo(1));
        this.cursed = cursed;
    }

    public boolean hasEntity(ItemStack stack) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return data.contains(TAG_ENTITY);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide()) return InteractionResult.PASS;
        if (hasEntity(stack)) return InteractionResult.PASS;
        if (!target.isAlive()) return InteractionResult.PASS;
        if (target instanceof Player) return InteractionResult.PASS;

        if (cursed) {
            if (!(target instanceof Enemy)) {
                player.displayClientMessage(Component.translatable("message.extrautils2.lasso.not_hostile", target.getDisplayName()), true);
                return InteractionResult.FAIL;
            }
            if (!player.getAbilities().instabuild) {
                if (!target.canChangeDimensions(player.level(), player.level())) {
                    player.displayClientMessage(Component.translatable("message.extrautils2.lasso.too_powerful", target.getDisplayName()), true);
                    return InteractionResult.FAIL;
                }
                if (target instanceof Mob mob && mob.getPersistentData().getBoolean(TAG_CURSED_PICKED_UP)) {
                    return InteractionResult.FAIL;
                }
                float health = target.getHealth();
                float maxHealth = target.getMaxHealth();
                float threshold = Mth.clamp(maxHealth / 4.0F, 4.0F, 10.0F);
                if (health > threshold) {
                    player.displayClientMessage(Component.translatable("message.extrautils2.lasso.too_much_health",
                            target.getDisplayName(),
                            (int) Math.floor(health / 2.0F),
                            (int) Math.floor(threshold / 2.0F)), true);
                    return InteractionResult.FAIL;
                }
            }
        } else {
            if (target instanceof Enemy) {
                player.displayClientMessage(Component.translatable("message.extrautils2.lasso.hostile", target.getDisplayName()), true);
                return InteractionResult.FAIL;
            }
            if (!(target instanceof Mob)) {
                return InteractionResult.PASS;
            }
            if (target instanceof Mob mob && mob.getTarget() != null) {
                player.displayClientMessage(Component.translatable("message.extrautils2.lasso.attacking", target.getDisplayName()), true);
                return InteractionResult.FAIL;
            }
        }

        CompoundTag entityTag = new CompoundTag();
        if (!target.saveAsPassenger(entityTag)) {
            return InteractionResult.FAIL;
        }

        float health = target.getHealth();
        float maxHealth = target.getMaxHealth();
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.put(TAG_ENTITY, entityTag);
            tag.putFloat(TAG_ENTITY_HEALTH, health);
            tag.putFloat(TAG_ENTITY_MAX_HEALTH, maxHealth);
            if (target.hasCustomName()) {
                tag.putString(TAG_ENTITY_NAME, Component.Serializer.toJson(target.getCustomName(), player.registryAccess()));
            }
        });

        target.discard();
        player.setItemInHand(hand, stack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (ctx.getLevel().isClientSide()) return InteractionResult.PASS;

        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = ctx.getItemInHand();
        if (!hasEntity(stack)) return InteractionResult.PASS;

        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag customTag = data.copyTag();
        CompoundTag entityTag = customTag.getCompound(TAG_ENTITY);

        Level level = ctx.getLevel();

        if (cursed && level.getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            player.displayClientMessage(Component.translatable("message.extrautils2.lasso.peaceful"), true);
            return InteractionResult.FAIL;
        }

        Optional<Entity> optEntity = EntityType.create(entityTag, level);
        if (optEntity.isEmpty()) return InteractionResult.FAIL;

        Entity entity = optEntity.get();
        Vec3 pos = Vec3.atBottomCenterOf(ctx.getClickedPos().relative(ctx.getClickedFace()));
        entity.setPos(pos.x, pos.y, pos.z);
        entity.setDeltaMovement(Vec3.ZERO);
        entity.fallDistance = 0;

        if (cursed && entity instanceof Mob mob) {
            mob.getPersistentData().putBoolean(TAG_CURSED_PICKED_UP, true);
        }

        level.addFreshEntity(entity);

        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.remove(TAG_ENTITY);
            tag.remove(TAG_ENTITY_NAME);
            tag.remove(TAG_ENTITY_HEALTH);
            tag.remove(TAG_ENTITY_MAX_HEALTH);
        });

        player.setItemInHand(ctx.getHand(), stack);

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasEntity(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        if (hasEntity(stack)) {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = data.copyTag();
            CompoundTag entityTag = tag.getCompound(TAG_ENTITY);
            String entityId = entityTag.getString("id");

            float hp = tag.getFloat(TAG_ENTITY_HEALTH);
            float maxHp = tag.getFloat(TAG_ENTITY_MAX_HEALTH);
            String healthStr = String.format("%.1f/%.1f", hp / 2.0F, maxHp / 2.0F);

            if (tag.contains(TAG_ENTITY_NAME)) {
                tooltip.add(Component.literal(tag.getString(TAG_ENTITY_NAME))
                        .append(Component.literal(" (" + healthStr + " \u2764)").withStyle(ChatFormatting.RED))
                        .withStyle(ChatFormatting.GOLD));
            } else {
                EntityType.byString(entityId).ifPresent(type ->
                        tooltip.add(type.getDescription().copy()
                                .append(Component.literal(" (" + healthStr + " \u2764)").withStyle(ChatFormatting.RED))
                                .withStyle(ChatFormatting.GOLD)));
            }
        } else {
            if (cursed) {
                tooltip.add(Component.translatable("tooltip.extrautils2.lasso.cursed.empty").withStyle(ChatFormatting.GRAY));
            } else {
                tooltip.add(Component.translatable("tooltip.extrautils2.lasso.empty").withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
