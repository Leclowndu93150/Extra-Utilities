package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.block.CompressedBlock;
import com.leclowndu93150.extrautils2.block.DrumBlock;
import com.leclowndu93150.extrautils2.block.RedstoneClockBlock;
import com.leclowndu93150.extrautils2.block.SpikeBlock;
import com.leclowndu93150.extrautils2.block.TrashCanBlock;
import com.leclowndu93150.extrautils2.block.XUBlock;
import com.leclowndu93150.extrautils2.block.generator.GeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.GeneratorType;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorBlock;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;

public class ModBlocks {

    // --- Machine Generators ---
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_FURNACE = registerMachineGenerator(MachineGeneratorType.FURNACE);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_SURVIVALIST = registerMachineGenerator(MachineGeneratorType.SURVIVALIST);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_CULINARY = registerMachineGenerator(MachineGeneratorType.CULINARY);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_POTION = registerMachineGenerator(MachineGeneratorType.POTION);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_TNT = registerMachineGenerator(MachineGeneratorType.TNT);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_LAVA = registerMachineGenerator(MachineGeneratorType.LAVA);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_PINK = registerMachineGenerator(MachineGeneratorType.PINK);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_NETHERSTAR = registerMachineGenerator(MachineGeneratorType.NETHERSTAR);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_ENDER = registerMachineGenerator(MachineGeneratorType.ENDER);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_REDSTONE = registerMachineGenerator(MachineGeneratorType.REDSTONE);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_OVERCLOCK = registerMachineGenerator(MachineGeneratorType.OVERCLOCK);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_DRAGON = registerMachineGenerator(MachineGeneratorType.DRAGON);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_ICE = registerMachineGenerator(MachineGeneratorType.ICE);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_DEATH = registerMachineGenerator(MachineGeneratorType.DEATH);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_ENCHANT = registerMachineGenerator(MachineGeneratorType.ENCHANT);
    public static final DeferredBlock<MachineGeneratorBlock> MACHINE_GENERATOR_SLIME = registerMachineGenerator(MachineGeneratorType.SLIME);

    // --- Generators ---
    public static final DeferredBlock<GeneratorBlock> GENERATOR_SOLAR = registerGenerator(GeneratorType.SOLAR);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_LUNAR = registerGenerator(GeneratorType.LUNAR);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_LAVA = registerGenerator(GeneratorType.LAVA);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_WATER = registerGenerator(GeneratorType.WATER);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_WIND = registerGenerator(GeneratorType.WIND);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_FIRE = registerGenerator(GeneratorType.FIRE);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_PLAYER_WIND_UP = registerGenerator(GeneratorType.PLAYER_WIND_UP);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_DRAGON_EGG = registerGenerator(GeneratorType.DRAGON_EGG);
    public static final DeferredBlock<GeneratorBlock> GENERATOR_CREATIVE = registerGenerator(GeneratorType.CREATIVE);

