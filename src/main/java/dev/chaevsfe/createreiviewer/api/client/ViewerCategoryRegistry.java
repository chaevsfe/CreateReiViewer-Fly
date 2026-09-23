package dev.chaevsfe.createreiviewer.api.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Where a {@link CreateViewerClientPlugin} registers its categories and extra workstations.
 */
public interface ViewerCategoryRegistry {
    /**
     * Registers an add-on category, drawn in REI and JEI from its {@link ViewerLayout}.
     */
    void add(ViewerCategory category);

    /**
     * Adds workstations (REI) and recipe catalysts (JEI) to a category the add-on does not own.
     *
     * <p>{@code category} is either one of Create's categories, named by {@link CreateViewerCategories} (for example an
     * extra sandpaper on {@link CreateViewerCategories#SANDPAPER_POLISHING}), or the id of another add-on's category.
     * The stacks are appended after the category's own workstations, in registration order. A category that neither
     * viewer knows is skipped with a warning. Workstations for the add-on's own categories belong in
     * {@link ViewerCategory.Builder#workstations}.
     *
     * <pre>{@code
     * registry.addWorkstations(CreateViewerCategories.SANDPAPER_POLISHING, CAItems.DIAMOND_GRIT_SANDPAPER);
     * }</pre>
     */
    void addWorkstations(Identifier category, ItemStack... stacks);

    /**
     * Same as {@link #addWorkstations(Identifier, ItemStack...)}, one stack of each item.
     */
    default void addWorkstations(Identifier category, ItemLike... items) {
        ItemStack[] stacks = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            stacks[i] = new ItemStack(items[i]);
        }
        addWorkstations(category, stacks);
    }
}
