package dev.chaevsfe.createreiviewer.api;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

@FunctionalInterface
public interface ViewerRecipeMapper<T extends Recipe<?>> {
    ViewerRecipe map(RecipeHolder<T> holder, ViewerRecipe.Builder recipe);
}
