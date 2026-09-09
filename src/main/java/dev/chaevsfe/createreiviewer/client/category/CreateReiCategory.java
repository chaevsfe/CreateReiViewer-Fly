package dev.chaevsfe.createreiviewer.client.category;

import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;

public abstract class CreateReiCategory implements DisplayCategory<CreateReiDisplay> {
    public static final int CONTENT_WIDTH = 177;
    public static final int PADDING = 4;

    protected abstract int contentHeight();

    protected int contentOverhangTop() {
        return 0;
    }

    @Override
    public int getDisplayWidth(CreateReiDisplay display) {
        return CONTENT_WIDTH + PADDING * 2;
    }

    @Override
    public int getDisplayHeight() {
        return contentHeight() + contentOverhangTop() + PADDING * 2;
    }

    protected int originX(me.shedaniel.math.Rectangle bounds) {
        return bounds.x + PADDING;
    }

    protected int originY(me.shedaniel.math.Rectangle bounds) {
        return bounds.y + PADDING + contentOverhangTop();
    }
}
