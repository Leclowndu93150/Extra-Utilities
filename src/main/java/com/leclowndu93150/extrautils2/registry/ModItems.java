package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.item.GoldenBagItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredItem<GoldenBagItem> GOLDEN_BAG = ModRegistries.registerItem(
            "golden_bag", GoldenBagItem::new
    );

    public static final DeferredItem<AngelRingItem> ANGEL_RING_BASE = ModRegistries.registerItem(
            "angel_ring_base", () -> new AngelRingItem(0));
    public static final DeferredItem<AngelRingItem> ANGEL_RING_FEATHER = ModRegistries.registerItem(
            "angel_ring_feather", () -> new AngelRingItem(1));
    public static final DeferredItem<AngelRingItem> ANGEL_RING_BUTTERFLY = ModRegistries.registerItem(
            "angel_ring_butterfly", () -> new AngelRingItem(2));
    public static final DeferredItem<AngelRingItem> ANGEL_RING_DEMON = ModRegistries.registerItem(
            "angel_ring_demon", () -> new AngelRingItem(3));
    public static final DeferredItem<AngelRingItem> ANGEL_RING_GOLDEN = ModRegistries.registerItem(
            "angel_ring_golden", () -> new AngelRingItem(4));
    public static final DeferredItem<AngelRingItem> ANGEL_RING_BAT = ModRegistries.registerItem(
            "angel_ring_bat", () -> new AngelRingItem(5));

    public static void init() {}
}
