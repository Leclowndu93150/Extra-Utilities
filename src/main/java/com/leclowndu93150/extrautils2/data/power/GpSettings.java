package com.leclowndu93150.extrautils2.data.power;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GpSettings extends SavedData {
    private static final String NAME = "extrautils2_gp";

    public static final Factory<GpSettings> FACTORY = new Factory<>(
            GpSettings::new,
            GpSettings::load,
            null
    );

    public final Map<Integer, UUID> freqToPlayer = new HashMap<>();
    public final Map<Integer, Set<Integer>> allies = new HashMap<>();
    public final Set<Integer> lockedFreqs = new HashSet<>();

    public static GpSettings get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, NAME);
    }

    private GpSettings() {}

    private static GpSettings load(CompoundTag tag, HolderLookup.Provider provider) {
        GpSettings settings = new GpSettings();

        ListTag players = tag.getList("players", Tag.TAG_COMPOUND);
        for (int i = 0; i < players.size(); i++) {
            CompoundTag entry = players.getCompound(i);
            int freq = entry.getInt("freq");
            UUID uuid = entry.getUUID("uuid");
            settings.freqToPlayer.put(freq, uuid);

            int[] alliesArr = entry.getIntArray("allies");
            if (alliesArr.length > 0) {
                Set<Integer> set = new HashSet<>();
                for (int a : alliesArr) set.add(a);
                settings.allies.put(freq, set);
            }
        }

        for (int locked : tag.getIntArray("lockedFreqs")) {
            settings.lockedFreqs.add(locked);
        }

        return settings;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag players = new ListTag();
        for (Map.Entry<Integer, UUID> entry : freqToPlayer.entrySet()) {
            CompoundTag e = new CompoundTag();
            e.putInt("freq", entry.getKey());
            e.putUUID("uuid", entry.getValue());
            Set<Integer> a = allies.get(entry.getKey());
            if (a != null && !a.isEmpty()) {
                int[] arr = a.stream().mapToInt(Integer::intValue).toArray();
                e.putIntArray("allies", arr);
            }
            players.add(e);
        }
        tag.put("players", players);
        tag.putIntArray("lockedFreqs", lockedFreqs.stream().mapToInt(Integer::intValue).toArray());
        return tag;
    }

    public void trackPlayer(int freq, UUID uuid) {
        freqToPlayer.put(freq, uuid);
        setDirty();
    }

    public void setAllies(int freq, Set<Integer> newAllies) {
        if (newAllies.isEmpty()) allies.remove(freq);
        else allies.put(freq, new HashSet<>(newAllies));
        setDirty();
    }

    public void setLocked(int freq, boolean locked) {
        if (locked) lockedFreqs.add(freq);
        else lockedFreqs.remove(freq);
        setDirty();
    }
}
