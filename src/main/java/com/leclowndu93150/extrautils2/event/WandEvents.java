package com.leclowndu93150.extrautils2.event;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.DestructionWandItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class WandEvents {

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof DestructionWandItem wand)) return;

        if (event.getPosition().isEmpty()) return;

        float adjusted = wand.getAdjustedSpeed(player, event.getState(), event.getPosition().get());
        if (adjusted <= 0f) {
            event.setCanceled(true);
        } else {
            event.setNewSpeed(adjusted);
        }
    }
}
