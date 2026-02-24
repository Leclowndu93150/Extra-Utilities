package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.AngelBlockItem;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.item.GoldenBagItem;
import com.leclowndu93150.extrautils2.item.UpgradeItem;
import net.minecraft.world.item.Item;
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

    public static final DeferredItem<AngelBlockItem> ANGEL_BLOCK = ModRegistries.registerItem(
            "angel_block", () -> new AngelBlockItem(ModBlocks.ANGEL_BLOCK.get(), new Item.Properties()));

    public static final DeferredItem<Item> OPINIUM_CORE_0 = registerOpinium(0);
    public static final DeferredItem<Item> OPINIUM_CORE_1 = registerOpinium(1);
    public static final DeferredItem<Item> OPINIUM_CORE_2 = registerOpinium(2);
    public static final DeferredItem<Item> OPINIUM_CORE_3 = registerOpinium(3);
    public static final DeferredItem<Item> OPINIUM_CORE_4 = registerOpinium(4);
    public static final DeferredItem<Item> OPINIUM_CORE_5 = registerOpinium(5);
    public static final DeferredItem<Item> OPINIUM_CORE_6 = registerOpinium(6);
    public static final DeferredItem<Item> OPINIUM_CORE_7 = registerOpinium(7);
    public static final DeferredItem<Item> OPINIUM_CORE_8 = registerOpinium(8);

    private static DeferredItem<Item> registerOpinium(int tier) {
        return ModRegistries.registerItem("opinium_core_" + tier, () -> new Item(new Item.Properties()));
    }

    public static DeferredItem<Item> getOpiniumCore(int tier) {
        return switch (tier) {
            case 0 -> OPINIUM_CORE_0;
            case 1 -> OPINIUM_CORE_1;
            case 2 -> OPINIUM_CORE_2;
            case 3 -> OPINIUM_CORE_3;
            case 4 -> OPINIUM_CORE_4;
            case 5 -> OPINIUM_CORE_5;
            case 6 -> OPINIUM_CORE_6;
            case 7 -> OPINIUM_CORE_7;
            case 8 -> OPINIUM_CORE_8;
            default -> throw new IllegalArgumentException("Invalid opinium tier: " + tier);
        };
    }

    public static void init() {}
}
