package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class XULootTableProvider extends LootTableProvider {
    public XULootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(XUBlockLoot::new, LootContextParamSets.BLOCK)
        ), lookupProvider);
    }

    static class XUBlockLoot extends BlockLootSubProvider {
        protected XUBlockLoot(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            dropSelf(ModBlocks.ANGEL_BLOCK.get());
            dropSelf(ModBlocks.CURSED_EARTH.get());
            dropSelf(ModBlocks.MOON_STONE.get());
            dropSelf(ModBlocks.DECORATIVE_SOLID.get());
            dropSelf(ModBlocks.DECORATIVE_SOLID_WOOD.get());
            dropSelf(ModBlocks.DECORATIVE_BEDROCK.get());
            dropSelf(ModBlocks.OPINIUM_BLOCK.get());
            dropSelf(ModBlocks.REDSTONE_LANTERN.get());
            dropSelf(ModBlocks.SOUND_MUFFLER.get());
            dropSelf(ModBlocks.REDSTONE_CLOCK.get());

            dropSelf(ModBlocks.SPIKE_WOOD.get());
            dropSelf(ModBlocks.SPIKE_STONE.get());
            dropSelf(ModBlocks.SPIKE_IRON.get());
            dropSelf(ModBlocks.SPIKE_GOLD.get());
            dropSelf(ModBlocks.SPIKE_DIAMOND.get());
            dropSelf(ModBlocks.SPIKE_CREATIVE.get());

            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_1.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_2.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_3.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_4.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_5.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_6.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_7.get());
            dropSelf(ModBlocks.COMPRESSED_COBBLESTONE_8.get());
            dropSelf(ModBlocks.COMPRESSED_DIRT_1.get());
            dropSelf(ModBlocks.COMPRESSED_DIRT_2.get());
            dropSelf(ModBlocks.COMPRESSED_DIRT_3.get());
            dropSelf(ModBlocks.COMPRESSED_DIRT_4.get());
            dropSelf(ModBlocks.COMPRESSED_SAND_1.get());
            dropSelf(ModBlocks.COMPRESSED_SAND_2.get());
            dropSelf(ModBlocks.COMPRESSED_GRAVEL_1.get());
            dropSelf(ModBlocks.COMPRESSED_GRAVEL_2.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_1.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_2.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_3.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_4.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_5.get());
            dropSelf(ModBlocks.COMPRESSED_NETHERRACK_6.get());

            dropSelf(ModBlocks.MACHINE_GENERATOR_FURNACE.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_SURVIVALIST.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_CULINARY.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_POTION.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_TNT.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_LAVA.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_PINK.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_NETHERSTAR.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_ENDER.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_REDSTONE.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_OVERCLOCK.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_DRAGON.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_ICE.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_DEATH.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_ENCHANT.get());
            dropSelf(ModBlocks.MACHINE_GENERATOR_SLIME.get());

            dropSelf(ModBlocks.GENERATOR_SOLAR.get());
            dropSelf(ModBlocks.GENERATOR_LUNAR.get());
            dropSelf(ModBlocks.GENERATOR_LAVA.get());
            dropSelf(ModBlocks.GENERATOR_WATER.get());
            dropSelf(ModBlocks.GENERATOR_WIND.get());
            dropSelf(ModBlocks.GENERATOR_FIRE.get());
            dropSelf(ModBlocks.GENERATOR_PLAYER_WIND_UP.get());
            dropSelf(ModBlocks.GENERATOR_DRAGON_EGG.get());
            dropSelf(ModBlocks.GENERATOR_CREATIVE.get());

            dropSelf(ModBlocks.TRASH_CAN.get());
            dropSelf(ModBlocks.TRASH_CAN_FLUID.get());
            dropSelf(ModBlocks.TRASH_CAN_ENERGY.get());
            dropSelf(ModBlocks.RESONATOR.get());
            dropSelf(ModBlocks.CRAFTER.get());
            dropSelf(ModBlocks.ANALOG_CRAFTER.get());
            dropSelf(ModBlocks.DRUM_16.get());
            dropSelf(ModBlocks.DRUM_256.get());
            dropSelf(ModBlocks.DRUM_4096.get());
            dropSelf(ModBlocks.DRUM_65536.get());
            dropSelf(ModBlocks.DRUM_CREATIVE.get());
            dropSelf(ModBlocks.SCREEN.get());
            dropSelf(ModBlocks.SPOTLIGHT.get());
            dropSelf(ModBlocks.POWER_TRANSMITTER.get());
            dropSelf(ModBlocks.POWER_BATTERY.get());
            dropSelf(ModBlocks.INDEXER.get());
            dropSelf(ModBlocks.PLAYER_CHEST.get());
            dropSelf(ModBlocks.CREATIVE_CHEST.get());
            add(ModBlocks.CREATIVE_HARVEST.get(), noDrop());
            dropSelf(ModBlocks.CREATIVE_ENERGY.get());
            dropSelf(ModBlocks.MINER.get());
            dropSelf(ModBlocks.SCANNER.get());
            dropSelf(ModBlocks.USER.get());
            dropSelf(ModBlocks.QUARRY.get());
            dropSelf(ModBlocks.TELEPORTER.get());
            dropSelf(ModBlocks.SUPER_MOB_SPAWNER.get());
            dropSelf(ModBlocks.TERRAFORMER.get());
            dropSelf(ModBlocks.POWER_OVERLOAD.get());
            dropSelf(ModBlocks.RAINBOW_GENERATOR.get());
            dropSelf(ModBlocks.SYNERGY_UNIT.get());
            dropSelf(ModBlocks.MACHINE_FURNACE.get());
            dropSelf(ModBlocks.MACHINE_CRUSHER.get());
            dropSelf(ModBlocks.MACHINE_ENCHANTER.get());

            dropWhenSilkTouch(ModBlocks.DECORATIVE_GLASS.get());
            dropWhenSilkTouch(ModBlocks.DECORATIVE_GLASS_BORDERED.get());
            dropWhenSilkTouch(ModBlocks.DECORATIVE_GLASS_DIAMONDS.get());
            dropWhenSilkTouch(ModBlocks.DARK_GLASS.get());
            dropWhenSilkTouch(ModBlocks.GLOWSTONE_GLASS.get());
            dropWhenSilkTouch(ModBlocks.REDSTONE_GLASS.get());
            dropWhenSilkTouch(ModBlocks.INEFFABLE_GLASS.get());
            dropWhenSilkTouch(ModBlocks.INEFFABLE_GLASS_REVERSE.get());
            dropWhenSilkTouch(ModBlocks.INEFFABLE_GLASS_CLEAR.get());
            dropWhenSilkTouch(ModBlocks.INEFFABLE_GLASS_DARK.get());

            dropSelf(ModBlocks.ENCHANTED_BLOCK.get());
            dropSelf(ModBlocks.DEMON_BLOCK.get());
            dropSelf(ModBlocks.EVIL_INFUSED_INGOT_BLOCK.get());

            dropSelf(ModBlocks.LARGISH_CHEST.get());
            dropSelf(ModBlocks.MINI_CHEST.get());
            dropSelf(ModBlocks.TRASH_CHEST.get());
            dropSelf(ModBlocks.KLEIN_BOTTLE.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModRegistries.BLOCKS.getEntries().stream()
                    .<Block>map(DeferredHolder::get)
                    .toList();
        }
    }
}
