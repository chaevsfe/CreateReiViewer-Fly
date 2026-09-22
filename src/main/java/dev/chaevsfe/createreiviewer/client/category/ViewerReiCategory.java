package dev.chaevsfe.createreiviewer.client.category;

import dev.chaevsfe.createreiviewer.api.ViewerIngredient;
import dev.chaevsfe.createreiviewer.api.client.ViewerCategory;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.client.widget.ReiCanvas;
import dev.chaevsfe.createreiviewer.client.widget.StackIconRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.ViewerReiDisplays;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;

import java.util.IdentityHashMap;
import java.util.Map;

public class ViewerReiCategory extends CreateReiCategory<CreateReiDisplay> {
    private final ViewerCategory category;
    private final CategoryIdentifier<CreateReiDisplay> identifier;
    private final Renderer icon;

    public ViewerReiCategory(ViewerCategory category) {
        super(category.id().toString());
        this.category = category;
        this.identifier = CategoryIdentifier.of(category.id());
        this.icon = new StackIconRenderer(category.icon());
    }

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return identifier;
    }

    @Override
    public Component getTitle() {
        return category.title();
    }

    @Override
    public Renderer getIcon() {
        return icon;
    }

    @Override
    public int getDisplayWidth(CreateReiDisplay display) {
        return category.width() + PADDING * 2;
    }

    @Override
    protected int contentHeight() {
        return category.height();
    }

    @Override
    protected int contentOverhangTop() {
        return category.overhangTop();
    }

    @Override
    protected void build(CreateReiDisplay display, Panel panel) {
        Map<ViewerIngredient, EntryIngredient> originals = new IdentityHashMap<>();
        category.layout().build(ViewerReiDisplays.recipe(display, originals), new ReiCanvas(panel, originals));
    }
}
