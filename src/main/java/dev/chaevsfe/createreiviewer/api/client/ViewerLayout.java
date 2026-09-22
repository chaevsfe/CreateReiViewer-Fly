package dev.chaevsfe.createreiviewer.api.client;

import dev.chaevsfe.createreiviewer.api.ViewerRecipe;

@FunctionalInterface
public interface ViewerLayout {
    void build(ViewerRecipe recipe, ViewerCanvas canvas);
}
