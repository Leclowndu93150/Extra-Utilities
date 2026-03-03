package com.leclowndu93150.extrautils2.event;

import com.leclowndu93150.extrautils2.ExtraUtilities;
import com.leclowndu93150.extrautils2.recipe.XPCraftingRecipe;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = ExtraUtilities.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CraftingEvents {

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (player.isCreative()) return;

        var level = player.level();
        var recipeManager = level.getRecipeManager();

        for (var holder : recipeManager.getRecipes()) {
            if (holder.value() instanceof XPCraftingRecipe xpRecipe) {
                ItemStack result = xpRecipe.getResultItem(level.registryAccess());
                if (ItemStack.isSameItem(result, event.getCrafting())) {
                    player.onEnchantmentPerformed(event.getCrafting(), xpRecipe.getXpCost());
                    return;
                }
            }
        }
    }
}
