package com.leclowndu93150.extrautils2.data.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GpData {
    public static final Codec<GpData> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Codec.INT.fieldOf("frequency").forGetter(d -> d.frequency),
            Codec.INT.listOf().fieldOf("allies").forGetter(d -> List.copyOf(d.allies)),
            Codec.BOOL.fieldOf("locked").orElse(false).forGetter(d -> d.locked)
    ).apply(inst, (freq, allies, locked) -> {
        GpData data = new GpData(freq);
        data.allies.addAll(allies);
        data.locked = locked;
        return data;
    }));

    public int frequency;
    public final Set<Integer> allies = new HashSet<>();
    public boolean locked;

    public GpData(int frequency) {
        this.frequency = frequency;
    }
}
