package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import net.minecraft.core.Direction;
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

        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_FURNACE.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_SURVIVALIST.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_CULINARY.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_POTION.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_TNT.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_LAVA.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_PINK.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_NETHERSTAR.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_ENDER.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_REDSTONE.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_OVERCLOCK.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_DRAGON.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_ICE.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_DEATH.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_ENCHANT.get());
        machineGeneratorItem(ModBlocks.MACHINE_GENERATOR_SLIME.get());

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

        for (String type : AngelRingItem.WING_TYPES) {
            handheld("angel_ring_" + type, tex("angelring_" + type));
        }
    }

    private void blockItem(Block block) {
        String path = block.builtInRegistryHolder().key().location().getPath();
        withExistingParent(path, ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path));
    }

    private void machineGeneratorItem(Block block) {
        MachineGeneratorType type = ((MachineGeneratorBlock) block).generatorType;
        String path = block.builtInRegistryHolder().key().location().getPath();
        String side   = type.sideTex   != null ? type.sideTex   : "machine/machine_base_white_side";
        String bottom = type.bottomTex != null ? type.bottomTex : "machine/machine_base_white_bottom";

        var builder = getBuilder(path)
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .texture("bottom", tex(bottom))
                .texture("side", tex(side))
                .texture("front", tex("machine/generator_off"))
                .renderType("minecraft:cutout");

        builder.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).tintindex(1).end()
                .face(Direction.UP).texture("#bottom").cullface(Direction.UP).tintindex(1).end()
                .face(Direction.NORTH).texture("#bottom").cullface(Direction.NORTH).tintindex(1).end()
                .face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).tintindex(1).end()
                .face(Direction.WEST).texture("#side").cullface(Direction.WEST).tintindex(1).end()
                .face(Direction.EAST).texture("#side").cullface(Direction.EAST).tintindex(1).end()
                .end();

        // Slightly in front of the north face to avoid z-fighting in item renders.
        builder.element()
                .from(0, 0, -0.01f).to(16, 16, 0)
                .face(Direction.NORTH).texture("#front").end()
                .end();

        if (type.overlayTexture == null) {
            return;
        }

        builder.texture("overlay", tex(type.overlayTexture));
        builder.element()
                .from(0, 16, 0).to(16, 16.01f, 16)
                .face(Direction.UP).texture("#overlay").end()
                .end();
    }

    private void handheld(String name, ResourceLocation texture) {
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/handheld"))
                .texture("layer0", texture);
    }
}
