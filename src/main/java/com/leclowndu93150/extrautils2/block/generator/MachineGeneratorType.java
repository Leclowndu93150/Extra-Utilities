package com.leclowndu93150.extrautils2.block.generator;

import com.leclowndu93150.extrautils2.registry.ModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

import java.util.HashSet;
import java.util.Set;

public enum MachineGeneratorType {
    FURNACE(0xFFFFFF, null, null, null, "furnace_generator", 10000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(burnTime, 40f);
        }
    },
    SURVIVALIST(0xFFFFFF, null, null, null, "survival_generator", 10000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(burnTime * 5, 5f);
        }
    },
    CULINARY(0xFFFFFF, "machine/generator/generator_culinary", null, null, "culinary_generator", 100000, 8000) {
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
    POTION(0x540F91, "machine/generator/generator_potion", null, null, "potion_generator", 100000, 1000) {
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
    TNT(0xDB591A, "machine/generator/generator_tnt", null, null, "explosive_generator", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }

        @Override
        public void processingTick(Level level, BlockPos pos, int speedLevel) {
            var rand = level.getRandom();
            int n = 1 + speedLevel;
            for (int j = 0; j < n; j++) {
                if (rand.nextInt(41) == 0) {
                    for (int i = 0; i < 10; i++) {
                        double v = 1.5;
                        double x = pos.getX() + 0.5 + v * rand.nextGaussian();
                        double y = pos.getY() + 0.5 + v * rand.nextGaussian();
                        double z = pos.getZ() + 0.5 + v * rand.nextGaussian();
                        BlockPos other = BlockPos.containing(x, y, z);
                        if (i == 9 || !pos.equals(other)) {
                            BlockState state = level.getBlockState(other);
                            if (i >= 5 || state.getExplosionResistance(level, other, null) == 0.0f) {
                                level.explode(null, x, y, z, 2.0f, Level.ExplosionInteraction.NONE);
                                return;
                            }
                        }
                    }
                }
            }
        }
    },
    LAVA(0x990A22, null, null, null, "magmatic_generator", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesFluid() { return true; }

        @Override
        public boolean usesRecipes() { return true; }
    },
    PINK(0xFF9DB0, "machine/generator/generator_pink", null, null, "pink_generator", 100000, 100) {
        private volatile Set<Item> pinkItems;

        private Set<Item> getPinkItems() {
            if (pinkItems == null) {
                Set<Item> set = new HashSet<>();
                for (var item : BuiltInRegistries.ITEM) {
                    var key = BuiltInRegistries.ITEM.getKey(item);
                    if (key != null && key.getPath().contains("pink")) {
                        set.add(item);
                    }
                }
                pinkItems = set;
            }
            return pinkItems;
        }

        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            if (stack.is(Items.PINK_DYE)) return new FuelResult(4000, 100f);
            if (getPinkItems().contains(stack.getItem())) return new FuelResult(400, 40f);
            return null;
        }
    },
    NETHERSTAR(0xFFFFFF, "machine/generator/generator_netherstar",
            "machine/generator/machine_base_netherstar_side", "machine/generator/machine_base_netherstar_bottom", "nether_star_generator", 400000, 400000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }

        @Override
        public void processingTick(Level level, BlockPos pos, int speedLevel) {
            int n = 1 + speedLevel;
            AABB area = new AABB(pos).inflate(5.0);
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
                if (!(entity instanceof Endermite)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 410,
                            (int) Math.floor(Math.sqrt(5 + n)) - 1));
                }
            }
            if (level instanceof ServerLevel serverLevel) {
                spawnEffectParticles(serverLevel, area, 2, 0.1f, 0.1f, 0.1f);
            }
        }
    },
    ENDER(0x2596B4, "machine/generator/generator_ender", null, null, "ender_generator", 100000, 4000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }
    },
    REDSTONE(0xAA6E23, "machine/generator/generator_redstone", null, null, "heated_redstone_generator", 100000, 1600) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesFluid() { return true; }

        @Override
        public boolean usesRecipes() { return true; }
    },
    OVERCLOCK(0x1B0B90, "machine/generator/generator_overclock", null, null, "overclocked_generator", 1000000, 1000000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) {
            int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            if (burnTime <= 0) return null;
            return new FuelResult(Math.max(1, burnTime / 10), 4000f);
        }
    },
    DRAGON(0xA74BA7, null, null, null, "halitosis_generator", 1000000, 8000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }

        @Override
        public void processingTick(Level level, BlockPos pos, int speedLevel) {
            if (level.getDifficulty() == Difficulty.PEACEFUL) return;
            if (level.getRandom().nextInt(1200) != 0) return;
            var rand = level.getRandom();
            double x = pos.getX() + 0.5 + rand.nextGaussian() * 3.0;
            double y = pos.getY() + 0.5 + rand.nextGaussian() * 3.0;
            double z = pos.getZ() + 0.5 + rand.nextGaussian() * 3.0;
            Endermite mite = new Endermite(EntityType.ENDERMITE, level);
            mite.moveTo(x, y, z, rand.nextFloat() * 360.0f, 0.0f);
            if (!level.noCollision(mite)) return;
            level.addFreshEntity(mite);
        }
    },
    ICE(0x4E4FDF, null, null, null, "frosty_generator", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }

        @Override
        public void processingTick(Level level, BlockPos pos, int speedLevel) {
            var rand = level.getRandom();
            int n = 1 + speedLevel;
            for (int i = 0; i < n; i++) {
                if (rand.nextInt(1024) != 0) continue;
                for (int attempt = 0; attempt < 40; attempt++) {
                    BlockPos target = pos.offset(
                            rand.nextInt(19) - 9,
                            rand.nextInt(19) - 9,
                            rand.nextInt(19) - 9);
                    if (target.distSqr(pos) > 81) continue;
                    BlockState state = level.getBlockState(target);
                    if (state.isAir()) {
                        if (Blocks.SNOW.defaultBlockState().canSurvive(level, target)) {
                            level.setBlock(target, Blocks.SNOW.defaultBlockState(), 3);
                            break;
                        }
                    } else if (state.is(Blocks.WATER)) {
                        level.setBlock(target, Blocks.ICE.defaultBlockState(), 3);
                        break;
                    } else if (state.is(Blocks.SNOW)) {
                        int layers = state.getValue(SnowLayerBlock.LAYERS);
                        double dist = Math.sqrt(target.distSqr(pos));
                        if (layers < 8 && (9.0 - dist) * 1.25 > layers) {
                            level.setBlock(target, state.setValue(
                                    SnowLayerBlock.LAYERS, layers + 1), 3);
                            break;
                        }
                    }
                }
            }
        }
    },
    DEATH(0xD8A63C, "machine/generator/generator_death", null, null, "death_generator", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean usesRecipes() { return true; }

        @Override
        public void processingTick(Level level, BlockPos pos, int speedLevel) {
            AABB area = new AABB(pos).inflate(1.0);
            for (Player player : level.getEntitiesOfClass(Player.class, area)) {
                if (!player.hasEffect(ModMobEffects.DOOM)) {
                    player.addEffect(new MobEffectInstance(ModMobEffects.DOOM, 1200, 0));
                }
            }
            if (level instanceof ServerLevel serverLevel) {
                spawnEffectParticles(serverLevel, area, 4, 0.7f, 0.1f, 0.1f);
            }
        }
    },
    ENCHANT(0x3C3F76, "machine/generator/generator_enchant", null, null, "disenchantment_generator", 100000, 1000) {
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
            "machine/generator/machine_base_slime_side", "machine/generator/machine_base_slime_bottom", "slimey_generator", 100000, 1000) {
        @Override
        public FuelResult getFuelResult(ItemStack stack) { return null; }

        @Override
        public boolean needsSecondarySlot() { return true; }

        @Override
        public boolean usesRecipes() { return true; }

    };

    public final int color;
    public final @Nullable String overlayTexture;
    public final @Nullable String sideTex;
    public final @Nullable String bottomTex;
    public final String textureFolder;
    public final int energyCapacity;
    public final int maxExtract;

    MachineGeneratorType(int color, @Nullable String overlayTexture, @Nullable String sideTex, @Nullable String bottomTex,
                         String textureFolder, int energyCapacity, int maxExtract) {
        this.color = color;
        this.overlayTexture = overlayTexture;
        this.sideTex = sideTex;
        this.bottomTex = bottomTex;
        this.textureFolder = textureFolder;
        this.energyCapacity = energyCapacity;
        this.maxExtract = maxExtract;
    }

    public abstract FuelResult getFuelResult(ItemStack stack);

    public boolean usesFluid() { return false; }
    public boolean needsSecondarySlot() { return false; }
    public boolean usesRecipes() { return false; }
    public String getFrontTexture() { return "machines/" + textureFolder + "/front"; }
    public String getOnFrontTexture() { return "machines/" + textureFolder + "/front_on"; }
    public String getSideTexture() { return "machines/" + textureFolder + "/side"; }
    public String getTopTexture() { return "machines/" + textureFolder + "/top"; }
    public String getPreviewBaseTexture() { return "machines/" + textureFolder + "/side"; }
    public void processingTick(Level level, BlockPos pos, int speedLevel) {}

    protected static void spawnEffectParticles(ServerLevel level, AABB area, int count, float r, float g, float b) {
        var rand = level.getRandom();
        var particle = ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, r, g, b);
        for (int i = 0; i < count; i++) {
            double x = area.minX + (area.maxX - area.minX) * rand.nextFloat();
            double y = area.minY + (area.maxY - area.minY) * rand.nextFloat();
            double z = area.minZ + (area.maxZ - area.minZ) * rand.nextFloat();
            level.sendParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public record FuelResult(int totalEnergy, float gpRate) {}
}
