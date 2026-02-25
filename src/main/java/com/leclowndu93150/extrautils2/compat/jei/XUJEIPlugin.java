package com.leclowndu93150.extrautils2.compat.jei;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.block.generator.MachineGeneratorType;
import com.leclowndu93150.extrautils2.client.gui.MachineGeneratorScreen;
import com.leclowndu93150.extrautils2.client.gui.ResonatorScreen;
import com.leclowndu93150.extrautils2.gui.HasProgressArrow;
import com.leclowndu93150.extrautils2.gui.MachineGeneratorMenu;
import com.leclowndu93150.extrautils2.gui.ResonatorMenu;
import com.leclowndu93150.extrautils2.recipe.ResonatorRecipe;
import com.leclowndu93150.extrautils2.registry.ModBlocks;
import com.leclowndu93150.extrautils2.registry.ModRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@JeiPlugin
public class XUJEIPlugin implements IModPlugin {

    @SuppressWarnings("unchecked")
    public static final RecipeType<RecipeHolder<ResonatorRecipe>> RESONATOR =
            (RecipeType<RecipeHolder<ResonatorRecipe>>) (RecipeType<?>) RecipeType.create(ExtraUtilities.MODID, "resonator", RecipeHolder.class);

    private static final Map<MachineGeneratorType, RecipeType<GeneratorFuelRecipe>> RECIPE_TYPES = new EnumMap<>(MachineGeneratorType.class);

    static {
        for (MachineGeneratorType type : MachineGeneratorType.values()) {
            RECIPE_TYPES.put(type, RecipeType.create(ExtraUtilities.MODID,
                    "generator_" + type.name().toLowerCase(), GeneratorFuelRecipe.class));
        }
    }

    public static RecipeType<GeneratorFuelRecipe> getRecipeType(MachineGeneratorType type) {
        return RECIPE_TYPES.get(type);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ExtraUtilities.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new ResonatorCategory(RESONATOR, guiHelper));
        for (var entry : RECIPE_TYPES.entrySet()) {
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
        }
        for (var entry : RECIPE_TYPES.entrySet()) {
            List<GeneratorFuelRecipe> recipes = GeneratorFuelRecipe.getRecipesFor(entry.getKey());
            if (!recipes.isEmpty()) {
                registration.addRecipes(entry.getValue(), recipes);
            }
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlocks.RESONATOR.get().asItem().getDefaultInstance(), RESONATOR);
        for (var entry : RECIPE_TYPES.entrySet()) {
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
                    return List.of(IGuiClickableArea.createBasic(
                            arrow.getArrowX(), arrow.getArrowY(), 22, 16, RESONATOR));
                }
                return List.of();
            }
        });

        registration.addGuiContainerHandler(MachineGeneratorScreen.class, new IGuiContainerHandler<>() {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(MachineGeneratorScreen screen, double mouseX, double mouseY) {
                if (screen.getMenu() instanceof MachineGeneratorMenu menu && menu instanceof HasProgressArrow arrow) {
                    MachineGeneratorType type = menu.getGeneratorType();
                    RecipeType<GeneratorFuelRecipe> rt = type != null ? RECIPE_TYPES.get(type) : null;
                    if (rt != null) {
                        RecipeType<GeneratorFuelRecipe> finalRt = rt;
                        return List.of(new IGuiClickableArea() {
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
                                recipesGui.showTypes(List.of(finalRt));
                            }
                        });
                    }
                }
                return List.of();
            }
        });
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
