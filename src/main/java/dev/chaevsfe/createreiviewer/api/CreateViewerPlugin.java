package dev.chaevsfe.createreiviewer.api;

/**
 * Add-on entry point of the Create Fly Recipe Viewer: one recipe-to-model mapping per category, shown in
 * Roughly Enough Items and in Just Enough Items from the same source.
 *
 * <p>Declare an implementation under the {@code "createreiviewer"} entrypoint key in {@code fabric.mod.json}. It is
 * common code: it runs on dedicated servers (REI builds its displays there) and on clients (JEI maps the recipes the
 * server synchronized). The viewer calls it once, lazily, after every mod's {@code main} entrypoint has run, so it may
 * touch the add-on's registries. Nothing here may reference a REI, JEI or client class.
 *
 * <p>Draw each category from the {@code "createreiviewer_client"} entrypoint, see
 * {@link dev.chaevsfe.createreiviewer.api.client.CreateViewerClientPlugin}. A category id is an ordinary
 * {@code Identifier} in the add-on's namespace, and the same id is the REI category and the JEI recipe type.
 *
 * <p>JEI only sees recipes whose serializers the server synchronizes, so list them with
 * {@link ViewerRecipeRegistry#synchronize}: a dedicated server always offers them, a client asks for them only when JEI
 * or RRV is installed, and every such recipe must round-trip through its serializer's stream codec.
 *
 * <pre>{@code
 * public final class DieselViewerPlugin implements CreateViewerPlugin {
 *     public static final Identifier HAMMERING = Identifier.fromNamespaceAndPath("createdieselgenerators", "hammering");
 *
 *     @Override
 *     public void registerRecipes(ViewerRecipeRegistry registry) {
 *         registry.add(HAMMERING, CDGRecipes.HAMMERING.getType(), HammerRecipe.class, (holder, recipe) -> recipe
 *             .input(holder.value().ingredient())
 *             .catalyst(CDGItems.HAMMER.asStack())
 *             .results(holder.value().results())
 *             .build());
 *         registry.synchronize(CDGRecipes.HAMMERING.getSerializer());
 *     }
 * }
 * }</pre>
 */
public interface CreateViewerPlugin {
    String ENTRYPOINT = "createreiviewer";

    void registerRecipes(ViewerRecipeRegistry registry);
}
