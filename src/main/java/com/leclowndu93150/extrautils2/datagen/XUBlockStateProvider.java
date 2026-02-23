package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.RedstoneClockBlock;
import com.leclowndu93150.extrautils2.block.SpikeBlock;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.ObjModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class XUBlockStateProvider extends BlockStateProvider {
    public XUBlockStateProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, ExtraUtilities.MODID, efh);
    }

    private ResourceLocation tex(String path) {
        return modLoc("block/" + path);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.ANGEL_BLOCK.get(), cubeAll(ModBlocks.ANGEL_BLOCK.get(), "angel_block"));
        simpleBlock(ModBlocks.CURSED_EARTH.get(), models().cubeBottomTop(
                name(ModBlocks.CURSED_EARTH.get()),
                tex("cursedearthside"),
                tex("cursedearthbottom"),
                tex("cursedearthside")
        ));
        simpleBlock(ModBlocks.MOON_STONE.get(), cubeAll(ModBlocks.MOON_STONE.get(), "moon_stone"));
        simpleBlock(ModBlocks.DECORATIVE_SOLID.get(), cubeAll(ModBlocks.DECORATIVE_SOLID.get(), "connected/borderstone"));
        simpleBlock(ModBlocks.DECORATIVE_SOLID_WOOD.get(), cubeAll(ModBlocks.DECORATIVE_SOLID_WOOD.get(), "connected/diagonalwood"));
        simpleBlock(ModBlocks.DECORATIVE_BEDROCK.get(), cubeAll(ModBlocks.DECORATIVE_BEDROCK.get(), "bedrock_bricks"));
        simpleBlock(ModBlocks.OPINIUM_BLOCK.get(), cubeAll(ModBlocks.OPINIUM_BLOCK.get(), "demon_block"));
        simpleBlock(ModBlocks.REDSTONE_LANTERN.get(), cubeAll(ModBlocks.REDSTONE_LANTERN.get(), "redstone_lantern"));
        simpleBlock(ModBlocks.SOUND_MUFFLER.get(), cubeAll(ModBlocks.SOUND_MUFFLER.get(), "sound_muffler"));
        ModelFile clockOff = models().cubeAll("redstone_clock", tex("redstone_clock_off"));
        ModelFile clockOn = models().cubeAll("redstone_clock_on", tex("redstone_clock_on"));
        getVariantBuilder(ModBlocks.REDSTONE_CLOCK.get())
                .partialState().with(RedstoneClockBlock.POWER_STATE, RedstoneClockBlock.PowerState.DISABLED)
                .modelForState().modelFile(clockOff).addModel()
                .partialState().with(RedstoneClockBlock.POWER_STATE, RedstoneClockBlock.PowerState.ENABLED_NOT_POWERED)
                .modelForState().modelFile(clockOn).addModel()
                .partialState().with(RedstoneClockBlock.POWER_STATE, RedstoneClockBlock.PowerState.ENABLED_POWERED)
                .modelForState().modelFile(clockOn).addModel();

        spikes();
        compressedBlocks();
        generators();
        machines();
    }

    private void spikes() {
        spikeBlock(ModBlocks.SPIKE_WOOD.get(), "wood");
        spikeBlock(ModBlocks.SPIKE_STONE.get(), "stone");
        spikeBlock(ModBlocks.SPIKE_IRON.get(), "iron");
        spikeBlock(ModBlocks.SPIKE_GOLD.get(), "gold");
        spikeBlock(ModBlocks.SPIKE_DIAMOND.get(), "diamond");
        spikeBlock(ModBlocks.SPIKE_CREATIVE.get(), "creative");
    }

    private void spikeBlock(Block block, String type) {
        String blockName = name(block);
        ModelFile model = models().getBuilder(blockName)
                .customLoader(ObjModelBuilder::begin)
                .modelLocation(modLoc("models/block/spike.obj"))
                .overrideMaterialLibrary(modLoc("models/block/spike_" + type + ".mtl"))
                .automaticCulling(false)
                .shadeQuads(true)
                .flipV(true)
                .end()
                .renderType("solid");

        getVariantBuilder(block).forAllStates(state -> {
            Direction facing = state.getValue(SpikeBlock.FACING);
            int xRot = 0, yRot = 0;
            switch (facing) {
                case UP -> xRot = 0;
                case DOWN -> xRot = 180;
                case NORTH -> xRot = 90;
                case SOUTH -> { xRot = 90; yRot = 180; }
                case WEST -> { xRot = 90; yRot = 270; }
                case EAST -> { xRot = 90; yRot = 90; }
            }
            return ConfiguredModel.builder().modelFile(model).rotationX(xRot).rotationY(yRot).build();
        });
    }

    private void compressedBlocks() {
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_1.get(), "cobblestone", 1);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_2.get(), "cobblestone", 2);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_3.get(), "cobblestone", 3);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_4.get(), "cobblestone", 4);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_5.get(), "cobblestone", 5);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_6.get(), "cobblestone", 6);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_7.get(), "cobblestone", 7);
        compressedSimple(ModBlocks.COMPRESSED_COBBLESTONE_8.get(), "cobblestone", 8);
        compressedSimple(ModBlocks.COMPRESSED_DIRT_1.get(), "dirt", 1);
        compressedSimple(ModBlocks.COMPRESSED_DIRT_2.get(), "dirt", 2);
        compressedSimple(ModBlocks.COMPRESSED_DIRT_3.get(), "dirt", 3);
        compressedSimple(ModBlocks.COMPRESSED_DIRT_4.get(), "dirt", 4);
        compressedSimple(ModBlocks.COMPRESSED_SAND_1.get(), "sand", 1);
        compressedSimple(ModBlocks.COMPRESSED_SAND_2.get(), "sand", 2);
        compressedSimple(ModBlocks.COMPRESSED_GRAVEL_1.get(), "gravel", 1);
        compressedSimple(ModBlocks.COMPRESSED_GRAVEL_2.get(), "gravel", 2);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_1.get(), "netherrack", 1);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_2.get(), "netherrack", 2);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_3.get(), "netherrack", 3);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_4.get(), "netherrack", 4);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_5.get(), "netherrack", 5);
        compressedSimple(ModBlocks.COMPRESSED_NETHERRACK_6.get(), "netherrack", 6);
    }

    private void compressedSimple(Block block, String base, int level) {
        ResourceLocation baseTex = modLoc("compressed/" + base + "_" + level);
        models().existingFileHelper.trackGenerated(baseTex, ModelProvider.TEXTURE);
        ModelFile model = models().cubeAll(name(block), baseTex);
        simpleBlock(block, model);
    }

    private void generators() {
        simpleBlock(ModBlocks.GENERATOR_SOLAR.get(), models().getExistingFile(modLoc("block/generator_solar")));
        simpleBlock(ModBlocks.GENERATOR_LUNAR.get(), models().getExistingFile(modLoc("block/generator_lunar")));
        simpleBlock(ModBlocks.GENERATOR_LAVA.get(), models().getExistingFile(modLoc("block/generator_lava")));
        simpleBlock(ModBlocks.GENERATOR_WATER.get(), models().getExistingFile(modLoc("block/generator_water")));
        simpleBlock(ModBlocks.GENERATOR_WIND.get(), models().getExistingFile(modLoc("block/generator_wind")));
        simpleBlock(ModBlocks.GENERATOR_FIRE.get(), models().getExistingFile(modLoc("block/generator_fire")));
        cubeBottomTopGenerator(ModBlocks.GENERATOR_PLAYER_WIND_UP.get(), "panel_stone_side", "panel_stone_base");
        cubeBottomTopGenerator(ModBlocks.GENERATOR_DRAGON_EGG.get(), "panel_egg_side", "panel_egg");
        cubeBottomTopGenerator(ModBlocks.GENERATOR_CREATIVE.get(), "panel_creative", "panel_creative_top");
    }

    private void cubeBottomTopGenerator(Block block, String side, String topBottom) {
        ModelFile model = models().cubeBottomTop(name(block), tex(side), tex(topBottom), tex(topBottom));
        simpleBlock(block, model);
    }

    private void machines() {
        simpleBlock(ModBlocks.TRASH_CAN.get(), models().cubeBottomTop(name(ModBlocks.TRASH_CAN.get()),
                tex("trashcan"), tex("trashcan_bottom"), tex("trashcan_top")));
        simpleBlock(ModBlocks.TRASH_CAN_FLUID.get(), models().cubeBottomTop(name(ModBlocks.TRASH_CAN_FLUID.get()),
                tex("trashcan_fluid"), tex("trashcan_bottom_fluid"), tex("trashcan_top_fluid")));
        simpleBlock(ModBlocks.TRASH_CAN_ENERGY.get(), models().cubeBottomTop(name(ModBlocks.TRASH_CAN_ENERGY.get()),
                tex("trashcan_energy"), tex("trashcan_bottom_energy"), tex("trashcan_top_energy")));
        simpleBlock(ModBlocks.RESONATOR.get(), models().cubeBottomTop(name(ModBlocks.RESONATOR.get()),
                tex("resonator_side"), tex("resonator_bottom"), tex("resonator_top")));
        simpleBlock(ModBlocks.CRAFTER.get(), models().cubeAll(name(ModBlocks.CRAFTER.get()), tex("autocraft")));
        simpleBlock(ModBlocks.ANALOG_CRAFTER.get(), models().cubeAll(name(ModBlocks.ANALOG_CRAFTER.get()), tex("analog_crafter")));
        drumBlock(ModBlocks.DRUM_16.get(), "stone");
        drumBlock(ModBlocks.DRUM_256.get(), "iron");
        drumBlock(ModBlocks.DRUM_4096.get(), "highcapacity");
        drumBlock(ModBlocks.DRUM_65536.get(), "insane");
        drumBlock(ModBlocks.DRUM_CREATIVE.get(), "creative");
        simpleBlock(ModBlocks.SCREEN.get(), models().cubeAll(name(ModBlocks.SCREEN.get()), tex("screen_no_signal")));
        simpleBlock(ModBlocks.SPOTLIGHT.get(), cubeAll(ModBlocks.SPOTLIGHT.get(), "spotlight"));
        simpleBlock(ModBlocks.POWER_TRANSMITTER.get(), models().cubeBottomTop(name(ModBlocks.POWER_TRANSMITTER.get()),
                tex("transfernodes/transmitter_side"), tex("transfernodes/transmitter_bottom"), tex("transfernodes/transmitter_top")));
        simpleBlock(ModBlocks.POWER_BATTERY.get(), models().cubeBottomTop(name(ModBlocks.POWER_BATTERY.get()),
                tex("transfernodes/battery_side"), tex("transfernodes/battery_side"), tex("transfernodes/battery_top")));
        simpleBlock(ModBlocks.INDEXER.get(), cubeAll(ModBlocks.INDEXER.get(), "chest_creative"));
        simpleBlock(ModBlocks.PLAYER_CHEST.get(), cubeAll(ModBlocks.PLAYER_CHEST.get(), "chest_creative"));
        simpleBlock(ModBlocks.CREATIVE_CHEST.get(), models().cubeBottomTop(name(ModBlocks.CREATIVE_CHEST.get()),
                tex("chest_creative_side"), tex("chest_creative"), tex("chest_creative")));
        simpleBlock(ModBlocks.CREATIVE_HARVEST.get(), cubeAll(ModBlocks.CREATIVE_HARVEST.get(), "creative_harvestable"));
        simpleBlock(ModBlocks.MINER.get(), models().orientable(name(ModBlocks.MINER.get()),
                tex("interact_side"), tex("interact_mine"), tex("interact_side")));
        simpleBlock(ModBlocks.SCANNER.get(), models().orientable(name(ModBlocks.SCANNER.get()),
                tex("interact_side"), tex("interact_scanner"), tex("interact_side")));
        simpleBlock(ModBlocks.USER.get(), models().orientable(name(ModBlocks.USER.get()),
                tex("interact_side"), tex("interact_use"), tex("interact_side")));
        simpleBlock(ModBlocks.QUARRY.get(), cubeAll(ModBlocks.QUARRY.get(), "machine/machine_base"));
        simpleBlock(ModBlocks.TELEPORTER.get(), cubeAll(ModBlocks.TELEPORTER.get(), "special_dim_portal"));
        simpleBlock(ModBlocks.SUPER_MOB_SPAWNER.get(), cubeAll(ModBlocks.SUPER_MOB_SPAWNER.get(), "enchanted_block"));
        simpleBlock(ModBlocks.TERRAFORMER.get(), models().cubeBottomTop(name(ModBlocks.TERRAFORMER.get()),
                tex("terraformer/terraformer_side"), tex("terraformer/terraformer_base"), tex("terraformer/terraformer_side")));
        simpleBlock(ModBlocks.POWER_OVERLOAD.get(), cubeAll(ModBlocks.POWER_OVERLOAD.get(), "enchanted_block"));
        simpleBlock(ModBlocks.RAINBOW_GENERATOR.get(), cubeAll(ModBlocks.RAINBOW_GENERATOR.get(), "connected/rainbow"));
        simpleBlock(ModBlocks.SYNERGY_UNIT.get(), cubeAll(ModBlocks.SYNERGY_UNIT.get(), "synergy/synergy_side"));
    }

    private ModelFile cubeAll(Block block, String texture) {
        return models().cubeAll(name(block), tex(texture));
    }

    private void drumBlock(Block block, String texture) {
        ModelFile model = models().getExistingFile(modLoc("block/drum_" + texture));
        simpleBlock(block, model);
    }

    private String name(Block block) {
        return block.builtInRegistryHolder().key().location().getPath();
    }
}
