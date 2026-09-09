package dev.chaevsfe.createreiviewer.display;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

public final class CreateReiCategories {
    public static final CategoryIdentifier<CreateReiDisplay> MIXING = of("mixing");

    private CreateReiCategories() {
    }

    private static CategoryIdentifier<CreateReiDisplay> of(String path) {
        return CategoryIdentifier.of(CreateReiViewer.MOD_ID, path);
    }
}
