package com.leclowndu93150.extrautils2.registry;

import com.leclowndu93150.extrautils2.blockentity.CreativeChestBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.CreativeHarvestBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.DrumBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.ResonatorBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.SoundMufflerBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.TrashCanBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.generator.GeneratorTile;
import com.leclowndu93150.extrautils2.blockentity.generator.HandCrankTile;
import com.leclowndu93150.extrautils2.blockentity.generator.MachineGeneratorTile;
import com.leclowndu93150.extrautils2.blockentity.machine.CrusherBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.EnchanterBlockEntity;
import com.leclowndu93150.extrautils2.blockentity.machine.FurnaceBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GeneratorTile>> GENERATOR =
            ModRegistries.BLOCK_ENTITY_TYPES.register("generator", () -> {
                Block[] blocks = {
                    ModBlocks.GENERATOR_SOLAR.get(),
                    ModBlocks.GENERATOR_LUNAR.get(),
                    ModBlocks.GENERATOR_LAVA.get(),
                    ModBlocks.GENERATOR_WATER.get(),
                    ModBlocks.GENERATOR_WIND.get(),
                    ModBlocks.GENERATOR_FIRE.get(),
                    ModBlocks.GENERATOR_DRAGON_EGG.get(),
                    ModBlocks.GENERATOR_CREATIVE.get()
                };
                return BlockEntityType.Builder.<GeneratorTile>of((pos, state) -> new GeneratorTile(ModBlockEntities.GENERATOR.get(), pos, state), blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HandCrankTile>> HAND_CRANK =
            ModRegistries.BLOCK_ENTITY_TYPES.register("hand_crank", () -> {
                Block[] blocks = { ModBlocks.GENERATOR_PLAYER_WIND_UP.get() };
                return BlockEntityType.Builder.<HandCrankTile>of((pos, state) -> new HandCrankTile(ModBlockEntities.HAND_CRANK.get(), pos, state), blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrumBlockEntity>> DRUM =
            ModRegistries.BLOCK_ENTITY_TYPES.register("drum", () -> {
                Block[] blocks = {
                        ModBlocks.DRUM_16.get(),
                        ModBlocks.DRUM_256.get(),
                        ModBlocks.DRUM_4096.get(),
                        ModBlocks.DRUM_65536.get(),
                        ModBlocks.DRUM_CREATIVE.get()
                };
                return BlockEntityType.Builder.<DrumBlockEntity>of((pos, state) -> new DrumBlockEntity(ModBlockEntities.DRUM.get(), pos, state), blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MachineGeneratorTile>> MACHINE_GENERATOR =
            ModRegistries.BLOCK_ENTITY_TYPES.register("machine_generator", () -> {
                Block[] blocks = {
                    ModBlocks.MACHINE_GENERATOR_FURNACE.get(),
                    ModBlocks.MACHINE_GENERATOR_SURVIVALIST.get(),
                    ModBlocks.MACHINE_GENERATOR_CULINARY.get(),
                    ModBlocks.MACHINE_GENERATOR_POTION.get(),
                    ModBlocks.MACHINE_GENERATOR_TNT.get(),
                    ModBlocks.MACHINE_GENERATOR_LAVA.get(),
                    ModBlocks.MACHINE_GENERATOR_PINK.get(),
                    ModBlocks.MACHINE_GENERATOR_NETHERSTAR.get(),
                    ModBlocks.MACHINE_GENERATOR_ENDER.get(),
                    ModBlocks.MACHINE_GENERATOR_REDSTONE.get(),
                    ModBlocks.MACHINE_GENERATOR_OVERCLOCK.get(),
                    ModBlocks.MACHINE_GENERATOR_DRAGON.get(),
                    ModBlocks.MACHINE_GENERATOR_ICE.get(),
                    ModBlocks.MACHINE_GENERATOR_DEATH.get(),
                    ModBlocks.MACHINE_GENERATOR_ENCHANT.get(),
                    ModBlocks.MACHINE_GENERATOR_SLIME.get()
                };
                return BlockEntityType.Builder.<MachineGeneratorTile>of(
                    (pos, state) -> new MachineGeneratorTile(ModBlockEntities.MACHINE_GENERATOR.get(), pos, state), blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrashCanBlockEntity>> TRASH_CAN =
            ModRegistries.BLOCK_ENTITY_TYPES.register("trash_can", () -> {
                Block[] blocks = {
                        ModBlocks.TRASH_CAN.get(),
                        ModBlocks.TRASH_CAN_FLUID.get(),
                        ModBlocks.TRASH_CAN_ENERGY.get()
                };
                return BlockEntityType.Builder.<TrashCanBlockEntity>of(
                        (pos, state) -> new TrashCanBlockEntity(ModBlockEntities.TRASH_CAN.get(), pos, state), blocks).build(null);
            });

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoundMufflerBlockEntity>> SOUND_MUFFLER =
            ModRegistries.BLOCK_ENTITY_TYPES.register("sound_muffler", () ->
                    BlockEntityType.Builder.<SoundMufflerBlockEntity>of(
                            (pos, state) -> new SoundMufflerBlockEntity(ModBlockEntities.SOUND_MUFFLER.get(), pos, state),
                            ModBlocks.SOUND_MUFFLER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ResonatorBlockEntity>> RESONATOR =
            ModRegistries.BLOCK_ENTITY_TYPES.register("resonator", () ->
                    BlockEntityType.Builder.<ResonatorBlockEntity>of(
                            ResonatorBlockEntity::new,
                            ModBlocks.RESONATOR.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeChestBlockEntity>> CREATIVE_CHEST =
            ModRegistries.BLOCK_ENTITY_TYPES.register("creative_chest", () ->
                    BlockEntityType.Builder.<CreativeChestBlockEntity>of(
                            CreativeChestBlockEntity::new,
                            ModBlocks.CREATIVE_CHEST.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CreativeHarvestBlockEntity>> CREATIVE_HARVEST =
            ModRegistries.BLOCK_ENTITY_TYPES.register("creative_harvest", () ->
                    BlockEntityType.Builder.<CreativeHarvestBlockEntity>of(
                            CreativeHarvestBlockEntity::new,
                            ModBlocks.CREATIVE_HARVEST.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<com.leclowndu93150.extrautils2.blockentity.CreativeEnergyBlockEntity>> CREATIVE_ENERGY =
            ModRegistries.BLOCK_ENTITY_TYPES.register("creative_energy", () ->
                    BlockEntityType.Builder.<com.leclowndu93150.extrautils2.blockentity.CreativeEnergyBlockEntity>of(
                            com.leclowndu93150.extrautils2.blockentity.CreativeEnergyBlockEntity::new,
                            ModBlocks.CREATIVE_ENERGY.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FurnaceBlockEntity>> MACHINE_FURNACE =
            ModRegistries.BLOCK_ENTITY_TYPES.register("machine_furnace", () ->
                    BlockEntityType.Builder.<FurnaceBlockEntity>of(
                            FurnaceBlockEntity::new,
                            ModBlocks.MACHINE_FURNACE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrusherBlockEntity>> MACHINE_CRUSHER =
            ModRegistries.BLOCK_ENTITY_TYPES.register("machine_crusher", () ->
                    BlockEntityType.Builder.<CrusherBlockEntity>of(
                            CrusherBlockEntity::new,
                            ModBlocks.MACHINE_CRUSHER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnchanterBlockEntity>> MACHINE_ENCHANTER =
            ModRegistries.BLOCK_ENTITY_TYPES.register("machine_enchanter", () ->
                    BlockEntityType.Builder.<EnchanterBlockEntity>of(
                            EnchanterBlockEntity::new,
                            ModBlocks.MACHINE_ENCHANTER.get()).build(null));

    public static void init() {}
}
