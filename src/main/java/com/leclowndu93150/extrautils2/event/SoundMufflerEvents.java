package com.leclowndu93150.extrautils2.event;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.SoundMufflerBlock;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ExtraUtilities.MODID, value = Dist.CLIENT)
public final class SoundMufflerEvents {

    private static final int RANGE_SQ = 8 * 8;
    private static final long EXPIRY_MS = 7000;

    private static final Map<BlockPos, Long> mufflers = new ConcurrentHashMap<>();

    public static void addMuffler(BlockPos pos) {
        mufflers.put(pos.immutable(), System.currentTimeMillis());
    }

    public static void removeMuffler(BlockPos pos) {
        mufflers.remove(pos);
    }

    public static void heartbeat(BlockPos pos) {
        mufflers.put(pos.immutable(), System.currentTimeMillis());
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            mufflers.clear();
        }
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (mufflers.isEmpty()) return;

        SoundInstance sound = event.getSound();
        if (sound == null) return;

        long now = System.currentTimeMillis();
        double sx = sound.getX();
        double sy = sound.getY();
        double sz = sound.getZ();

        Iterator<Map.Entry<BlockPos, Long>> it = mufflers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<BlockPos, Long> entry = it.next();
            if (now - entry.getValue() > EXPIRY_MS) {
                it.remove();
                continue;
            }
            BlockPos pos = entry.getKey();
            double dx = pos.getX() + 0.5 - sx;
            double dy = pos.getY() + 0.5 - sy;
            double dz = pos.getZ() + 0.5 - sz;
            if (dx * dx + dy * dy + dz * dz <= RANGE_SQ) {
                event.setSound(new MuffledSound(sound, 0.05f));
                return;
            }
        }
    }

    private static class MuffledSound implements SoundInstance {
        private final SoundInstance original;
        private final float volumeMultiplier;

        MuffledSound(SoundInstance original, float volumeMultiplier) {
            this.original = original;
            this.volumeMultiplier = volumeMultiplier;
        }

        @Override public ResourceLocation getLocation() { return original.getLocation(); }
        @Override public Sound getSound() { return original.getSound(); }
        @Override public SoundSource getSource() { return original.getSource(); }
        @Override public boolean isLooping() { return original.isLooping(); }
        @Override public boolean isRelative() { return original.isRelative(); }
        @Override public int getDelay() { return original.getDelay(); }
        @Override public float getVolume() { return original.getVolume() * volumeMultiplier; }
        @Override public float getPitch() { return original.getPitch(); }
        @Override public double getX() { return original.getX(); }
        @Override public double getY() { return original.getY(); }
        @Override public double getZ() { return original.getZ(); }
        @Override public Attenuation getAttenuation() { return original.getAttenuation(); }
        @Override public WeighedSoundEvents resolve(SoundManager manager) { return original.resolve(manager); }
        @Override public boolean canStartSilent() { return original.canStartSilent(); }
        @Override public boolean canPlaySound() { return original.canPlaySound(); }
    }
}
