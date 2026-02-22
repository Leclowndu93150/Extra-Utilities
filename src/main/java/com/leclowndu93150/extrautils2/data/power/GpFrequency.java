package com.leclowndu93150.extrautils2.data.power;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.api.power.IPassiveGp;
import com.leclowndu93150.extrautils2.network.power.GpSyncPacket;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GpFrequency {
    public final int frequency;

    final List<IGpSource> sources = new CopyOnWriteArrayList<>();
    final List<ServerPlayer> players = new ArrayList<>();

    public float gpCreated;
    public float gpDrained;
    public boolean dirty = true;

    public GpFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean isPowered() {
        return gpDrained <= gpCreated;
    }

    public void refresh() {
        float created = 0f;
        float drained = 0f;
        for (IGpSource source : sources) {
            if (!source.isLoaded()) continue;
            float gp = source.getGp();
            if (Float.isNaN(gp) || gp < 0f) continue;
            if (source instanceof IPassiveGp) created += gp;
            else drained += gp;
        }
        boolean wasPowered = isPowered();
        gpCreated = created;
        gpDrained = drained;
        boolean nowPowered = isPowered();
        dirty = false;

        if (wasPowered != nowPowered) {
            for (IGpSource source : sources) {
                source.onPowerChanged(nowPowered);
            }
        }

        float c = gpCreated;
        float d = gpDrained;
        for (ServerPlayer player : players) {
            GpSyncPacket.send(player, c, d);
        }
    }

    public void addSource(IGpSource source) {
        sources.add(source);
        dirty = true;
    }

    public void removeSource(IGpSource source) {
        sources.remove(source);
        dirty = true;
    }

    public void addPlayer(ServerPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
            dirty = true;
        }
    }

    public void removePlayer(ServerPlayer player) {
        players.remove(player);
    }

    public boolean isEmpty() {
        return sources.isEmpty() && players.isEmpty();
    }
}
