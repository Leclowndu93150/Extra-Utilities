package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.AngelBlockItem;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.item.ChickenRingItem;
import com.leclowndu93150.extrautils2.item.GoldenBagItem;
import com.leclowndu93150.extrautils2.item.GoldenLassoItem;
import com.leclowndu93150.extrautils2.item.KikokuItem;
import com.leclowndu93150.extrautils2.item.SquidRingItem;
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

    public static final DeferredItem<KikokuItem> KIKOKU = ModRegistries.registerItem(
            "kikoku", KikokuItem::new);

    // --- Crafting Ingredients ---
    public static final DeferredItem<Item> REDSTONE_CRYSTAL = registerSimple("redstone_crystal");
    public static final DeferredItem<Item> REDSTONE_GEAR = registerSimple("redstone_gear");
    public static final DeferredItem<Item> EYE_REDSTONE = registerSimple("eye_redstone");
    public static final DeferredItem<Item> DYE_POWDER_LUNAR = registerSimple("dye_powder_lunar");
    public static final DeferredItem<Item> RED_COAL = registerSimple("red_coal");
    public static final DeferredItem<Item> UPGRADE_BASE = registerSimple("upgrade_base");
    public static final DeferredItem<Item> EVIL_DROP = registerSimple("evil_drop");
    public static final DeferredItem<Item> DEMON_INGOT = registerSimple("demon_ingot");
    public static final DeferredItem<Item> ENCHANTED_INGOT = registerSimple("enchanted_ingot");
    public static final DeferredItem<Item> REDSTONE_COIL = registerSimple("redstone_coil");
    public static final DeferredItem<Item> EVIL_INFUSED_INGOT = registerSimple("evil_infused_ingot");
    public static final DeferredItem<Item> DYE_POWDER_BLUE = registerSimple("dye_powder_blue");

    // --- Tools ---
    public static final DeferredItem<Item> BUILDERS_WAND = registerSimple("builders_wand");
    public static final DeferredItem<Item> CREATIVE_BUILDERS_WAND = registerSimple("creative_builders_wand");
    public static final DeferredItem<Item> DESTRUCTION_WAND = registerSimple("destruction_wand");
    public static final DeferredItem<Item> CREATIVE_DESTRUCTION_WAND = registerSimple("creative_destruction_wand");
    public static final DeferredItem<Item> WRENCH = registerSimple("wrench");
    public static final DeferredItem<Item> GLASS_CUTTER = registerSimple("glass_cutter");
    public static final DeferredItem<Item> TROWEL = registerSimple("trowel");
    public static final DeferredItem<Item> WATERING_CAN = registerSimple("watering_can");
    public static final DeferredItem<GoldenLassoItem> GOLDEN_LASSO = ModRegistries.registerItem(
            "golden_lasso", () -> new GoldenLassoItem(false));
    public static final DeferredItem<GoldenLassoItem> CURSED_LASSO = ModRegistries.registerItem(
            "cursed_lasso", () -> new GoldenLassoItem(true));
    public static final DeferredItem<Item> BOOMERANG = registerSimple("boomerang");
    public static final DeferredItem<Item> FIRE_AXE = registerSimple("fire_axe");
    public static final DeferredItem<Item> FIRE_EXTINGUISHER = registerSimple("fire_extinguisher");
    public static final DeferredItem<Item> COMPOUND_BOW = registerSimple("compound_bow");
    public static final DeferredItem<Item> LUX_SABER = registerSimple("lux_saber");
    public static final DeferredItem<Item> BIOME_MARKER = registerSimple("biome_marker");
    public static final DeferredItem<Item> INDEXER_REMOTE = registerSimple("indexer_remote");
    public static final DeferredItem<Item> POWER_MANAGER = registerSimple("power_manager");

    // --- Rings ---
    public static final DeferredItem<ChickenRingItem> CHICKEN_RING = ModRegistries.registerItem(
            "chicken_ring", ChickenRingItem::new);
    public static final DeferredItem<SquidRingItem> SQUID_RING = ModRegistries.registerItem(
            "squid_ring", SquidRingItem::new);

    // --- Misc ---
    public static final DeferredItem<Item> SUN_CRYSTAL = registerSimple("sun_crystal");
    public static final DeferredItem<Item> UNSTABLE_INGOT = registerSimple("unstable_ingot");
    public static final DeferredItem<Item> CONTRACT = registerSimple("contract");
    public static final DeferredItem<Item> MAGIC_APPLE = registerSimple("magic_apple");
    public static final DeferredItem<Item> FILTER_ITEM = registerSimple("filter_item");
    public static final DeferredItem<Item> FILTER_FLUID = registerSimple("filter_fluid");
    public static final DeferredItem<Item> SNOW_GLOBE = registerSimple("snow_globe");

    public static final DeferredItem<Item> OPINIUM_CORE_0 = registerOpinium(0);
    public static final DeferredItem<Item> OPINIUM_CORE_1 = registerOpinium(1);
    public static final DeferredItem<Item> OPINIUM_CORE_2 = registerOpinium(2);
    public static final DeferredItem<Item> OPINIUM_CORE_3 = registerOpinium(3);
    public static final DeferredItem<Item> OPINIUM_CORE_4 = registerOpinium(4);
    public static final DeferredItem<Item> OPINIUM_CORE_5 = registerOpinium(5);
    public static final DeferredItem<Item> OPINIUM_CORE_6 = registerOpinium(6);
    public static final DeferredItem<Item> OPINIUM_CORE_7 = registerOpinium(7);
    public static final DeferredItem<Item> OPINIUM_CORE_8 = registerOpinium(8);

    private static DeferredItem<Item> registerSimple(String name) {
        return ModRegistries.registerItem(name, () -> new Item(new Item.Properties()));
    }

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
