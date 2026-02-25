package com.leclowndu93150.extrautils2.event;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.KikokuItem;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = ExtraUtilities.MODID)
public final class KikokuEvents {

    private static final ResourceLocation SOUL_DAMAGE_ID = KikokuItem.getSoulDamageId();

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity.level().isClientSide) return;
        if (entity.tickCount % 200 != 0) return;

        AttributeInstance healthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr == null) return;

        AttributeModifier modifier = healthAttr.getModifier(SOUL_DAMAGE_ID);
        if (modifier == null) return;

        double amount = modifier.amount();
        double newAmount = ((double) Math.round(amount * 39.0) + 1.0) / 39.0;

        healthAttr.removeModifier(SOUL_DAMAGE_ID);
        if (newAmount < 0.0) {
            healthAttr.addPermanentModifier(new AttributeModifier(
                    SOUL_DAMAGE_ID, newAmount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
            ));
        }
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        if (!(left.getItem() instanceof KikokuItem)) return;
        if (right.isEmpty()) return;

        ItemEnchantments leftEnchants = left.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments rightEnchants = right.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        if (rightEnchants.isEmpty()) return;

        Object2IntOpenHashMap<Holder<Enchantment>> merged = new Object2IntOpenHashMap<>();
        for (var entry : leftEnchants.entrySet()) {
            merged.put(entry.getKey(), entry.getIntValue());
        }

        int cost = 0;
        for (var entry : rightEnchants.entrySet()) {
            Holder<Enchantment> enchantment = entry.getKey();
            int addLevel = entry.getIntValue();
            int currentLevel = merged.getOrDefault(enchantment, 0);

            int newLevel;
            if (currentLevel == 0) {
                newLevel = addLevel;
                cost += addLevel;
            } else {
                newLevel = Math.min(currentLevel + addLevel, enchantment.value().getMaxLevel() * 2);
                cost += newLevel - currentLevel;
            }
            merged.put(enchantment, newLevel);
        }

        ItemStack output = left.copy();
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (var entry : merged.object2IntEntrySet()) {
            mutable.set(entry.getKey(), entry.getIntValue());
        }
        output.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());

        event.setOutput(output);
        event.setCost(cost * 2);
    }
}
