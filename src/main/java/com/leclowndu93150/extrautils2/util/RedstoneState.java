package com.leclowndu93150.extrautils2.util;

public enum RedstoneState {
    OPERATE_ALWAYS,
    OPERATE_REDSTONE_ON,
    OPERATE_REDSTONE_OFF,
    OPERATE_REDSTONE_PULSE;

    public RedstoneState next(boolean allowPulse) {
        RedstoneState[] values = values();
        int start = (ordinal() + 1) % values.length;
        for (int i = 0; i < values.length; i++) {
            RedstoneState candidate = values[(start + i) % values.length];
            if (allowPulse || candidate != OPERATE_REDSTONE_PULSE) {
                return candidate;
            }
        }
        return OPERATE_ALWAYS;
    }
}
