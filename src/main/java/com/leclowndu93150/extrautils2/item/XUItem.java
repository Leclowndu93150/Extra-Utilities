package com.leclowndu93150.extrautils2.item;

import net.minecraft.world.item.Item;

public class XUItem extends Item {
    public XUItem(Properties props) {
        super(props);
    }

    public XUItem() {
        this(new Properties());
    }

    public static Item.Properties defaultProps() {
        return new Item.Properties();
    }
}
