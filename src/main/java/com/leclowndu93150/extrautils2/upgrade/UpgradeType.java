package com.leclowndu93150.extrautils2.upgrade;

public enum UpgradeType {
    STACK_SIZE(1, 5.0f),
    SPEED(64, 1.0f),
    MINING(1, 10.0f) {
        @Override
        public float getPowerUse(int level) {
            return 0.0f;
        }
    };

    private static final float PENALTY = 0.032786883f;
    public final int maxLevel;
    public final float power;

    UpgradeType(int maxLevel, float power) {
        this.maxLevel = maxLevel;
        this.power = power;
    }

    public int getModifierLevel(int level) {
        return level;
    }

    public float getPowerUse(int level) {
        if (level <= 0) return 0.0f;
        if (level == 1) return 1.0f * power;
        float v = (float) Math.round((100f * level) * (1.0f + level * PENALTY) / 1.0327868f) / 100.0f;
        return v * power;
    }
}
