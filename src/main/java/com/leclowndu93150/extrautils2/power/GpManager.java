package com.leclowndu93150.extrautils2.power;

import com.leclowndu93150.extrautils2.api.power.IGpSource;
import com.leclowndu93150.extrautils2.api.power.IGpSourceItem;
import com.leclowndu93150.extrautils2.data.power.GpAttachments;
import com.leclowndu93150.extrautils2.data.power.GpData;
import com.leclowndu93150.extrautils2.data.power.GpFrequency;
import com.leclowndu93150.extrautils2.data.power.GpSettings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class GpManager {
    public static final GpManager INSTANCE = new GpManager();

    private final Map<Integer, GpFrequency> frequencies = new HashMap<>();
    private final Map<ServerPlayer, Map<IGpSourceItem, IGpSource>> playerSources = new HashMap<>();
    private final List<IGpSource> pendingAdd = new ArrayList<>();
    private final List<IGpSource> pendingRemove = new ArrayList<>();
    private final Map<Integer, Integer> allianceLinks = new HashMap<>();
    private boolean alliancesDirty = true;
    private final Random rand = new Random();

    private GpManager() {}

    public int assignFrequency(ServerPlayer player) {
        GpData data = player.getData(GpAttachments.GP_DATA.get());
        if (data.frequency != 0) return data.frequency;

        Set<Integer> usedFreqs = new HashSet<>(frequencies.keySet());
        MinecraftServer server = player.getServer();
        if (server != null) {
            for (ServerPlayer online : server.getPlayerList().getPlayers()) {
                int f = online.getData(GpAttachments.GP_DATA.get()).frequency;
                if (f != 0) usedFreqs.add(f);
            }
        }

        UUID uuid = player.getUUID();
        rand.setSeed(uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits());
        int freq;
        do {
            freq = rand.nextInt();
        } while (freq == 0 || usedFreqs.contains(freq));

        data.frequency = freq;
        player.setData(GpAttachments.GP_DATA.get(), data);
        alliancesDirty = true;
        return freq;
    }

    public int getFrequency(ServerPlayer player) {
        return player.getData(GpAttachments.GP_DATA.get()).frequency;
    }

    public GpData getGpData(ServerPlayer player) {
        return player.getData(GpAttachments.GP_DATA.get());
    }

    public void setAllies(ServerPlayer player, Set<Integer> allies) {
        GpData data = player.getData(GpAttachments.GP_DATA.get());
        data.allies.clear();
        data.allies.addAll(allies);
        player.setData(GpAttachments.GP_DATA.get(), data);
        getSettings(player.getServer()).setAllies(data.frequency, allies);
        alliancesDirty = true;
    }

    public void setLocked(ServerPlayer player, boolean locked) {
        GpData data = player.getData(GpAttachments.GP_DATA.get());
        data.locked = locked;
        player.setData(GpAttachments.GP_DATA.get(), data);
        getSettings(player.getServer()).setLocked(data.frequency, locked);
    }

    private GpSettings getSettings(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return GpSettings.get(overworld);
    }

    public GpFrequency getOrCreateFreq(int freq) {
        return frequencies.computeIfAbsent(freq, GpFrequency::new);
    }

    public boolean isPowered(ServerPlayer player) {
        int freq = getFrequency(player);
        if (freq == 0) return false;
        GpFrequency gpFreq = frequencies.get(resolveFreq(freq));
        return gpFreq != null && gpFreq.isPowered();
    }

    public boolean canAccess(ServerPlayer player, IGpSource source) {
        int playerFreq = getFrequency(player);
        if (playerFreq == 0) return false;
        GpData data = player.getData(GpAttachments.GP_DATA.get());
        if (data.locked) return areAllied(playerFreq, source.frequency());
        return true;
    }

    public boolean areAllied(int freqA, int freqB) {
        if (freqA == freqB) return true;
        return resolveFreq(freqA) == resolveFreq(freqB);
    }

    private int resolveFreq(int freq) {
        return allianceLinks.getOrDefault(freq, freq);
    }

    private void rebuildAllianceLinks(MinecraftServer server) {
        allianceLinks.clear();
        if (server == null) return;

        GpSettings settings = getSettings(server);
        Map<Integer, Set<Integer>> alliances = new HashMap<>(settings.allies);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            GpData data = player.getData(GpAttachments.GP_DATA.get());
            if (data.frequency == 0) continue;
            alliances.put(data.frequency, new HashSet<>(data.allies));
        }

        Map<Integer, Integer> parent = new HashMap<>();
        for (int freq : alliances.keySet()) parent.put(freq, freq);

        for (Map.Entry<Integer, Set<Integer>> entry : alliances.entrySet()) {
            int a = entry.getKey();
            for (int b : entry.getValue()) {
                Set<Integer> bAllies = alliances.get(b);
                if (bAllies != null && bAllies.contains(a)) {
                    union(parent, a, b);
                }
            }
        }

        for (int freq : parent.keySet()) {
            int root = find(parent, freq);
            if (root != freq) allianceLinks.put(freq, root);
        }

        for (GpFrequency gpFreq : frequencies.values()) {
            gpFreq.dirty = true;
        }
        alliancesDirty = false;
    }

    private int find(Map<Integer, Integer> parent, int x) {
        if (parent.getOrDefault(x, x) == x) return x;
        int root = find(parent, parent.get(x));
        parent.put(x, root);
        return root;
    }

    private void union(Map<Integer, Integer> parent, int a, int b) {
        int ra = find(parent, a);
        int rb = find(parent, b);
        if (ra != rb) parent.put(rb, ra);
    }

    public void addSource(IGpSource source) {
        synchronized (pendingAdd) {
            pendingAdd.add(source);
        }
    }

    public void removeSource(IGpSource source) {
        synchronized (pendingRemove) {
            pendingRemove.add(source);
        }
    }

    public void markSourceDirty(IGpSource source) {
        GpFrequency freq = frequencies.get(resolveFreq(source.frequency()));
        if (freq != null) freq.dirty = true;
    }

    public void clear() {
        frequencies.clear();
        pendingAdd.clear();
        pendingRemove.clear();
        playerSources.clear();
        allianceLinks.clear();
        alliancesDirty = true;
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        clear();
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        clear();
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();

        if (alliancesDirty) rebuildAllianceLinks(server);

        synchronized (pendingRemove) {
            for (IGpSource source : pendingRemove) {
                GpFrequency freq = frequencies.get(source.frequency());
                if (freq != null) {
                    freq.removeSource(source);
                    if (freq.isEmpty()) frequencies.remove(freq.frequency);
                }
            }
            pendingRemove.clear();
        }

        synchronized (pendingAdd) {
            for (IGpSource source : pendingAdd) {
                if (source.isLoaded()) {
                    getOrCreateFreq(resolveFreq(source.frequency())).addSource(source);
                }
            }
            pendingAdd.clear();
        }

        tickPlayerSources(server);

        for (GpFrequency freq : frequencies.values()) {
            if (freq.dirty) freq.refresh();
        }
    }

    private void tickPlayerSources(MinecraftServer server) {
        if (server == null) return;
        List<ServerPlayer> online = server.getPlayerList().getPlayers();
        Set<ServerPlayer> onlineSet = new HashSet<>(online);

        playerSources.keySet().removeIf(p -> !onlineSet.contains(p));

        for (ServerPlayer player : online) {
            Map<IGpSourceItem, IGpSource> active = playerSources.computeIfAbsent(player, k -> new HashMap<>());
            Set<IGpSourceItem> seen = new HashSet<>();

            Inventory inv = player.getInventory();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);
                if (stack.isEmpty()) continue;
                if (!(stack.getItem() instanceof IGpSourceItem creator)) continue;

                seen.add(creator);
                IGpSource existing = active.get(creator);
                boolean selected = inv.selected == i;

                if (existing != null && !creator.shouldOverride(existing, player, stack, selected)) {
                    if (!creator.shouldSustain(existing, stack)) {
                        removeSource(existing);
                        active.remove(creator);
                    }
                } else {
                    if (existing != null) removeSource(existing);
                    IGpSource source = creator.createSource(player, stack);
                    if (source != null) {
                        active.put(creator, source);
                        addSource(source);
                    }
                }
            }

            active.entrySet().removeIf(entry -> {
                if (!seen.contains(entry.getKey())) {
                    removeSource(entry.getValue());
                    return true;
                }
                return false;
            });
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int freq = assignFrequency(player);
            getOrCreateFreq(resolveFreq(freq)).addPlayer(player);
            getSettings(player.getServer()).trackPlayer(freq, player.getUUID());
            alliancesDirty = true;
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int freq = getFrequency(player);
            if (freq != 0) {
                GpFrequency gpFreq = frequencies.get(resolveFreq(freq));
                if (gpFreq != null) {
                    gpFreq.removePlayer(player);
                    if (gpFreq.isEmpty()) frequencies.remove(gpFreq.frequency);
                }
            }
            Map<IGpSourceItem, IGpSource> sources = playerSources.remove(player);
            if (sources != null) sources.values().forEach(this::removeSource);
            alliancesDirty = true;
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int freq = getFrequency(player);
            if (freq != 0) getOrCreateFreq(resolveFreq(freq)).addPlayer(player);
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            int freq = getFrequency(player);
            if (freq != 0) getOrCreateFreq(resolveFreq(freq)).dirty = true;
        }
    }
}
