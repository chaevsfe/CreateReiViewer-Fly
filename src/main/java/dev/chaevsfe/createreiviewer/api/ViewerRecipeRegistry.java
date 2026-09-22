package dev.chaevsfe.createreiviewer.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.function.Predicate;

/**
 * Where a {@link CreateViewerPlugin} registers its categories.
 *
 * <p>{@link #add} maps every recipe of {@code type} that is an instance of {@code recipeClass} and passes
 * {@code filter} into one {@link ViewerRecipe} of {@code category}. The mapper receives a builder already bound to
 * the category and to the recipe's id, and may return {@code null} to skip a recipe. One recipe type may feed several
 * categories through several calls.
 *
 * <p>{@link #synchronize} opts a serializer into Fabric's recipe synchronization, which is how a JEI client receives
 * the recipes at all. It is skipped on a client without JEI or RRV.
 */
public interface ViewerRecipeRegistry {
    <T extends Recipe<?>> void add(
        Identifier category,
        RecipeType<? super T> type,
        Class<T> recipeClass,
        Predicate<RecipeHolder<T>> filter,
        ViewerRecipeMapper<T> mapper
    );

    default <T extends Recipe<?>> void add(
        Identifier category,
        RecipeType<? super T> type,
        Class<T> recipeClass,
        ViewerRecipeMapper<T> mapper
    ) {
        add(category, type, recipeClass, holder -> true, mapper);
    }

    void synchronize(RecipeSerializer<?>... serializers);
}
