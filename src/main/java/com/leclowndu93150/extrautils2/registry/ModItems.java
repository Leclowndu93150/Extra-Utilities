package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.GoldenBagItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredItem<GoldenBagItem> GOLDEN_BAG = ModRegistries.registerItem(
            "golden_bag", GoldenBagItem::new
    );

    public static void init() {}
}
