package dev.chaevsfe.createreiviewer.client.category;

import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class CreateReiCategory<D extends CreateReiDisplay> implements DisplayCategory<D> {
    public static final int CONTENT_WIDTH = 177;
    public static final int PADDING = 4;

    private final String titleKey;

    protected CreateReiCategory(String titleKey) {
        this.titleKey = titleKey;
    }

    protected abstract int contentHeight();

    protected int contentOverhangTop() {
        return 0;
    }

    protected abstract void build(D display, Panel panel);

    @Override
    public Component getTitle() {
        return Component.translatable(titleKey);
    }

    @Override
    public int getDisplayWidth(D display) {
        return CONTENT_WIDTH + PADDING * 2;
    }

    @Override
    public int getDisplayHeight() {
        return contentHeight() + contentOverhangTop() + PADDING * 2;
    }

    @Override
    public List<Widget> setupDisplay(D display, Rectangle bounds) {
        Panel panel = new Panel(bounds.x + PADDING, bounds.y + PADDING + contentOverhangTop());
        panel.add(Widgets.createRecipeBase(bounds));
        build(display, panel);
        return panel.widgets();
    }
}
