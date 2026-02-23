package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.item.GoldenBagItem;
import com.leclowndu93150.extrautils2.item.UpgradeItem;
import com.leclowndu93150.extrautils2.upgrade.UpgradeType;
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

    public static final DeferredItem<UpgradeItem> UPGRADE_SPEED = ModRegistries.registerItem(
            "upgrade_speed", () -> new UpgradeItem(UpgradeType.SPEED, 4, false));
    public static final DeferredItem<UpgradeItem> UPGRADE_SPEED_ENCHANTED = ModRegistries.registerItem(
            "upgrade_speed_enchanted", () -> new UpgradeItem(UpgradeType.SPEED, 16, true));
    public static final DeferredItem<UpgradeItem> UPGRADE_SPEED_SUPER = ModRegistries.registerItem(
            "upgrade_speed_super", () -> new UpgradeItem(UpgradeType.SPEED, 64, true));
    public static final DeferredItem<UpgradeItem> UPGRADE_STACK_SIZE = ModRegistries.registerItem(
            "upgrade_stack_size", () -> new UpgradeItem(UpgradeType.STACK_SIZE, 64, false));
    public static final DeferredItem<UpgradeItem> UPGRADE_MINING = ModRegistries.registerItem(
            "upgrade_mining", () -> new UpgradeItem(UpgradeType.MINING, 64, false));

    public static void init() {}
}
