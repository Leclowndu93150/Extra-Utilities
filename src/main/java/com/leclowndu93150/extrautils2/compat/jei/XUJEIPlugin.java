package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.client.gui.MachineGeneratorScreen;
import com.leclowndu93150.extrautils2.client.gui.ResonatorScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.CrusherScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.EnchanterScreen;
import com.leclowndu93150.extrautils2.client.gui.machine.FurnaceScreen;
import com.leclowndu93150.extrautils2.gui.HasProgressArrow;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.recipe.CrusherRecipe;
import com.leclowndu93150.extrautils2.recipe.EnchanterRecipe;
import com.leclowndu93150.extrautils2.recipe.GeneratorFuelRecipe;
import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
import com.leclowndu93150.extrautils2.recipe.XPCraftingRecipe;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModItems;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryDecorator;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class XUJEIPlugin implements IModPlugin {

    @SuppressWarnings("unchecked")
    public static final RecipeType<RecipeHolder<ResonatorRecipe>> RESONATOR =
            (RecipeType<RecipeHolder<ResonatorRecipe>>) (RecipeType<?>) RecipeType.create(ExtraUtilities.MODID, "resonator", RecipeHolder.class);

    @SuppressWarnings("unchecked")
    public static final RecipeType<RecipeHolder<CrusherRecipe>> CRUSHER =
            (RecipeType<RecipeHolder<CrusherRecipe>>) (RecipeType<?>) RecipeType.create(ExtraUtilities.MODID, "crusher", RecipeHolder.class);

    @SuppressWarnings("unchecked")
    public static final RecipeType<RecipeHolder<EnchanterRecipe>> ENCHANTER =
            (RecipeType<RecipeHolder<EnchanterRecipe>>) (RecipeType<?>) RecipeType.create(ExtraUtilities.MODID, "enchanter", RecipeHolder.class);

    private static final Map<MachineGeneratorType, RecipeType<GeneratorFuelDisplay>> GENERATOR_TYPES = new EnumMap<>(MachineGeneratorType.class);

    static {
        for (MachineGeneratorType type : MachineGeneratorType.values()) {
            GENERATOR_TYPES.put(type, RecipeType.create(ExtraUtilities.MODID,
                    "generator_" + type.name().toLowerCase(), GeneratorFuelDisplay.class));
        }
    }

    public static RecipeType<GeneratorFuelDisplay> getGeneratorRecipeType(MachineGeneratorType type) {
        return GENERATOR_TYPES.get(type);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ResonatorCategory(RESONATOR, guiHelper));
        registration.addRecipeCategories(new CrusherCategory(CRUSHER, guiHelper));
        registration.addRecipeCategories(new EnchanterCategory(ENCHANTER, guiHelper));
        for (var entry : GENERATOR_TYPES.entrySet()) {
            Block block = getBlock(entry.getKey());
            if (block != null) {
                registration.addRecipeCategories(new GeneratorFuelCategory(entry.getValue(), block, guiHelper));
            }
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            List<RecipeHolder<ResonatorRecipe>> resonatorRecipes =
                    level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.RESONATOR.get());
            registration.addRecipes(RESONATOR, resonatorRecipes);

            List<RecipeHolder<CrusherRecipe>> crusherRecipes =
                    level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.CRUSHER.get());
            registration.addRecipes(CRUSHER, crusherRecipes);

            List<RecipeHolder<EnchanterRecipe>> enchanterRecipes =
                    level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ENCHANTER.get());
            registration.addRecipes(ENCHANTER, enchanterRecipes);

            Map<String, List<GeneratorFuelDisplay>> recipeDisplays = new java.util.HashMap<>();
            for (RecipeHolder<GeneratorFuelRecipe> holder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.GENERATOR_FUEL.get())) {
                GeneratorFuelRecipe recipe = holder.value();
                List<ItemStack> inputs = new ArrayList<>();
                if (recipe.input() != null && !recipe.input().isEmpty()) {
                    ItemStack stack = new ItemStack(recipe.input().getItems()[0].getItem(), recipe.inputCount());
                    inputs.add(stack);
                }
                if (recipe.secondaryInput() != null && !recipe.secondaryInput().isEmpty()) {
                    ItemStack stack = new ItemStack(recipe.secondaryInput().getItems()[0].getItem(), recipe.secondaryCount());
                    inputs.add(stack);
                }
                recipeDisplays.computeIfAbsent(recipe.generatorType(), k -> new ArrayList<>())
                        .add(new GeneratorFuelDisplay(null, inputs, recipe.fluidInput(), recipe.totalEnergy(), recipe.energyPerTick()));
            }

            for (var entry : GENERATOR_TYPES.entrySet()) {
                MachineGeneratorType genType = entry.getKey();
                String typeName = genType.name().toLowerCase();
                List<GeneratorFuelDisplay> displays;

                if (genType.usesRecipes()) {
                    displays = recipeDisplays.getOrDefault(typeName, List.of());
                } else {
                    displays = scanDynamicFuels(genType);
                }

                if (!displays.isEmpty()) {
                    registration.addRecipes(entry.getValue(), displays);
                }
            }
        }

        registration.addItemStackInfo(new ItemStack(ModItems.GOLDEN_LASSO.get()),
                Component.literal("Right-click a passive mob to capture it. Used in crafting recipes that require a specific captured mob. The lasso is returned after crafting."));
        registration.addItemStackInfo(new ItemStack(ModItems.CURSED_LASSO.get()),
                Component.literal("Right-click a weakened hostile mob to capture it. The mob must be reduced to 1/4 health first."));
    }

    private List<GeneratorFuelDisplay> scanDynamicFuels(MachineGeneratorType type) {
        List<GeneratorFuelDisplay> displays = new ArrayList<>();
        for (var item : BuiltInRegistries.ITEM) {
            ItemStack stack = new ItemStack(item);
            var result = type.getFuelResult(stack);
            if (result != null) {
                displays.add(new GeneratorFuelDisplay(type, List.of(stack),
                        FluidStack.EMPTY, result.totalEnergy(), Math.max(1, Math.round(result.gpRate()))));
            }
        }
        return displays;
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.RESONATOR.get().asItem().getDefaultInstance(), RESONATOR);
        registration.addRecipeCatalyst(ModBlocks.MACHINE_FURNACE.get().asItem().getDefaultInstance(), mezz.jei.api.constants.RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(ModBlocks.MACHINE_CRUSHER.get().asItem().getDefaultInstance(), CRUSHER);
        registration.addRecipeCatalyst(ModBlocks.MACHINE_ENCHANTER.get().asItem().getDefaultInstance(), ENCHANTER);
        for (var entry : GENERATOR_TYPES.entrySet()) {
            Block block = getBlock(entry.getKey());
            if (block != null) {
                registration.addRecipeCatalyst(block.asItem().getDefaultInstance(), entry.getValue());
            }
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ResonatorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(ResonatorScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof HasProgressArrow arrow) {
                    return List.of(createClickableArea(arrow, RESONATOR));
                }
                return List.of();
            }
        });

        registration.addGuiContainerHandler(FurnaceScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(FurnaceScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof HasProgressArrow arrow) {
                    return List.of(createClickableArea(arrow, mezz.jei.api.constants.RecipeTypes.SMELTING));
                }
                return List.of();
            }
        });

        registration.addGuiContainerHandler(CrusherScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(CrusherScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof HasProgressArrow arrow) {
                    return List.of(createClickableArea(arrow, CRUSHER));
                }
                return List.of();
            }
        });

        registration.addGuiContainerHandler(EnchanterScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(EnchanterScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof HasProgressArrow arrow) {
                    return List.of(createClickableArea(arrow, ENCHANTER));
                }
                return List.of();
            }
        });

        registration.addGuiContainerHandler(MachineGeneratorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(MachineGeneratorScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof MachineGeneratorMenu menu && menu instanceof HasProgressArrow arrow) {
                    MachineGeneratorType type = menu.getGeneratorType();
                    RecipeType<GeneratorFuelDisplay> rt = type != null ? GENERATOR_TYPES.get(type) : null;
                    if (rt != null) {
                        return List.of(createClickableArea(arrow, rt));
                    }
                }
                return List.of();
            }
        });
    }

    @Override
    public void registerAdvanced(IAdvancedRegistration registration) {
        registration.addRecipeCategoryDecorator(RecipeTypes.CRAFTING, new IRecipeCategoryDecorator<>() {
            @Override
            public void draw(RecipeHolder<CraftingRecipe> recipe, mezz.jei.api.recipe.category.IRecipeCategory<RecipeHolder<CraftingRecipe>> recipeCategory,
                             mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, net.minecraft.client.gui.GuiGraphics guiGraphics, double mouseX, double mouseY) {
                if (recipe.value() instanceof XPCraftingRecipe xpRecipe) {
                    var font = Minecraft.getInstance().font;
                    String text = xpRecipe.getXpCost() + " Levels";
                    guiGraphics.drawString(font, text, 60, 46, 0x80FF20, false);
                }
            }
        });
    }

    private static IGuiClickableArea createClickableArea(HasProgressArrow arrow, RecipeType<?> recipeType) {
        return new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return new Rect2i(arrow.getArrowX(), arrow.getArrowY(), 22, 16);
            }

            @Override
            public boolean isTooltipEnabled() {
                return false;
            }

            @Override
            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                recipesGui.showTypes(List.of(recipeType));
            }
        };
    }

    private static Block getBlock(MachineGeneratorType type) {
        DeferredBlock<?> deferred = switch (type) {
            case FURNACE -> ModBlocks.MACHINE_GENERATOR_FURNACE;
            case SURVIVALIST -> ModBlocks.MACHINE_GENERATOR_SURVIVALIST;
            case CULINARY -> ModBlocks.MACHINE_GENERATOR_CULINARY;
            case POTION -> ModBlocks.MACHINE_GENERATOR_POTION;
            case TNT -> ModBlocks.MACHINE_GENERATOR_TNT;
            case LAVA -> ModBlocks.MACHINE_GENERATOR_LAVA;
            case PINK -> ModBlocks.MACHINE_GENERATOR_PINK;
            case NETHERSTAR -> ModBlocks.MACHINE_GENERATOR_NETHERSTAR;
            case ENDER -> ModBlocks.MACHINE_GENERATOR_ENDER;
            case REDSTONE -> ModBlocks.MACHINE_GENERATOR_REDSTONE;
            case OVERCLOCK -> ModBlocks.MACHINE_GENERATOR_OVERCLOCK;
            case DRAGON -> ModBlocks.MACHINE_GENERATOR_DRAGON;
            case ICE -> ModBlocks.MACHINE_GENERATOR_ICE;
            case DEATH -> ModBlocks.MACHINE_GENERATOR_DEATH;
            case ENCHANT -> ModBlocks.MACHINE_GENERATOR_ENCHANT;
            case SLIME -> ModBlocks.MACHINE_GENERATOR_SLIME;
        };
        return deferred.get();
    }
}
