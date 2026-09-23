package dev.chaevsfe.createreiviewer.api.client;

import net.minecraft.resources.Identifier;

import java.util.List;

/**
 * Ids of Create's own recipe categories, for {@link ViewerCategoryRegistry#addWorkstations}.
 *
 * <p>Each id is Create Fly's JEI recipe type id ({@code create:<name>}). The viewer maps it to its own REI category
 * of the same name, so one id reaches the page in both viewers.
 */
public final class CreateViewerCategories {
    public static final Identifier AUTOMATIC_PACKING = create("automatic_packing");
    public static final Identifier PACKING = create("packing");
    public static final Identifier PRESSING = create("pressing");
    public static final Identifier AUTOMATIC_SHAPELESS = create("automatic_shapeless");
    public static final Identifier MIXING = create("mixing");
    public static final Identifier MILLING = create("milling");
    public static final Identifier SAWING = create("sawing");
    public static final Identifier CRUSHING = create("crushing");
    public static final Identifier MYSTERY_CONVERSION = create("mystery_conversion");
    public static final Identifier ITEM_APPLICATION = create("item_application");
    public static final Identifier DEPLOYING = create("deploying");
    public static final Identifier DRAINING = create("draining");
    public static final Identifier MECHANICAL_CRAFTING = create("mechanical_crafting");
    public static final Identifier SPOUT_FILLING = create("spout_filling");
    public static final Identifier SANDPAPER_POLISHING = create("sandpaper_polishing");
    public static final Identifier SEQUENCED_ASSEMBLY = create("sequenced_assembly");
    public static final Identifier FAN_BLASTING = create("fan_blasting");
    public static final Identifier FAN_HAUNTING = create("fan_haunting");
    public static final Identifier FAN_SMOKING = create("fan_smoking");
    public static final Identifier FAN_WASHING = create("fan_washing");
    public static final Identifier AUTOMATIC_BREWING = create("automatic_brewing");
    public static final Identifier BLOCK_CUTTING = create("block_cutting");

    public static final List<Identifier> ALL = List.of(
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

    private CreateViewerCategories() {
    }

    private static Identifier create(String path) {
        return Identifier.fromNamespaceAndPath("create", path);
    }
}
