package com.leclowndu93150.extrautils2.block.generator;

import com.leclowndu93150.extrautils2.blockentity.generator.GeneratorTile;
import com.leclowndu93150.extrautils2.blockentity.generator.HandCrankTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.TreeMap;

public enum GeneratorType {
    SOLAR(2f, cap(80f, 0.6667f), cap(160f, 0.5f), cap(320f, 0.2f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            BlockPos pos = tile.getBlockPos();
            if (!level.canSeeSky(pos)) return 0f;
            float mult = isDaytime(level) ? 1f : 0f;
            if (level.isRaining()) mult *= 0.95f;
            return baseGp * mult;
        }
    },

    LUNAR(2f, cap(80f, 0.6667f), cap(160f, 0.5f), cap(320f, 0.2f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            BlockPos pos = tile.getBlockPos();
            if (!level.canSeeSky(pos)) return 0f;
            if (isDaytime(level)) return 0f;
            float moonBoost = 1f + level.getMoonBrightness() * 0.25f;
            return baseGp * moonBoost;
        }
    },

    LAVA(4f, cap(200f, 0.6667f), cap(400f, 0.5f), cap(800f, 0.2f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            float h = 0f;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos offset = tile.getBlockPos().relative(dir);
                BlockState state = level.getBlockState(offset);
                if (state.getBlock() == Blocks.LAVA) {
                    int lvl = state.getValue(LiquidBlock.LEVEL);
                    h = Math.max(h, 8f - lvl);
                    if (h >= 8f) return baseGp * 2f;
                }
            }
            return baseGp * (h / 4f);
        }
    },

    WATER(4f, cap(64f, 0.6667f), cap(128f, 0.5f), cap(512f, 0.1f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            float v = 0f;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos offset = tile.getBlockPos().relative(dir);
                BlockState state = level.getBlockState(offset);
                if (isWater(state)) {
                    BlockState above = level.getBlockState(offset.above());
                    if (isWater(above)) continue;
                    int lvl = state.getValue(LiquidBlock.LEVEL);
                    if (lvl > 0 && lvl < 8) v += (8 - lvl) / 4f;
                }
            }
            return baseGp * v;
        }

        private boolean isWater(BlockState state) {
            return state.getBlock() == Blocks.WATER;
        }
    },

    WIND(8f, cap(512f, 0.5f), cap(1024f, 0.2f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            BlockPos pos = tile.getBlockPos();
            if (!level.isEmptyBlock(pos.north()) || !level.isEmptyBlock(pos.south())) return 0f;
            float v = windMultiplier(level);
            return baseGp * v;
        }

        private float windMultiplier(Level level) {
            long t = level.getGameTime();
            float frac = (float)(t & 0xFF) / 256f;
            long k = (t >> 8) + (long) level.dimension().location().hashCode() * 31L;
            long a = k * k * 42317861L + k * 11L;
            long b = a + (2L * k + 1L) * 42317861L + 11L;
            float ai = (float)(a & 0xFF) / 256f;
            float bi = (float)(b & 0xFF) / 256f;
            float v1 = ai + (bi - ai) * frac;
            return 0.5f + v1 * 2f + (level.isRaining() ? 1f : 0f) + (level.isThundering() ? 2f : 0f);
        }
    },

    FIRE(4f, cap(40f, 0.6667f), cap(320f, 0.5f), cap(640f, 0.2f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            BlockState below = level.getBlockState(tile.getBlockPos().below());
            return below.getBlock() == Blocks.FIRE ? baseGp : 0f;
        }
    },

    PLAYER_WIND_UP(15f) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            if (!(tile instanceof HandCrankTile crank)) return 0f;
            return Math.min(crank.getCrankTime(), 0.5f) * 30f;
        }
    },

    DRAGON_EGG(500f, cap(500f, 0.5f), cap(1000f, 0.25f), cap(1500f, 0.05f)) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            BlockState above = level.getBlockState(tile.getBlockPos().above());
            return above.getBlock() == Blocks.DRAGON_EGG ? baseGp : 0f;
        }
    },

    CREATIVE(10000f) {
        @Override
        public float computeGp(GeneratorTile tile, Level level) {
            return baseGp;
        }
    };

    public final float baseGp;
    private final TreeMap<Float, Float> efficiencyCaps;

    GeneratorType(float baseGp, float[]... caps) {
        this.baseGp = baseGp;
        this.efficiencyCaps = new TreeMap<>();
        for (float[] cap : caps) {
            efficiencyCaps.put(cap[0], cap[1]);
        }
    }

    public abstract float computeGp(GeneratorTile tile, Level level);

    public float applyEfficiencyLoss(float raw) {
        if (efficiencyCaps.isEmpty()) return raw;
        float result = 0f;
        float remaining = raw;
        Float prev = 0f;
        for (var entry : efficiencyCaps.entrySet()) {
            float threshold = entry.getKey();
            float efficiency = entry.getValue();
            float band = Math.min(remaining, threshold - prev);
            if (band <= 0) break;
            result += band * efficiency;
            remaining -= band;
            prev = threshold;
            if (remaining <= 0) break;
        }
        if (remaining > 0) {
            float lastEff = efficiencyCaps.lastEntry().getValue();
            result += remaining * lastEff;
        }
        return result;
    }

    private static float[] cap(float threshold, float efficiency) {
        return new float[]{threshold, efficiency};
    }

    private static boolean isDaytime(Level level) {
        float angle = (float)(Mth.frac(level.dayTime() / 24000.0 - 0.25));
        return Mth.cos(angle * (float)(Math.PI * 2)) >= 0f;
    }

    public boolean hasEfficiencyLoss() {
        return !efficiencyCaps.isEmpty();
    }

    public TreeMap<Float, Float> getCaps() {
        return efficiencyCaps;
    }
}
