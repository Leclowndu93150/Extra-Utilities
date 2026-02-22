package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class XUItemModelProvider extends ItemModelProvider {
    public XUItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, ExtraUtilities.MODID, efh);
    }

    private ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path);
    }

    @Override
    protected void registerModels() {
        blockItem(ModBlocks.ANGEL_BLOCK.get());
        blockItem(ModBlocks.CURSED_EARTH.get());
        blockItem(ModBlocks.MOON_STONE.get());
        blockItem(ModBlocks.DECORATIVE_SOLID.get());
        blockItem(ModBlocks.DECORATIVE_SOLID_WOOD.get());
        blockItem(ModBlocks.DECORATIVE_BEDROCK.get());
        blockItem(ModBlocks.OPINIUM_BLOCK.get());
        blockItem(ModBlocks.REDSTONE_LANTERN.get());
        blockItem(ModBlocks.SOUND_MUFFLER.get());
        blockItem(ModBlocks.REDSTONE_CLOCK.get());

        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_1.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_2.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_3.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_4.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_5.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_6.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_7.get());
        blockItem(ModBlocks.COMPRESSED_COBBLESTONE_8.get());
        blockItem(ModBlocks.COMPRESSED_DIRT_1.get());
        blockItem(ModBlocks.COMPRESSED_DIRT_2.get());
        blockItem(ModBlocks.COMPRESSED_DIRT_3.get());
        blockItem(ModBlocks.COMPRESSED_DIRT_4.get());
        blockItem(ModBlocks.COMPRESSED_SAND_1.get());
        blockItem(ModBlocks.COMPRESSED_SAND_2.get());
        blockItem(ModBlocks.COMPRESSED_GRAVEL_1.get());
        blockItem(ModBlocks.COMPRESSED_GRAVEL_2.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_1.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_2.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_3.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_4.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_5.get());
        blockItem(ModBlocks.COMPRESSED_NETHERRACK_6.get());

        blockItem(ModBlocks.GENERATOR_SOLAR.get());
        blockItem(ModBlocks.GENERATOR_LUNAR.get());
        blockItem(ModBlocks.GENERATOR_LAVA.get());
        blockItem(ModBlocks.GENERATOR_WATER.get());
        blockItem(ModBlocks.GENERATOR_WIND.get());
        blockItem(ModBlocks.GENERATOR_FIRE.get());
        blockItem(ModBlocks.GENERATOR_PLAYER_WIND_UP.get());
        blockItem(ModBlocks.GENERATOR_DRAGON_EGG.get());
        blockItem(ModBlocks.GENERATOR_CREATIVE.get());

        blockItem(ModBlocks.TRASH_CAN.get());
        blockItem(ModBlocks.TRASH_CAN_FLUID.get());
        blockItem(ModBlocks.TRASH_CAN_ENERGY.get());
        blockItem(ModBlocks.RESONATOR.get());
        blockItem(ModBlocks.CRAFTER.get());
        blockItem(ModBlocks.ANALOG_CRAFTER.get());
        // drums use manual item models for display transforms
        blockItem(ModBlocks.SCREEN.get());
        blockItem(ModBlocks.SPOTLIGHT.get());
        blockItem(ModBlocks.POWER_TRANSMITTER.get());
        blockItem(ModBlocks.POWER_BATTERY.get());
        blockItem(ModBlocks.INDEXER.get());
        blockItem(ModBlocks.PLAYER_CHEST.get());
        blockItem(ModBlocks.CREATIVE_CHEST.get());
        blockItem(ModBlocks.CREATIVE_HARVEST.get());
        blockItem(ModBlocks.MINER.get());
        blockItem(ModBlocks.SCANNER.get());
        blockItem(ModBlocks.USER.get());
        blockItem(ModBlocks.QUARRY.get());
        blockItem(ModBlocks.TELEPORTER.get());
        blockItem(ModBlocks.SUPER_MOB_SPAWNER.get());
        blockItem(ModBlocks.TERRAFORMER.get());
        blockItem(ModBlocks.POWER_OVERLOAD.get());
        blockItem(ModBlocks.RAINBOW_GENERATOR.get());
        blockItem(ModBlocks.SYNERGY_UNIT.get());

        handheld("golden_bag", tex("bag_of_holding"));
    }

    private void blockItem(Block block) {
        String path = block.builtInRegistryHolder().key().location().getPath();
        withExistingParent(path, ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path));
    }

    private void handheld(String name, ResourceLocation texture) {
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/handheld"))
                .texture("layer0", texture);
    }
}
