package com.leclowndu93150.extrautils2.event;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.registry.ModDamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;

@EventBusSubscriber(modid = ExtraUtilities.MODID)
public final class SpikeEvents {
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (event.getSource().is(ModDamageTypes.SPIKE_CREATIVE)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        if (event.getEntity().getLastDamageSource() != null && event.getEntity().getLastDamageSource().is(ModDamageTypes.SPIKE_CREATIVE)) {
            event.setDroppedExperience(0);
        }
        if (event.getEntity().getLastDamageSource() != null && event.getEntity().getLastDamageSource().is(ModDamageTypes.SPIKE)) {
            if (event.getEntity().getPersistentData().getBoolean("xu2_spike_gold")) {
                event.getEntity().getPersistentData().remove("xu2_spike_gold");
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level)) return;
        if (event.getSource() == null || !event.getSource().is(ModDamageTypes.SPIKE)) return;
        if (!entity.getPersistentData().getBoolean("xu2_spike_gold")) return;
        entity.getPersistentData().remove("xu2_spike_gold");
        int xp = entity.getExperienceReward(level, event.getSource().getEntity());
        if (xp > 0) {
            ExperienceOrb.award(level, entity.position(), xp);
        }
    }
}
