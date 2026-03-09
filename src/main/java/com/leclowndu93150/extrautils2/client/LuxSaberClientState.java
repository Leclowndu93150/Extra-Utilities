package com.leclowndu93150.extrautils2.client;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.LuxSaberItem;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public final class LuxSaberClientState {
    public static final int MODEL_STAGES = 20;
    public static final ResourceLocation EXTENDED_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "lux_saber_extended");

    private static final WeakHashMap<ItemStack, Float> EXTENSION = new WeakHashMap<>();
    private static final float EXTEND_STEP = 0.05F;
    private static final float RETRACT_STEP = 0.2F;

    private LuxSaberClientState() {
    }

    public static void registerProperties() {
        for (Item item : ModItems.getLuxSabers()) {
            ItemProperties.register(item, EXTENDED_PROPERTY, (stack, level, entity, seed) -> getExtension(stack));
        }
    }

    public static void tick(Minecraft minecraft) {
        if (minecraft.level == null) {
            EXTENSION.clear();
            return;
        }

        Set<ItemStack> heldSabers = Collections.newSetFromMap(new IdentityHashMap<>());

        for (Player player : minecraft.level.players()) {
            extend(player.getMainHandItem(), heldSabers);
            extend(player.getOffhandItem(), heldSabers);
        }

        decayUnheld(heldSabers);
    }

    public static float getExtension(ItemStack stack) {
        return EXTENSION.getOrDefault(stack, 0.0F);
    }

    public static int getStage(ItemStack stack) {
        return Math.min(MODEL_STAGES, Math.max(0, (int) Math.ceil(getExtension(stack) * MODEL_STAGES)));
    }

    private static void extend(ItemStack stack, Set<ItemStack> heldSabers) {
        if (!(stack.getItem() instanceof LuxSaberItem)) {
            return;
        }

        heldSabers.add(stack);
        EXTENSION.put(stack, Math.min(1.0F, getExtension(stack) + EXTEND_STEP));
    }

    private static void decayUnheld(Set<ItemStack> heldSabers) {
        Iterator<Map.Entry<ItemStack, Float>> iterator = EXTENSION.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ItemStack, Float> entry = iterator.next();
            if (heldSabers.contains(entry.getKey())) {
                continue;
            }
            float value = Math.max(0.0F, entry.getValue() - RETRACT_STEP);
            if (value <= 0.0F) {
                iterator.remove();
            } else {
                entry.setValue(value);
            }
        }
    }
}
