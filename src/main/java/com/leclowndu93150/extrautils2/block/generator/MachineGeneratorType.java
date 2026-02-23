package com.leclowndu93150.extrautils2.block.generator;

import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

public enum MachineGeneratorType {
    FURNACE(0xFFFFFF, null, null, null, 10000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(burnTime, 40f);
        }
    },
    SURVIVALIST(0xFFFFFF, null, null, null, 10000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(burnTime * 5, 5f);
        }
    },
    CULINARY(0xFFFFFF, "machine/generator/generator_culinary", null, null, 100000, 8000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            var food = stack.getFoodProperties(null);
            if (food == null) return null;
            int nutrition = food.nutrition();
            float saturation = food.saturation();
            int energy = (int) (nutrition * saturation * 2000);
            if (energy <= 0) return null;
            return new FuelResult(energy, 8000f);
        }
    },
    POTION(0x54239F, "machine/generator/generator_potion", null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (!(stack.getItem() instanceof PotionItem)) return null;
            var contents = stack.get(DataComponents.POTION_CONTENTS);
            if (contents == null || contents.potion().isEmpty()) return null;
            var potion = contents.potion().get().value();
            int duration = potion.getEffects().stream()
                    .mapToInt(MobEffectInstance::getDuration).sum();
            if (duration <= 0) return null;
            return new FuelResult(duration * 100, 1000f);
        }
    },
    TNT(0xDB6A0A, "machine/generator/generator_tnt", null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Blocks.TNT.asItem())) return new FuelResult(512000, 160f);
            if (stack.is(Items.GUNPOWDER)) return new FuelResult(64000, 160f);
            return null;
        }
    },
    LAVA(0x99A402, null, null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesFluid() { return true; }
    },
    PINK(0xFF9FB0, "machine/generator/generator_pink", null, null, 100000, 100) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.PINK_DYE)) return new FuelResult(4000, 100f);
            if (stack.is(Blocks.PINK_TULIP.asItem())) return new FuelResult(400, 40f);
            if (stack.is(Blocks.PEONY.asItem())) return new FuelResult(400, 40f);
            return null;
        }
    },
    NETHERSTAR(0xFFFFFF, "machine/generator/generator_netherstar",
            "machine/generator/machine_base_netherstar_side", "machine/generator/machine_base_netherstar_bottom", 400000, 400000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.NETHER_STAR)) return new FuelResult(9600000, 4000f);
            return null;
        }
    },
    ENDER(0x258314, "machine/generator/generator_ender", null, null, 100000, 4000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.ENDER_PEARL)) return new FuelResult(64000, 40f);
            if (stack.is(Items.ENDER_EYE)) return new FuelResult(256000, 80f);
            return null;
        }
    },
    REDSTONE(0xAA5B43, "machine/generator/generator_redstone", null, null, 100000, 1600) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.REDSTONE)) return new FuelResult(20000, 160f);
            return null;
        }

        @Override
        public boolean usesFluid() { return true; }
    },
    OVERCLOCK(0x1B0310, "machine/generator/generator_overclock", null, null, 1000000, 1000000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(Math.max(1, burnTime / 10), 4000f);
        }
    },
    DRAGON(0xA73767, null, null, null, 1000000, 8000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.DRAGON_BREATH)) return new FuelResult(480000, 40f);
            return null;
        }
    },
    ICE(0x4E5FDF, null, null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Blocks.ICE.asItem())) return new FuelResult(1600, 40f);
            if (stack.is(Blocks.PACKED_ICE.asItem())) return new FuelResult(1600, 40f);
            if (stack.is(Items.SNOWBALL)) return new FuelResult(200, 40f);
            if (stack.is(Blocks.SNOW_BLOCK.asItem())) return new FuelResult(800, 40f);
            return null;
        }
    },
    DEATH(0xD8760C, "machine/generator/generator_death", null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.BONE)) return new FuelResult(16000, 1000f);
            if (stack.is(Blocks.BONE_BLOCK.asItem())) return new FuelResult(48000, 1000f);
            if (stack.is(Items.ROTTEN_FLESH)) return new FuelResult(8000, 1000f);
            if (stack.is(Items.WITHER_SKELETON_SKULL)) return new FuelResult(60000, 1000f);
            return null;
        }
    },
    ENCHANT(0x3C3FB6, "machine/generator/generator_enchant", null, null, 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (!stack.isEnchanted()) return null;
            int totalLevels = stack.getEnchantments().entrySet().stream()
                    .mapToInt(e -> e.getIntValue())
                    .sum();
            if (totalLevels <= 0) return null;
            return new FuelResult(totalLevels * 5000, 1000f);
        }
    },
    SLIME(0xFFFFFF, null,
            "machine/generator/machine_base_slime_side", "machine/generator/machine_base_slime_bottom", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean needsSecondarySlot() { return true; }
    };

    public final int color;
    public final @Nullable String overlayTexture;
    public final @Nullable String sideTex;
    public final @Nullable String bottomTex;
    public final int energyCapacity;
    public final int maxExtract;

    MachineGeneratorType(int color, @Nullable String overlayTexture, @Nullable String sideTex, @Nullable String bottomTex,
                         int energyCapacity, int maxExtract) {
        this.color = color;
        this.overlayTexture = overlayTexture;
        this.sideTex = sideTex;
        this.bottomTex = bottomTex;
        this.energyCapacity = energyCapacity;
        this.maxExtract = maxExtract;
    }

    public abstract FuelResult getFuelResult(ItemStack stack);

    public boolean usesFluid() { return false; }
    public boolean needsSecondarySlot() { return false; }

    public record FuelResult(int totalEnergy, float gpRate) {}
}
