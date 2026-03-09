package com.leclowndu93150.extrautils2.datagen;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.item.AngelRingItem;
import com.leclowndu93150.extrautils2.item.LuxSaberColor;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class XUItemModelProvider extends ItemModelProvider {
    private static final ResourceLocation LUX_SABER_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "lux_saber_extended");
    private static final ResourceLocation LUX_SABER_BLADE_ONLY_PROPERTY =
            ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "lux_saber_blade_only");
    private static final int LUX_SABER_STAGES = 20;
    private static final float LUX_SABER_MAX_MODEL_Y = 32.0F;
    private static final float[] LUX_SABER_HILT_BOTTOM = new float[]{12.0F, 13.5F, 14.0F, 15.5F};
    private static final float[] LUX_SABER_HILT_TOP = new float[]{14.0F, 13.5F, 16.0F, 15.5F};
    private static final float[] LUX_SABER_HILT_A = new float[]{12.0F, 3.5F, 14.0F, 7.5F};
    private static final float[] LUX_SABER_HILT_B = new float[]{12.5F, 1.5F, 14.0F, 3.5F};
    private static final float[] LUX_SABER_HILT_C = new float[]{12.0F, 0.0F, 14.0F, 1.5F};
    private static final float[] LUX_SABER_HILT_C_LIT = new float[]{12.0F, 0.5F, 14.0F, 1.5F};

    public XUItemModelProvider(PackOutput output, ExistingFileHelper efh) {
        super(output, ExtraUtilities.MODID, efh);
    }

    private ResourceLocation tex(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path);
    }

    private ResourceLocation itemTex(String path) {
        return ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "item/" + path);
    }

    @Override
    protected void registerModels() {
        blockItem(ModBlocks.ANGEL_BLOCK.get());
        blockItem(ModBlocks.CURSED_EARTH.get());
        blockItem(ModBlocks.MOON_STONE.get());
        blockItem(ModBlocks.DECORATIVE_SOLID.get());
        blockItem(ModBlocks.DECORATIVE_SOLID_WOOD.get());
        blockItem(ModBlocks.MAGICAL_WOOD.get());
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
        withExistingParent("generator_player_wind_up", ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/manual_mill"));
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
        getBuilder("creative_chest")
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .texture("particle", tex("chest_creative_side"))
                .transforms()
                .transform(ItemDisplayContext.GUI).rotation(30.0F, 45.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F, 0.625F, 0.625F).end()
                .transform(ItemDisplayContext.GROUND).rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 3.0F, 0.0F).scale(0.25F, 0.25F, 0.25F).end()
                .transform(ItemDisplayContext.HEAD).rotation(0.0F, 180.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(1.0F, 1.0F, 1.0F).end()
                .transform(ItemDisplayContext.FIXED).rotation(0.0F, 180.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F, 0.5F, 0.5F).end()
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(75.0F, 315.0F, 0.0F).translation(0.0F, 2.5F, 0.0F).scale(0.375F, 0.375F, 0.375F).end()
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0.0F, 315.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F, 0.4F, 0.4F).end();
        blockItem(ModBlocks.CREATIVE_HARVEST.get());
        blockItem(ModBlocks.CREATIVE_ENERGY.get());
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

        withExistingParent("transfer_pipe", modLoc("block/transfer/pipe_item"));
        nodeItem("transfer_node_items", tex("transfernodes/transfernode_front"), tex("transfernodes/transfernode_back"), tex("transfernodes/pipes"));
        nodeItem("retrieval_node_items", tex("transfernodes/transfernode_front_blue"), tex("transfernodes/transfernode_back"), tex("transfernodes/pipes"));
        nodeItem("transfer_node_fluids", tex("transfernodes/transfernode_front_cyan"), tex("transfernodes/transfernode_back"), tex("transfernodes/pipes"));
        nodeItem("retrieval_node_fluids", tex("transfernodes/transfernode_front_green"), tex("transfernodes/transfernode_back"), tex("transfernodes/pipes"));

        blockItem(ModBlocks.DECORATIVE_GLASS.get());
        blockItem(ModBlocks.DECORATIVE_GLASS_BORDERED.get());
        blockItem(ModBlocks.DECORATIVE_GLASS_DIAMONDS.get());
        blockItem(ModBlocks.DARK_GLASS.get());
        blockItem(ModBlocks.GLOWSTONE_GLASS.get());
        blockItem(ModBlocks.REDSTONE_GLASS.get());
        blockItem(ModBlocks.INEFFABLE_GLASS.get());
        blockItem(ModBlocks.INEFFABLE_GLASS_REVERSE.get());
        blockItem(ModBlocks.INEFFABLE_GLASS_CLEAR.get());
        blockItem(ModBlocks.INEFFABLE_GLASS_DARK.get());

        blockItem(ModBlocks.ENCHANTED_BLOCK.get());
        blockItem(ModBlocks.DEMON_BLOCK.get());
        blockItem(ModBlocks.EVIL_INFUSED_INGOT_BLOCK.get());

        blockItem(ModBlocks.LARGISH_CHEST.get());
        blockItem(ModBlocks.MINI_CHEST.get());
        blockItem(ModBlocks.TRASH_CHEST.get());
        blockItem(ModBlocks.KLEIN_BOTTLE.get());

        machineItem(ModBlocks.MACHINE_FURNACE.get(), "machine/furnace_off", "machine/machine_base_side", "machine/machine_base", "machine/machine_base_bottom");
        machineItem(ModBlocks.MACHINE_CRUSHER.get(), "machine/crusher_off", "machine/machine_base_side", "machine/machine_base", "machine/machine_base_bottom");
        machineItem(ModBlocks.MACHINE_ENCHANTER.get(), "machine/enchanter_off", "machine/enchanter_side", "machine/machine_base_bottom", "machine/machine_base_bottom", "machine/enchanter_top");

        handheld("golden_bag", tex("bag_of_holding"));

        for (String type : AngelRingItem.WING_TYPES) {
            handheld("angel_ring_" + type, tex("angelring_" + type));
        }

        handheld("upgrade_speed", itemTex("upgrade_speed"));
        handheld("upgrade_speed_enchanted", itemTex("upgrade_speed_enchanted"));
        handheld("upgrade_speed_super", itemTex("upgrade_speed_super"));
        handheld("upgrade_stack_size", itemTex("upgrade_stack_size"));
        handheld("upgrade_mining", itemTex("upgrade_mining"));

        for (int i = 0; i <= 8; i++) {
            getBuilder("opinium_core_" + i)
                    .parent(new ModelFile.UncheckedModelFile("minecraft:builtin/entity"));
        }

        basicItem("redstone_crystal");
        basicItem("redstone_gear");
        basicItem("eye_redstone");
        basicItem("dye_powder_lunar");
        basicItem("red_coal");
        basicItem("upgrade_base");
        basicItem("evil_drop");
        basicItem("demon_ingot");
        basicItem("enchanted_ingot");
        basicItem("redstone_coil");
        basicItem("evil_infused_ingot");
        basicItem("dye_powder_blue");

        handheld("builders_wand", tex("builderswand0"));
        handheld("creative_builders_wand", tex("creativebuilderswand0"));
        handheld("destruction_wand", tex("destructionwand0"));
        handheld("creative_destruction_wand", tex("creativedestructionwand0"));
        handheld("wrench", tex("pipe_wrench"));
        handheld("glass_cutter", tex("glasscutter"));
        handheld("trowel", tex("trowel"));
        handheld("watering_can", tex("watering_can"));
        lassoItem("golden_lasso", tex("golden_lasso"));
        lassoItem("cursed_lasso", tex("dark_lasso"));
        handheld("boomerang", tex("boomerang"));
        handheld("fire_axe", tex("fire_axe"));
        handheld("fire_extinguisher", tex("fire_extinguisher"));
        handheld("compound_bow", tex("compound_bow"));
        for (LuxSaberColor color : LuxSaberColor.values()) {
            luxSaberItem(color);
        }
        handheld("biome_marker", tex("biome_marker"));
        handheld("indexer_remote", tex("indexer_remote"));
        handheld("power_manager", tex("power_scanner"));

        basicItem("chicken_ring", tex("chickenring"));
        basicItem("squid_ring", tex("flyingsquidring"));

        basicItem("sun_crystal", tex("sun_crystal"));
        basicItem("unstable_ingot", tex("unstable_ingot_interior"));
        basicItem("contract", tex("contract"));
        basicItem("magic_apple", tex("reroll_apple"));
        basicItem("filter_item", tex("filter_item"));
        basicItem("filter_fluid", tex("filter_fluid"));
        basicItem("snow_globe", tex("globe_side"));
    }

    private void blockItem(Block block) {
        String path = block.builtInRegistryHolder().key().location().getPath();
        withExistingParent(path, ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path));
    }

    private void machineGeneratorItem(Block block) {
        String path = block.builtInRegistryHolder().key().location().getPath();
        withExistingParent(path, ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "block/" + path + "_off"));
    }

    private void machineItem(Block block, String front, String side, String top, String bottom) {
        machineItem(block, front, side, top, bottom, null);
    }

    private void machineItem(Block block, String front, String side, String top, String bottom, @Nullable String topOverlay) {
        String path = block.builtInRegistryHolder().key().location().getPath();
        var builder = getBuilder(path)
                .parent(new ModelFile.UncheckedModelFile("minecraft:block/block"))
                .texture("side", tex(side))
                .texture("top", tex(top))
                .texture("bottom", tex(bottom))
                .texture("front", tex(front))
                .renderType("minecraft:cutout");

        builder.element()
                .from(0, 0, 0).to(16, 16, 16)
                .face(Direction.DOWN).texture("#bottom").cullface(Direction.DOWN).end()
                .face(Direction.UP).texture("#top").cullface(Direction.UP).end()
                .face(Direction.NORTH).texture("#top").cullface(Direction.NORTH).end()
                .face(Direction.SOUTH).texture("#side").cullface(Direction.SOUTH).end()
                .face(Direction.WEST).texture("#side").cullface(Direction.WEST).end()
                .face(Direction.EAST).texture("#side").cullface(Direction.EAST).end()
                .end();

        builder.element()
                .from(0, 0, -0.01f).to(16, 16, 0)
                .face(Direction.NORTH).texture("#front").end()
                .end();

        if (topOverlay != null) {
            builder.texture("top_overlay", tex(topOverlay));
            builder.element()
                    .from(0, 16, 0).to(16, 16.01f, 16)
                    .face(Direction.UP).texture("#top_overlay").end()
                    .end();
        }
    }

    private void basicItem(String name) {
        basicItem(name, tex(name));
    }

    private void basicItem(String name, ResourceLocation texture) {
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/generated"))
                .texture("layer0", texture);
    }

    private void handheld(String name, ResourceLocation texture) {
        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/handheld"))
                .texture("layer0", texture);
    }

    private void luxSaberItem(LuxSaberColor color) {
        String itemId = color.itemId();
        ItemModelBuilder builder = getBuilder(itemId)
                .parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                .texture("layer0", tex("luxsaber"))
                .texture("particle", tex("luxsaber"))
                .guiLight(BlockModel.GuiLight.FRONT)
                .renderType("minecraft:translucent");

        addLuxSaberHilt(builder, false, color);

        for (int stage = 1; stage <= LUX_SABER_STAGES; stage++) {
            float extension = stage / (float) LUX_SABER_STAGES;
            ItemModelBuilder stageModel = getBuilder(itemId + "_stage_" + "%02d".formatted(stage))
                    .parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                    .texture("layer0", tex("luxsaber"))
                    .texture("particle", tex("luxsaber"))
                    .guiLight(BlockModel.GuiLight.FRONT)
                    .renderType("minecraft:translucent");

            addLuxSaberHilt(stageModel, true, color);
            addLuxSaberBlade(stageModel, color, extension);

            ItemModelBuilder bladeStageModel = getBuilder(itemId + "_blade_stage_" + "%02d".formatted(stage))
                    .parent(new ModelFile.UncheckedModelFile("minecraft:item/handheld"))
                    .texture("layer0", tex("luxsaber"))
                    .texture("particle", tex("luxsaber"))
                    .guiLight(BlockModel.GuiLight.FRONT)
                    .renderType("minecraft:translucent");

            addLuxSaberBlade(bladeStageModel, color, extension);

            builder.override()
                    .predicate(LUX_SABER_PROPERTY, extension)
                    .model(stageModel)
                    .end();
            builder.override()
                    .predicate(LUX_SABER_BLADE_ONLY_PROPERTY, extension)
                    .model(bladeStageModel)
                    .end();
        }
    }

    private void addLuxSaberHilt(ItemModelBuilder builder, boolean lit, LuxSaberColor color) {
        addLuxSaberBox(builder, 6.0F, 0.0F, 6.0F, 10.0F, 8.0F, 10.0F, LUX_SABER_HILT_BOTTOM, LUX_SABER_HILT_TOP, LUX_SABER_HILT_A, false);
        addLuxSaberBox(builder, 6.5F, 8.0F, 6.5F, 9.5F, 12.0F, 9.5F, LUX_SABER_HILT_BOTTOM, LUX_SABER_HILT_TOP, LUX_SABER_HILT_B, false);
        if (lit) {
            addLuxSaberBox(builder, 6.0F, 12.0F, 6.0F, 10.0F, 14.0F, 10.0F, LUX_SABER_HILT_BOTTOM, LUX_SABER_HILT_TOP, LUX_SABER_HILT_C_LIT, false);
            addLuxSaberBox(builder, 6.0F, 14.0F, 6.0F, 10.0F, 15.0F, 10.0F, luxSaberGlowTop(color), luxSaberGlowTop(color), luxSaberGlowSide(color), true);
        } else {
            addLuxSaberBox(builder, 6.0F, 12.0F, 6.0F, 10.0F, 15.0F, 10.0F, LUX_SABER_HILT_BOTTOM, LUX_SABER_HILT_TOP, LUX_SABER_HILT_C, false);
        }
    }

    private void addLuxSaberBlade(ItemModelBuilder builder, LuxSaberColor color, float extension) {
        float bladeScale = Math.min(extension * 1.2F, 1.0F);
        float innerHalfWidth = bladeScale * 0.75F;
        float outerHalfWidth = bladeScale * 1.5F;
        float innerTopY = Math.min(LUX_SABER_MAX_MODEL_Y, 15.0F + 48.0F * extension);
        float outerTopY = Math.min(LUX_SABER_MAX_MODEL_Y, 15.0F + 49.6F * extension);

        addLuxSaberBox(
                builder,
                8.0F - innerHalfWidth, 15.0F, 8.0F - innerHalfWidth,
                8.0F + innerHalfWidth, innerTopY, 8.0F + innerHalfWidth,
                luxSaberBladeTop(color), luxSaberBladeTop(color), luxSaberBladeSide(color, extension),
                true
        );
        addLuxSaberBox(
                builder,
                8.0F - outerHalfWidth, 15.0F, 8.0F - outerHalfWidth,
                8.0F + outerHalfWidth, outerTopY, 8.0F + outerHalfWidth,
                luxSaberBladeTop(color), luxSaberBladeTop(color), luxSaberBladeSide(color, extension),
                true
        );
    }

    private void addLuxSaberBox(
            ItemModelBuilder builder,
            float fromX, float fromY, float fromZ,
            float toX, float toY, float toZ,
            float[] bottomUv, float[] topUv, float[] sideUv,
            boolean emissive
    ) {
        var element = builder.element()
                .from(fromX, fromY, fromZ)
                .to(toX, toY, toZ);
        if (emissive) {
            element.shade(false);
        }
        var down = element.face(Direction.DOWN).texture("#layer0").uvs(bottomUv[0], bottomUv[1], bottomUv[2], bottomUv[3]);
        if (emissive) down.emissivity(15, 15);
        down.end();
        var up = element.face(Direction.UP).texture("#layer0").uvs(topUv[0], topUv[1], topUv[2], topUv[3]);
        if (emissive) up.emissivity(15, 15);
        up.end();
        var north = element.face(Direction.NORTH).texture("#layer0").uvs(sideUv[0], sideUv[1], sideUv[2], sideUv[3]);
        if (emissive) north.emissivity(15, 15);
        north.end();
        var south = element.face(Direction.SOUTH).texture("#layer0").uvs(sideUv[0], sideUv[1], sideUv[2], sideUv[3]);
        if (emissive) south.emissivity(15, 15);
        south.end();
        var west = element.face(Direction.WEST).texture("#layer0").uvs(sideUv[0], sideUv[1], sideUv[2], sideUv[3]);
        if (emissive) west.emissivity(15, 15);
        west.end();
        var east = element.face(Direction.EAST).texture("#layer0").uvs(sideUv[0], sideUv[1], sideUv[2], sideUv[3]);
        if (emissive) east.emissivity(15, 15);
        east.end();
        element.end();
    }

    private float[] luxSaberGlowSide(LuxSaberColor color) {
        float minU = color.ordinal() * 2.0F;
        return new float[]{minU, 15.5F, minU + 2.0F, 16.0F};
    }

    private float[] luxSaberGlowTop(LuxSaberColor color) {
        float minU = color.ordinal() * 2.0F;
        return new float[]{minU, 13.5F, minU + 2.0F, 15.5F};
    }

    private float[] luxSaberBladeSide(LuxSaberColor color, float extension) {
        float minU = color.ordinal() * 1.5F;
        return new float[]{minU, (1.0F - extension) * 12.0F, minU + 1.5F, 12.0F};
    }

    private float[] luxSaberBladeTop(LuxSaberColor color) {
        float minU = color.ordinal() * 1.5F;
        return new float[]{minU, 12.0F, minU + 1.5F, 13.5F};
    }

    private void nodeItem(String name, ResourceLocation nodeTex, ResourceLocation backTex, ResourceLocation pipeTex) {
        withExistingParent(name, modLoc("block/transfer/node_item_template"))
                .texture("node", nodeTex)
                .texture("back", backTex)
                .texture("pipe", pipeTex);
    }

    private void lassoItem(String name, ResourceLocation baseTexture) {
        var filled = withExistingParent(name + "_filled", ResourceLocation.withDefaultNamespace("item/handheld"))
                .texture("layer0", baseTexture)
                .texture("layer1", tex("lasso_internal_1"))
                .texture("layer2", tex("lasso_internal_2"));

        withExistingParent(name, ResourceLocation.withDefaultNamespace("item/handheld"))
                .texture("layer0", baseTexture)
                .override()
                    .predicate(ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "filled"), 1.0F)
                    .model(filled)
                    .end();
    }
}
