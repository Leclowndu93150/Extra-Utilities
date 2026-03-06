package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class XUBlockTagProvider extends BlockTagsProvider {
    public XUBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh) {
        super(output, lookupProvider, ExtraUtilities.MODID, efh);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.ANGEL_BLOCK.get())
                .add(ModBlocks.MOON_STONE.get())
                .add(ModBlocks.DECORATIVE_SOLID.get())
                .add(ModBlocks.DECORATIVE_BEDROCK.get())
                .add(ModBlocks.OPINIUM_BLOCK.get())
                .add(ModBlocks.REDSTONE_LANTERN.get())
                .add(ModBlocks.SOUND_MUFFLER.get())
                .add(ModBlocks.REDSTONE_CLOCK.get())
                .add(ModBlocks.SPIKE_STONE.get())
                .add(ModBlocks.SPIKE_IRON.get())
                .add(ModBlocks.SPIKE_GOLD.get())
                .add(ModBlocks.SPIKE_DIAMOND.get())
                .add(ModBlocks.SPIKE_COPPER.get())
                .add(ModBlocks.SPIKE_NETHERITE.get())
                .add(ModBlocks.SPIKE_CREATIVE.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_1.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_2.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_3.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_4.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_5.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_6.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_7.get())
                .add(ModBlocks.COMPRESSED_COBBLESTONE_8.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_1.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_2.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_3.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_4.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_5.get())
                .add(ModBlocks.COMPRESSED_NETHERRACK_6.get())
                .add(ModBlocks.GENERATOR_SOLAR.get())
                .add(ModBlocks.GENERATOR_LUNAR.get())
                .add(ModBlocks.GENERATOR_LAVA.get())
                .add(ModBlocks.GENERATOR_WATER.get())
                .add(ModBlocks.GENERATOR_WIND.get())
                .add(ModBlocks.GENERATOR_FIRE.get())
                .add(ModBlocks.GENERATOR_PLAYER_WIND_UP.get())
                .add(ModBlocks.GENERATOR_DRAGON_EGG.get())
                .add(ModBlocks.GENERATOR_CREATIVE.get())
                .add(ModBlocks.TRASH_CAN.get())
                .add(ModBlocks.TRASH_CAN_FLUID.get())
                .add(ModBlocks.TRASH_CAN_ENERGY.get())
                .add(ModBlocks.RESONATOR.get())
                .add(ModBlocks.CRAFTER.get())
                .add(ModBlocks.ANALOG_CRAFTER.get())
                .add(ModBlocks.DRUM_16.get())
                .add(ModBlocks.DRUM_256.get())
                .add(ModBlocks.DRUM_4096.get())
                .add(ModBlocks.DRUM_65536.get())
                .add(ModBlocks.DRUM_CREATIVE.get())
                .add(ModBlocks.SCREEN.get())
                .add(ModBlocks.SPOTLIGHT.get())
                .add(ModBlocks.POWER_TRANSMITTER.get())
                .add(ModBlocks.POWER_BATTERY.get())
                .add(ModBlocks.INDEXER.get())
                .add(ModBlocks.PLAYER_CHEST.get())
                .add(ModBlocks.CREATIVE_CHEST.get())
                .add(ModBlocks.CREATIVE_HARVEST.get())
                .add(ModBlocks.MINER.get())
                .add(ModBlocks.SCANNER.get())
                .add(ModBlocks.USER.get())
                .add(ModBlocks.QUARRY.get())
                .add(ModBlocks.TELEPORTER.get())
                .add(ModBlocks.SUPER_MOB_SPAWNER.get())
                .add(ModBlocks.TERRAFORMER.get())
                .add(ModBlocks.POWER_OVERLOAD.get())
                .add(ModBlocks.RAINBOW_GENERATOR.get())
                .add(ModBlocks.SYNERGY_UNIT.get())
                .add(ModBlocks.TRANSFER_PIPE.get())
                .add(ModBlocks.TRANSFER_NODE_ITEMS.get())
                .add(ModBlocks.RETRIEVAL_NODE_ITEMS.get())
                .add(ModBlocks.TRANSFER_NODE_FLUIDS.get())
                .add(ModBlocks.RETRIEVAL_NODE_FLUIDS.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SPIKE_WOOD.get())
                .add(ModBlocks.DECORATIVE_SOLID_WOOD.get())
                .add(ModBlocks.MAGICAL_WOOD.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.CURSED_EARTH.get())
                .add(ModBlocks.COMPRESSED_DIRT_1.get())
                .add(ModBlocks.COMPRESSED_DIRT_2.get())
                .add(ModBlocks.COMPRESSED_DIRT_3.get())
                .add(ModBlocks.COMPRESSED_DIRT_4.get())
                .add(ModBlocks.COMPRESSED_SAND_1.get())
                .add(ModBlocks.COMPRESSED_SAND_2.get())
                .add(ModBlocks.COMPRESSED_GRAVEL_1.get())
                .add(ModBlocks.COMPRESSED_GRAVEL_2.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.SPIKE_COPPER.get())
                .add(ModBlocks.SPIKE_IRON.get())
                .add(ModBlocks.SPIKE_GOLD.get())
                .add(ModBlocks.RESONATOR.get())
                .add(ModBlocks.CRAFTER.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SPIKE_DIAMOND.get())
                .add(ModBlocks.SPIKE_NETHERITE.get())
                .add(ModBlocks.GENERATOR_SOLAR.get())
                .add(ModBlocks.GENERATOR_LUNAR.get())
                .add(ModBlocks.GENERATOR_LAVA.get())
                .add(ModBlocks.GENERATOR_WATER.get())
                .add(ModBlocks.GENERATOR_WIND.get())
                .add(ModBlocks.GENERATOR_FIRE.get())
                .add(ModBlocks.GENERATOR_PLAYER_WIND_UP.get())
                .add(ModBlocks.GENERATOR_DRAGON_EGG.get())
                .add(ModBlocks.QUARRY.get());
    }
}
