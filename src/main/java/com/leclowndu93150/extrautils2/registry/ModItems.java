package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.item.XUItem;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItems {
    public static final DeferredItem<XUItem> GOLDEN_BAG = ModRegistries.registerItem(
            "golden_bag", XUItem::new
    );

    public static void init() {}
}
