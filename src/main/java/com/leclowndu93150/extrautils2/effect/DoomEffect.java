package com.leclowndu93150.extrautils2.effect;

import com.leclowndu93150.extrautils2.registry.ModDamageTypes;
import com.leclowndu93150.extrautils2.registry.ModMobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DoomEffect extends MobEffect {

    public DoomEffect() {
        super(MobEffectCategory.HARMFUL, 0x300C30);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance instance = entity.getEffect(ModMobEffects.DOOM);
        if (instance == null) return true;

        int duration = instance.getDuration();
        if (entity.level().isClientSide) {
            int seconds = duration / 20;
            if (seconds > 0 && duration % 20 == 0 && (seconds <= 10 || seconds % 10 == 0)) {
                if (entity instanceof Player player) {
                    player.displayClientMessage(
                            Component.literal("The Specter of Death will arrive in " + seconds + " seconds."), false);
                }
            }
        } else if (duration <= 4) {
            entity.hurt(ModDamageTypes.source(entity.level(), ModDamageTypes.DOOM), Float.MAX_VALUE);
        }
        return true;
    }
}
