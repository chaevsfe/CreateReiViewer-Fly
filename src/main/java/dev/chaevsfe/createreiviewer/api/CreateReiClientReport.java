package dev.chaevsfe.createreiviewer.api;

import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class CreateReiClientReport {
    private static final Map<String, List<CategoryIdentifier<? extends CreateReiDisplay>>> GROUPS = new LinkedHashMap<>();

    private CreateReiClientReport() {
    }

    public static synchronized void register(String label, List<CategoryIdentifier<? extends CreateReiDisplay>> categories) {
        GROUPS.put(label, List.copyOf(categories));
    }

    public static synchronized Map<String, List<CategoryIdentifier<? extends CreateReiDisplay>>> groups() {
        return Map.copyOf(GROUPS);
    }
}
