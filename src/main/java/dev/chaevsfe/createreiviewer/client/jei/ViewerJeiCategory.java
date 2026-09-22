package dev.chaevsfe.createreiviewer.client.jei;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.api.ViewerRecipe;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class ViewerJeiCategory implements IRecipeCategory<ViewerRecipe> {
    private final ViewerCategory category;
    private final IRecipeType<ViewerRecipe> type;
    private final IDrawable icon;
    private boolean warned;

    public ViewerJeiCategory(ViewerCategory category, IRecipeType<ViewerRecipe> type) {
        this.category = category;
        this.type = type;
        this.icon = new IconDrawable(category.icon());
    }

    @Override
    public IRecipeType<ViewerRecipe> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return category.title();
    }

    @Override
    public int getWidth() {
        return category.width();
    }

    @Override
    public int getHeight() {
        return category.height() + category.overhangTop();
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, ViewerRecipe recipe, IFocusGroup focuses) {
        try {
            category.layout().build(recipe, new JeiSlotCanvas(builder, category.overhangTop()));
        } catch (RuntimeException exception) {
            warn("lay out", recipe, exception);
        }
    }

    @Override
    public void draw(ViewerRecipe recipe, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        try {
            category.layout().build(recipe, new JeiDrawCanvas(graphics, category.overhangTop()));
        } catch (RuntimeException exception) {
            warn("draw", recipe, exception);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ViewerRecipe recipe, IRecipeSlotsView slots, double mouseX, double mouseY) {
        try {
            category.layout().build(recipe, new JeiTooltipCanvas(tooltip, mouseX, mouseY, category.overhangTop()));
        } catch (RuntimeException exception) {
            warn("build the tooltip of", recipe, exception);
        }
    }

    @Override
    public Identifier getIdentifier(ViewerRecipe recipe) {
        return recipe.location().orElse(null);
    }

    private void warn(String action, ViewerRecipe recipe, RuntimeException exception) {
        if (warned) {
            return;
        }
        warned = true;
        CreateReiViewer.LOGGER.warn("Could not {} recipe {} in JEI category {}; further failures in it are not logged",
            action, recipe.location().map(Identifier::toString).orElse("<unknown>"), category.id(), exception);
    }
}
