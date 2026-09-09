package dev.chaevsfe.createreiviewer.display;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import java.util.List;

public final class CreateReiCategories {
    public static final CategoryIdentifier<CreateReiDisplay> AUTOMATIC_PACKING = of("automatic_packing");
    public static final CategoryIdentifier<CreateReiDisplay> PACKING = of("packing");
    public static final CategoryIdentifier<CreateReiDisplay> PRESSING = of("pressing");
    public static final CategoryIdentifier<CreateReiDisplay> AUTOMATIC_SHAPELESS = of("automatic_shapeless");
    public static final CategoryIdentifier<CreateReiDisplay> MIXING = of("mixing");
    public static final CategoryIdentifier<CreateReiDisplay> MILLING = of("milling");
    public static final CategoryIdentifier<CreateReiDisplay> SAWING = of("sawing");
    public static final CategoryIdentifier<CreateReiDisplay> CRUSHING = of("crushing");
    public static final CategoryIdentifier<CreateReiDisplay> MYSTERY_CONVERSION = of("mystery_conversion");
    public static final CategoryIdentifier<CreateReiDisplay> ITEM_APPLICATION = of("item_application");
    public static final CategoryIdentifier<CreateReiDisplay> DEPLOYING = of("deploying");
    public static final CategoryIdentifier<CreateReiDisplay> DRAINING = of("draining");
    public static final CategoryIdentifier<CreateReiGridDisplay> MECHANICAL_CRAFTING = ofGrid("mechanical_crafting");
    public static final CategoryIdentifier<CreateReiDisplay> SPOUT_FILLING = of("spout_filling");
    public static final CategoryIdentifier<CreateReiDisplay> SANDPAPER_POLISHING = of("sandpaper_polishing");
    public static final CategoryIdentifier<CreateReiSequenceDisplay> SEQUENCED_ASSEMBLY = ofSequence("sequenced_assembly");
    public static final CategoryIdentifier<CreateReiDisplay> FAN_BLASTING = of("fan_blasting");
    public static final CategoryIdentifier<CreateReiDisplay> FAN_HAUNTING = of("fan_haunting");
    public static final CategoryIdentifier<CreateReiDisplay> FAN_SMOKING = of("fan_smoking");
    public static final CategoryIdentifier<CreateReiDisplay> FAN_WASHING = of("fan_washing");
    public static final CategoryIdentifier<CreateReiDisplay> AUTOMATIC_BREWING = of("automatic_brewing");
    public static final CategoryIdentifier<CreateReiDisplay> BLOCK_CUTTING = of("block_cutting");

    public static final List<CategoryIdentifier<? extends CreateReiDisplay>> ALL = List.of(
        AUTOMATIC_PACKING,
        PACKING,
        PRESSING,
        AUTOMATIC_SHAPELESS,
        MIXING,
        MILLING,
        SAWING,
        CRUSHING,
        MYSTERY_CONVERSION,
        ITEM_APPLICATION,
        DEPLOYING,
        DRAINING,
        MECHANICAL_CRAFTING,
        SPOUT_FILLING,
        SANDPAPER_POLISHING,
        SEQUENCED_ASSEMBLY,
        FAN_BLASTING,
        FAN_HAUNTING,
        FAN_SMOKING,
        FAN_WASHING,
        AUTOMATIC_BREWING,
        BLOCK_CUTTING
    );

    private CreateReiCategories() {
    }

    private static CategoryIdentifier<CreateReiDisplay> of(String path) {
        return CategoryIdentifier.of(CreateReiViewer.MOD_ID, path);
    }

    private static CategoryIdentifier<CreateReiGridDisplay> ofGrid(String path) {
        return CategoryIdentifier.of(CreateReiViewer.MOD_ID, path);
    }

    private static CategoryIdentifier<CreateReiSequenceDisplay> ofSequence(String path) {
        return CategoryIdentifier.of(CreateReiViewer.MOD_ID, path);
    }
}