    // --- Spikes ---
    public static final DeferredBlock<SpikeBlock> SPIKE_WOOD = ModRegistries.registerBlock(
            "spike_wood", () -> new SpikeBlock(SpikeBlock.Type.WOOD, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1f, 3f)));
    public static final DeferredBlock<SpikeBlock> SPIKE_STONE = ModRegistries.registerBlock(
            "spike_stone", () -> new SpikeBlock(SpikeBlock.Type.STONE, BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).strength(1.5f, 6f)));
    public static final DeferredBlock<SpikeBlock> SPIKE_IRON = ModRegistries.registerBlock(
            "spike_iron", () -> new SpikeBlock(SpikeBlock.Type.IRON, XUBlock.defaultProps()));
    public static final DeferredBlock<SpikeBlock> SPIKE_GOLD = ModRegistries.registerBlock(
            "spike_gold", () -> new SpikeBlock(SpikeBlock.Type.GOLD, XUBlock.defaultProps()));
    public static final DeferredBlock<SpikeBlock> SPIKE_DIAMOND = ModRegistries.registerBlock(
            "spike_diamond", () -> new SpikeBlock(SpikeBlock.Type.DIAMOND, XUBlock.defaultProps()));
    public static final DeferredBlock<SpikeBlock> SPIKE_CREATIVE = ModRegistries.registerBlock(
            "spike_creative", () -> new SpikeBlock(SpikeBlock.Type.CREATIVE,
                    BlockBehaviour.Properties.of().strength(-1f, Float.MAX_VALUE).requiresCorrectToolForDrops()));

    // --- Compressed Blocks ---
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_1 = registerCompressed("compressed_cobblestone_1", 1, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_2 = registerCompressed("compressed_cobblestone_2", 2, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_3 = registerCompressed("compressed_cobblestone_3", 3, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_4 = registerCompressed("compressed_cobblestone_4", 4, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_5 = registerCompressed("compressed_cobblestone_5", 5, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_6 = registerCompressed("compressed_cobblestone_6", 6, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_7 = registerCompressed("compressed_cobblestone_7", 7, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_COBBLESTONE_8 = registerCompressed("compressed_cobblestone_8", 8, Blocks.COBBLESTONE);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_DIRT_1 = registerCompressed("compressed_dirt_1", 1, Blocks.DIRT);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_DIRT_2 = registerCompressed("compressed_dirt_2", 2, Blocks.DIRT);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_DIRT_3 = registerCompressed("compressed_dirt_3", 3, Blocks.DIRT);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_DIRT_4 = registerCompressed("compressed_dirt_4", 4, Blocks.DIRT);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_SAND_1 = registerCompressed("compressed_sand_1", 1, Blocks.SAND);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_SAND_2 = registerCompressed("compressed_sand_2", 2, Blocks.SAND);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_GRAVEL_1 = registerCompressed("compressed_gravel_1", 1, Blocks.GRAVEL);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_GRAVEL_2 = registerCompressed("compressed_gravel_2", 2, Blocks.GRAVEL);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_1 = registerCompressed("compressed_netherrack_1", 1, Blocks.NETHERRACK);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_2 = registerCompressed("compressed_netherrack_2", 2, Blocks.NETHERRACK);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_3 = registerCompressed("compressed_netherrack_3", 3, Blocks.NETHERRACK);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_4 = registerCompressed("compressed_netherrack_4", 4, Blocks.NETHERRACK);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_5 = registerCompressed("compressed_netherrack_5", 5, Blocks.NETHERRACK);
    public static final DeferredBlock<CompressedBlock> COMPRESSED_NETHERRACK_6 = registerCompressed("compressed_netherrack_6", 6, Blocks.NETHERRACK);

    // --- Decorative ---
    public static final DeferredBlock<XUBlock> ANGEL_BLOCK = ModRegistries.registerBlock(
            "angel_block", () -> new XUBlock(BlockBehaviour.Properties.of().strength(0.5f, 6f)));
    public static final DeferredBlock<XUBlock> CURSED_EARTH = ModRegistries.registerBlock(
            "cursed_earth", () -> new XUBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK)));
    public static final DeferredBlock<XUBlock> DECORATIVE_SOLID_WOOD = ModRegistries.registerBlock(
            "decorative_solid_wood", () -> new XUBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final DeferredBlock<XUBlock> DECORATIVE_SOLID = ModRegistries.registerBlock(
            "decorative_solid", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<XUBlock> DECORATIVE_BEDROCK = ModRegistries.registerBlock(
            "decorative_bedrock", () -> new XUBlock(BlockBehaviour.Properties.of().strength(-1f, Float.MAX_VALUE)));
    public static final DeferredBlock<XUBlock> MOON_STONE = ModRegistries.registerBlock(
            "moon_stone", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<XUBlock> OPINIUM_BLOCK = ModRegistries.registerBlock(
            "opinium_block", () -> new XUBlock(XUBlock.defaultProps()));

    // --- Machines (stubs - tile entity blocks) ---
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN = ModRegistries.registerBlock(
            "trash_can", () -> new TrashCanBlock(XUBlock.defaultProps().noOcclusion(), TrashCanBlock.TrashType.ITEM));
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN_FLUID = ModRegistries.registerBlock(
            "trash_can_fluid", () -> new TrashCanBlock(XUBlock.defaultProps().noOcclusion(), TrashCanBlock.TrashType.FLUID));
    public static final DeferredBlock<TrashCanBlock> TRASH_CAN_ENERGY = ModRegistries.registerBlock(
            "trash_can_energy", () -> new TrashCanBlock(XUBlock.defaultProps().noOcclusion(), TrashCanBlock.TrashType.ENERGY));
    public static final DeferredBlock<Block> CRAFTER = ModRegistries.registerBlock(
            "crafter", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> RESONATOR = ModRegistries.registerBlock(
            "resonator", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> POWER_TRANSMITTER = ModRegistries.registerBlock(
            "power_transmitter", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> POWER_BATTERY = ModRegistries.registerBlock(
            "power_battery", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> RAINBOW_GENERATOR = ModRegistries.registerBlock(
            "rainbow_generator", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> SYNERGY_UNIT = ModRegistries.registerBlock(
            "synergy_unit", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> QUARRY = ModRegistries.registerBlock(
            "quarry", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<DrumBlock> DRUM_16 = ModRegistries.registerBlock(
            "drum_16", () -> new DrumBlock(DrumBlock.Capacity.DRUM_16, BlockBehaviour.Properties.of().strength(2f, 6f)));
    public static final DeferredBlock<DrumBlock> DRUM_256 = ModRegistries.registerBlock(
            "drum_256", () -> new DrumBlock(DrumBlock.Capacity.DRUM_256, BlockBehaviour.Properties.of().strength(2f, 6f)));
    public static final DeferredBlock<DrumBlock> DRUM_4096 = ModRegistries.registerBlock(
            "drum_4096", () -> new DrumBlock(DrumBlock.Capacity.DRUM_4096, BlockBehaviour.Properties.of().strength(2f, 6f)));
    public static final DeferredBlock<DrumBlock> DRUM_65536 = ModRegistries.registerBlock(
            "drum_65536", () -> new DrumBlock(DrumBlock.Capacity.DRUM_65536, BlockBehaviour.Properties.of().strength(2f, 6f)));
    public static final DeferredBlock<DrumBlock> DRUM_CREATIVE = ModRegistries.registerBlock(
            "drum_creative", () -> new DrumBlock(DrumBlock.Capacity.DRUM_CREATIVE, BlockBehaviour.Properties.of().strength(2f, 6f)));
    public static final DeferredBlock<Block> MINER = ModRegistries.registerBlock(
            "miner", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> SCANNER = ModRegistries.registerBlock(
            "scanner", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> USER = ModRegistries.registerBlock(
            "user", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> PLAYER_CHEST = ModRegistries.registerBlock(
            "player_chest", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> TERRAFORMER = ModRegistries.registerBlock(
            "terraformer", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> SCREEN = ModRegistries.registerBlock(
            "screen", () -> new XUBlock.Facing(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> SPOTLIGHT = ModRegistries.registerBlock(
            "spotlight", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> SOUND_MUFFLER = ModRegistries.registerBlock(
            "sound_muffler", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<RedstoneClockBlock> REDSTONE_CLOCK = ModRegistries.registerBlock(
            "redstone_clock", () -> new RedstoneClockBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> REDSTONE_LANTERN = ModRegistries.registerBlock(
            "redstone_lantern", () -> new XUBlock(BlockBehaviour.Properties.of().strength(0.3f).lightLevel(s -> 15)));
    public static final DeferredBlock<Block> SUPER_MOB_SPAWNER = ModRegistries.registerBlock(
            "mob_spawner", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> ANALOG_CRAFTER = ModRegistries.registerBlock(
            "analog_crafter", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> INDEXER = ModRegistries.registerBlock(
            "indexer", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> TELEPORTER = ModRegistries.registerBlock(
            "teleporter", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> POWER_OVERLOAD = ModRegistries.registerBlock(
            "power_overload", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> CREATIVE_CHEST = ModRegistries.registerBlock(
            "creative_chest", () -> new XUBlock(XUBlock.defaultProps()));
    public static final DeferredBlock<Block> CREATIVE_HARVEST = ModRegistries.registerBlock(
            "creative_harvest", () -> new XUBlock.FacingAll(XUBlock.defaultProps()));

    // --- Helpers ---
    private static DeferredBlock<MachineGeneratorBlock> registerMachineGenerator(MachineGeneratorType type) {
        String name = "machine_generator_" + type.name().toLowerCase();
        return ModRegistries.registerBlock(name, () -> new MachineGeneratorBlock(type, XUBlock.defaultProps()));
    }

    private static DeferredBlock<GeneratorBlock> registerGenerator(GeneratorType type) {
        String name = "generator_" + type.name().toLowerCase();
        BlockBehaviour.Properties props = XUBlock.defaultProps();
        if (type == GeneratorType.FIRE || type == GeneratorType.WIND || type == GeneratorType.WATER
                || type == GeneratorType.SOLAR || type == GeneratorType.LUNAR) {
            props = props.noOcclusion();
        }
        final BlockBehaviour.Properties finalProps = props;
        return ModRegistries.registerBlock(name, () -> new GeneratorBlock(type, finalProps));
    }

    private static DeferredBlock<CompressedBlock> registerCompressed(String name, int level, Block base) {
        return ModRegistries.registerBlock(name, () -> new CompressedBlock(level,
                BlockBehaviour.Properties.ofFullCopy(base)
                        .strength(base.defaultDestroyTime() * (float) Math.pow(2.25, level - 1),
                                  base.getExplosionResistance() * (float) Math.pow(1.5, level - 1))));
    }

    public static void init() {}
}
