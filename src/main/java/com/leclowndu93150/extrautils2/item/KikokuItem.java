package com.leclowndu93150.extrautils2.item;

import com.leclowndu93150.extrautils2.registry.ModDamageTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

public class KikokuItem extends SwordItem {

    private static final ResourceLocation SOUL_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath("extrautils2", "kikoku_soul_damage");
    private static final float ARMOR_BYPASS_DAMAGE = 3.0F;
    private static final float DIVINE_DAMAGE = 2.0F;
    private static final double SOUL_DRAIN_AMOUNT = 0.25;

    private static final Tier KIKOKU_TIER = new Tier() {
        @Override public int getUses() { return 2048; }
        @Override public float getSpeed() { return 10.0F; }
        @Override public float getAttackDamageBonus() { return 10.0F; }
        @Override public net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> getIncorrectBlocksForDrops() { return BlockTags.INCORRECT_FOR_NETHERITE_TOOL; }
        @Override public int getEnchantmentValue() { return 22; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    };

    public KikokuItem() {
        super(KIKOKU_TIER, new Properties()
                .attributes(SwordItem.createAttributes(KIKOKU_TIER, 3, -2.4F))
                .component(DataComponents.UNBREAKABLE, new Unbreakable(true))
        );
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (target == null || !target.isAttackable()) return false;
        if (attacker.level().isClientSide) return false;
        if (!(attacker instanceof Player player)) return false;

        float strength = player.getAttackStrengthScale(0.0F);
        double mx = target.getDeltaMovement().x;
        double my = target.getDeltaMovement().y;
        double mz = target.getDeltaMovement().z;
        Level level = target.level();

        boolean canDrainSoul = !target.isInvulnerableTo(level.damageSources().anvil(attacker));
        if (target instanceof Player targetPlayer) {
            if (targetPlayer.getAbilities().instabuild) canDrainSoul = false;
        }
        if (canDrainSoul) {
            drainHealth(target);
        }

        target.invulnerableTime = 0;
        target.hurt(ModDamageTypes.source(level, ModDamageTypes.KIKOKU_DIVINE, attacker), DIVINE_DAMAGE * strength);

        target.invulnerableTime = 0;
        target.hurt(ModDamageTypes.source(level, ModDamageTypes.KIKOKU_ARMOR_BYPASS, attacker), ARMOR_BYPASS_DAMAGE * strength);

        target.setDeltaMovement(mx, my, mz);
        target.hurtMarked = true;

        return true;
    }

    @Override
    public void postHurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    }

    private void drainHealth(LivingEntity target) {
        AttributeInstance healthAttr = target.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr == null) return;

        AttributeModifier existing = healthAttr.getModifier(SOUL_DAMAGE_ID);
        double newAmount;
        if (existing != null) {
            if (existing.amount() <= -1.0) return;
            newAmount = existing.amount() - SOUL_DRAIN_AMOUNT;
        } else {
            newAmount = -SOUL_DRAIN_AMOUNT;
        }

        if (newAmount < -1.0) newAmount = -1.0;

        healthAttr.removeModifier(SOUL_DAMAGE_ID);
        healthAttr.addPermanentModifier(new AttributeModifier(
                SOUL_DAMAGE_ID, newAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ));

        if (newAmount <= -1.0) {
            target.hurt(target.damageSources().fellOutOfWorld(), Float.MAX_VALUE);
        }
    }

    public static ResourceLocation getSoulDamageId() {
        return SOUL_DAMAGE_ID;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    }
}
