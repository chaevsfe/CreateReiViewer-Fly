package dev.chaevsfe.createreiviewer.registry;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.ViewerRecipeMapper;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Predicate;

public record ViewerMapping<T extends Recipe<?>>(
    String owner,
    Identifier category,
    RecipeType<? super T> type,
    Class<T> recipeClass,
    Predicate<RecipeHolder<T>> filter,
    ViewerRecipeMapper<T> mapper
) {
    public boolean accepts(RecipeHolder<?> holder) {
        return recipeClass.isInstance(holder.value()) && filter.test(cast(holder));
    }

    public ViewerRecipe map(RecipeHolder<?> holder) {
        if (!recipeClass.isInstance(holder.value())) {
            return null;
        }
        return mapTyped(cast(holder));
    }

    public ViewerRecipe mapTyped(RecipeHolder<T> holder) {
        try {
            if (!filter.test(holder)) {
                return null;
            }
            ViewerRecipe recipe = mapper.map(holder, ViewerRecipe.builder(category).location(holder));
            if (recipe != null && !recipe.category().equals(category)) {
                CreateReiViewer.LOGGER.warn("{} mapped recipe {} into category {}, registered as {}",
                    owner, holder.id().identifier(), recipe.category(), category);
            }
            return recipe;
        } catch (RuntimeException exception) {
            CreateReiViewer.LOGGER.warn("{} failed to map recipe {} for category {}", owner, holder.id().identifier(), category, exception);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private RecipeHolder<T> cast(RecipeHolder<?> holder) {
        return (RecipeHolder<T>) holder;
    }
}
