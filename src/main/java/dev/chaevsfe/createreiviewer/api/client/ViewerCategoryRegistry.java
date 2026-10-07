package dev.chaevsfe.createreiviewer.api.client;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

/**
 * Where a {@link CreateViewerClientPlugin} registers its categories, extra workstations and sequenced assembly steps.
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

    /**
     * Draws the sequenced assembly steps of {@code stepType}, a recipe type id, in Create's Recipe Sequence category, in
     * REI and JEI alike.
     *
     * <p>The step's name is the one the add-on registers for that type with Create Fly's {@code AllAssemblyRecipeNames};
     * without a drawing the step shows only its name. Create's own pressing, deploying, cutting and filling steps keep
     * their machines, and a type drawn twice keeps the first drawing.
     *
     * <pre>{@code
     * registry.addAssemblyStep(Identifier.fromNamespaceAndPath("createaddition", "charging"), CABlocks.TESLA_COIL);
     * }</pre>
     */
    void addAssemblyStep(Identifier stepType, ViewerAssemblyStep drawing);

    /**
     * Same as {@link #addAssemblyStep(Identifier, ViewerAssemblyStep)}, drawing {@code icon} 24 pixels wide where Create
     * draws its machines.
     */
    default void addAssemblyStep(Identifier stepType, ItemStack icon) {
        ItemStack copy = icon.copy();
        addAssemblyStep(stepType, (canvas, index, x, y) -> canvas.itemPip(x - 4, y + 27, 24, () -> copy));
    }

    /**
     * Same as {@link #addAssemblyStep(Identifier, ItemStack)}, one stack of {@code icon}.
     */
    default void addAssemblyStep(Identifier stepType, ItemLike icon) {
        addAssemblyStep(stepType, new ItemStack(icon));
    }
}
