package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class XULanguageProvider extends LanguageProvider {
    public XULanguageProvider(PackOutput output) {
        super(output, ExtraUtilities.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.extrautils2", "Extra Utilities 2");

        addBlock(ModBlocks.ANGEL_BLOCK, "Angel Block");
        addBlock(ModBlocks.CURSED_EARTH, "Cursed Earth");
        addBlock(ModBlocks.MOON_STONE, "Moon Stone");
        addBlock(ModBlocks.DECORATIVE_SOLID, "Decorative Stone");
        addBlock(ModBlocks.DECORATIVE_SOLID_WOOD, "Decorative Wood");
        addBlock(ModBlocks.DECORATIVE_BEDROCK, "Bedrock Bricks");
        addBlock(ModBlocks.OPINIUM_BLOCK, "Block of Demon Metal");
        addBlock(ModBlocks.REDSTONE_LANTERN, "Redstone Lantern");
        addBlock(ModBlocks.SOUND_MUFFLER, "Sound Muffler");
        addBlock(ModBlocks.REDSTONE_CLOCK, "Redstone Clock");

        addBlock(ModBlocks.SPIKE_WOOD, "Wooden Spikes");
        addBlock(ModBlocks.SPIKE_STONE, "Stone Spikes");
        addBlock(ModBlocks.SPIKE_IRON, "Iron Spikes");
        addBlock(ModBlocks.SPIKE_GOLD, "Golden Spikes");
        addBlock(ModBlocks.SPIKE_DIAMOND, "Diamond Spikes");
        addBlock(ModBlocks.SPIKE_CREATIVE, "Creative Spikes");

        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_1, "Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_2, "Double Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_3, "Triple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_4, "Quadruple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_5, "Quintuple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_6, "Sextuple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_7, "Septuple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_COBBLESTONE_8, "Octuple Compressed Cobblestone");
        addBlock(ModBlocks.COMPRESSED_DIRT_1, "Compressed Dirt");
        addBlock(ModBlocks.COMPRESSED_DIRT_2, "Double Compressed Dirt");
        addBlock(ModBlocks.COMPRESSED_DIRT_3, "Triple Compressed Dirt");
        addBlock(ModBlocks.COMPRESSED_DIRT_4, "Quadruple Compressed Dirt");
        addBlock(ModBlocks.COMPRESSED_SAND_1, "Compressed Sand");
        addBlock(ModBlocks.COMPRESSED_SAND_2, "Double Compressed Sand");
        addBlock(ModBlocks.COMPRESSED_GRAVEL_1, "Compressed Gravel");
        addBlock(ModBlocks.COMPRESSED_GRAVEL_2, "Double Compressed Gravel");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_1, "Compressed Netherrack");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_2, "Double Compressed Netherrack");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_3, "Triple Compressed Netherrack");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_4, "Quadruple Compressed Netherrack");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_5, "Quintuple Compressed Netherrack");
        addBlock(ModBlocks.COMPRESSED_NETHERRACK_6, "Sextuple Compressed Netherrack");

        addBlock(ModBlocks.MACHINE_GENERATOR_FURNACE, "Furnace Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_SURVIVALIST, "Survival Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_CULINARY, "Culinary Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_POTION, "Potion Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_TNT, "Explosive Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_LAVA, "Magmatic Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_PINK, "Pink Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_NETHERSTAR, "Netherstar Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_ENDER, "Ender Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_REDSTONE, "Heated Redstone Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_OVERCLOCK, "Overclocked Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_DRAGON, "Halitosis Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_ICE, "Frosty Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_DEATH, "Death Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_ENCHANT, "Dischantment Generator");
        addBlock(ModBlocks.MACHINE_GENERATOR_SLIME, "Slimey Generator");

        addBlock(ModBlocks.GENERATOR_SOLAR, "Solar Panel");
        addBlock(ModBlocks.GENERATOR_LUNAR, "Lunar Panel");
        addBlock(ModBlocks.GENERATOR_LAVA, "Lava Mill");
        addBlock(ModBlocks.GENERATOR_WATER, "Water Mill");
        addBlock(ModBlocks.GENERATOR_WIND, "Wind Mill");
        addBlock(ModBlocks.GENERATOR_FIRE, "Fire Mill");
        addBlock(ModBlocks.GENERATOR_PLAYER_WIND_UP, "Manual Mill");
        addBlock(ModBlocks.GENERATOR_DRAGON_EGG, "Dragon Egg Mill");
        addBlock(ModBlocks.GENERATOR_CREATIVE, "Creative Mill");

        addBlock(ModBlocks.TRASH_CAN, "Trash Can");
        addBlock(ModBlocks.TRASH_CAN_FLUID, "Trash Can (Fluid)");
        addBlock(ModBlocks.TRASH_CAN_ENERGY, "Trash Can (Energy)");
        addBlock(ModBlocks.RESONATOR, "Resonator");
        addBlock(ModBlocks.CRAFTER, "Mechanical Crafter");
        addBlock(ModBlocks.ANALOG_CRAFTER, "Analog Crafter");
        addBlock(ModBlocks.DRUM_16, "Stone Drum");
        addBlock(ModBlocks.DRUM_256, "Iron Drum");
        addBlock(ModBlocks.DRUM_4096, "Reinforced Large Drum");
        addBlock(ModBlocks.DRUM_65536, "Demonically Gargantuan Drum");
        addBlock(ModBlocks.DRUM_CREATIVE, "Creative Drum");
        addBlock(ModBlocks.SCREEN, "Screen");
        addBlock(ModBlocks.SPOTLIGHT, "Spotlight");
        addBlock(ModBlocks.POWER_TRANSMITTER, "Wireless RF Transmitter");
        addBlock(ModBlocks.POWER_BATTERY, "Wireless RF Battery");
        addBlock(ModBlocks.INDEXER, "Indexer");
        addBlock(ModBlocks.PLAYER_CHEST, "Player Chest");
        addBlock(ModBlocks.CREATIVE_CHEST, "Creative Chest");
        addBlock(ModBlocks.CREATIVE_HARVEST, "Creative Harvest");
        addBlock(ModBlocks.CREATIVE_ENERGY, "Creative Energy Source");
        addBlock(ModBlocks.MINER, "Mechanical Miner");
        addBlock(ModBlocks.SCANNER, "Scanner");
        addBlock(ModBlocks.USER, "Mechanical User");
        addBlock(ModBlocks.QUARRY, "Quantum Quarry");
        addBlock(ModBlocks.TELEPORTER, "Teleporter");
        addBlock(ModBlocks.SUPER_MOB_SPAWNER, "Resturbed Mob Spawner");
        addBlock(ModBlocks.TERRAFORMER, "Terraformer");
        addBlock(ModBlocks.POWER_OVERLOAD, "Power Overload");
        addBlock(ModBlocks.RAINBOW_GENERATOR, "Rainbow Generator!");
        addBlock(ModBlocks.SYNERGY_UNIT, "Synergy Unit");

        addItem(ModItems.GOLDEN_BAG, "Golden Bag");

        addItem(ModItems.ANGEL_RING_BASE, "Angel Ring");
        addItem(ModItems.ANGEL_RING_FEATHER, "Angel Ring (Feather)");
        addItem(ModItems.ANGEL_RING_BUTTERFLY, "Angel Ring (Butterfly)");
        addItem(ModItems.ANGEL_RING_DEMON, "Angel Ring (Demon)");
        addItem(ModItems.ANGEL_RING_GOLDEN, "Angel Ring (Golden)");
        addItem(ModItems.ANGEL_RING_BAT, "Angel Ring (Bat)");
        addItem(ModItems.UPGRADE_SPEED, "Speed Upgrade");
        addItem(ModItems.UPGRADE_SPEED_ENCHANTED, "Speed Upgrade (Enchanted)");
        addItem(ModItems.UPGRADE_SPEED_SUPER, "Speed Upgrade (Super)");
        addItem(ModItems.UPGRADE_STACK_SIZE, "Stack Size Upgrade");
        addItem(ModItems.UPGRADE_MINING, "Mining Upgrade");

        addItem(ModItems.OPINIUM_CORE_0, "Opinium Core (Pathetic)");
        addItem(ModItems.OPINIUM_CORE_1, "Opinium Core (Mediocre)");
        addItem(ModItems.OPINIUM_CORE_2, "Opinium Core (Passable)");
        addItem(ModItems.OPINIUM_CORE_3, "Opinium Core (Decent)");
        addItem(ModItems.OPINIUM_CORE_4, "Opinium Core (Good)");
        addItem(ModItems.OPINIUM_CORE_5, "Opinium Core (Damn Good)");
        addItem(ModItems.OPINIUM_CORE_6, "Opinium Core (Amazing)");
        addItem(ModItems.OPINIUM_CORE_7, "Opinium Core (Inspiring)");
        addItem(ModItems.OPINIUM_CORE_8, "Opinium Core (Perfected)");

        addItem(ModItems.KIKOKU, "Kikoku");

        add("tooltip.extrautils2.compressed", "%s Blocks");
        add("tooltip.extrautils2.drum.empty", "Drum: Empty");
        add("tooltip.extrautils2.drum", "Drum: %s (%s / %s)");
        add("tooltip.extrautils2.gp.status", "GP: %s / %s");
        add("tooltip.extrautils2.upgrade.max", "Max Upgrades: %s");
        add("tooltip.extrautils2.upgrade.power.single", "Power Penalty: +%s GP");
        add("tooltip.extrautils2.upgrade.power.level1", "Power Penalty (level 1): +%s GP");
        add("tooltip.extrautils2.upgrade.power.leveln", "Power Penalty (level %s): +%s GP");
        add("tooltip.extrautils2.generator.solar.1", "Gives power during daylight hours.");
        add("tooltip.extrautils2.generator.solar.2", "Must have clear line of sight to sky.");
        add("tooltip.extrautils2.generator.solar.3", "Power reduced by rain.");
        add("tooltip.extrautils2.generator.lunar.1", "Gives power during night hours.");
        add("tooltip.extrautils2.generator.lunar.2", "Must have clear line of sight to sky.");
        add("tooltip.extrautils2.generator.lunar.3", "Power boosted by full moon.");
        add("tooltip.extrautils2.generator.lunar.4", "Power reduced by rain.");
        add("tooltip.extrautils2.generator.lava", "Operates when adjacent to lava.");
        add("tooltip.extrautils2.generator.water.1", "Gives GP for adjacent flowing water blocks.");
        add("tooltip.extrautils2.generator.water.2", "The higher the level, the more GP.");
        add("tooltip.extrautils2.generator.water.3", "Source/full water blocks do not count.");
        add("tooltip.extrautils2.generator.wind.1", "Generates power from wind.");
        add("tooltip.extrautils2.generator.wind.2", "Power boosted by rain.");
        add("tooltip.extrautils2.generator.wind.3", "North/south blocks must be clear.");
        add("tooltip.extrautils2.generator.fire", "Operates when placed over fire.");
        add("tooltip.extrautils2.generator.manual", "Hold right-click to temporarily generate power.");
        add("tooltip.extrautils2.generator.dragon_egg", "Operates when a Dragon Egg is placed on top.");
        add("tooltip.extrautils2.generator.creative", "Creative-only item.");
        add("tooltip.extrautils2.spike.wood", "Reduces health to half a heart, but doesn't kill");
        add("tooltip.extrautils2.spike.gold", "Mobs drop experience");
        add("tooltip.extrautils2.spike.diamond", "Mobs drop 'Player-kill only' items");
        add("tooltip.extrautils2.grid_overloaded", "Grid is overloaded");
        add("tooltip.extrautils2.redstone.always_on", "Always On");
        add("tooltip.extrautils2.redstone.on", "Redstone On");
        add("tooltip.extrautils2.redstone.off", "Redstone Off");
        add("tooltip.extrautils2.redstone.pulse", "Redstone Pulse");

        add("death.attack.spike", "%1$s walked on a pointy spike (ouchies)");
        add("death.attack.spike.item", "%1$s walked on a pointy spike (ouchies)");
        add("death.attack.spike_creative", "%1$s failed to become the guy");
        add("death.attack.spike_creative.item", "%1$s failed to become the guy");

        add("effect.extrautils2.doom", "Doom");
        add("death.attack.doom", "%1$s met his doom");
        add("death.attack.doom.item", "%1$s met his doom");
    }
}
