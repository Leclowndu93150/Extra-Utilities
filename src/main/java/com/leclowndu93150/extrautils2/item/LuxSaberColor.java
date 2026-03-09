package com.leclowndu93150.extrautils2.item;

public enum LuxSaberColor {
    BLUE("lux_saber", "Lux Saber", 0x0015FF),
    PINK("lux_saber_pink", "Lux Saber (Pink)", 0xE9FFBF),
    RED("lux_saber_red", "Lux Saber (Red)", 0x93124E),
    YELLOW("lux_saber_yellow", "Lux Saber (Yellow)", 0xFFF698),
    GREEN("lux_saber_green", "Lux Saber (Green)", 0x2CFF58),
    CYAN("lux_saber_cyan", "Lux Saber (Cyan)", 0x18FFC4),
    WHITE("lux_saber_white", "Lux Saber (White)", 0xDFE4DF),
    BLACK("lux_saber_black", "Lux Saber (Black)", 0x514B51);

    private final String itemId;
    private final String displayName;
    private final int color;

    LuxSaberColor(String itemId, String displayName, int color) {
        this.itemId = itemId;
        this.displayName = displayName;
        this.color = color;
    }

    public String itemId() {
        return itemId;
    }

    public String displayName() {
        return displayName;
    }

    public int color() {
        return color;
    }
}
