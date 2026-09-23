package dev.chaevsfe.createreiviewer.api.client;

/**
 * Client half of a Create Fly Recipe Viewer add-on: how each category registered by a
 * {@link dev.chaevsfe.createreiviewer.api.CreateViewerPlugin} looks.
 *
 * <p>Declare an implementation under the {@code "createreiviewer_client"} entrypoint key. The viewer calls it lazily,
 * once, on the client, whether REI, JEI, both or neither is installed, so it may reference Create Fly's client classes
 * ({@code AllGuiTextures}, render states) but never a REI or JEI class.
 *
 * <p>Each {@link ViewerCategory} carries a {@link ViewerLayout} written once against {@link ViewerCanvas}. The viewer
 * draws it as a REI category and as a JEI category; {@link ViewerLayouts} holds Create's usual pieces (basin, heat
 * bar, blaze burner, output grid). Coordinates are those of Create's own JEI pages: a 177 pixel wide content area whose
 * top is {@code overhangTop} pixels below the page top. An add-on that keeps its own REI category class or its own JEI
 * plugin category for an id sets {@link ViewerCategory.Builder#reiCategory(boolean)} or
 * {@link ViewerCategory.Builder#jeiCategory(boolean)} to {@code false}, or that viewer rejects the duplicate.
 *
 * <p>{@link ViewerCategoryRegistry#addWorkstations} adds the add-on's machines or tools to a category it does not own,
 * such as one of Create's ({@link CreateViewerCategories}), in REI and JEI alike.
 *
 * <pre>{@code
 * public final class DieselViewerClientPlugin implements CreateViewerClientPlugin {
 *     @Override
 *     public void registerCategories(ViewerCategoryRegistry registry) {
 *         registry.add(ViewerCategory.builder(DieselViewerPlugin.HAMMERING)
 *             .title("createdieselgenerators.recipe.hammering")
 *             .icon(CDGItems.HAMMER)
 *             .height(55)
 *             .workstations(CDGItems.HAMMER)
 *             .layout((recipe, canvas) -> {
 *                 canvas.texture(AllGuiTextures.JEI_SHADOW, 61, 21);
 *                 canvas.texture(AllGuiTextures.JEI_LONG_ARROW, 52, 32);
 *                 canvas.slot(81, 5, recipe.catalyst(0));
 *                 canvas.slot(27, 29, recipe.input(0));
 *                 ViewerLayouts.outputGrid(canvas, recipe, 142, 29);
 *             })
 *             .build());
 *     }
 * }
 * }</pre>
 */
public interface CreateViewerClientPlugin {
    String ENTRYPOINT = "createreiviewer_client";

    void registerCategories(ViewerCategoryRegistry registry);
}
