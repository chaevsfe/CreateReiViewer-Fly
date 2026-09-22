package dev.chaevsfe.createreiviewer.api;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public final class CreateReiApi {
    public static final String MOD_ID = CreateReiViewer.MOD_ID;
    public static final String REI_MOD_ID = "roughlyenoughitems";
    public static final double PLUGIN_PRIORITY = -200.0D;
    public static final double VIEWER_PLUGIN_PRIORITY = PLUGIN_PRIORITY - 50.0D;

    private CreateReiApi() {
    }

    @Deprecated
    public static boolean isAvailable() {
        FabricLoader loader = FabricLoader.getInstance();
        return loader.isModLoaded(MOD_ID) && loader.isModLoaded(REI_MOD_ID);
    }

    @Deprecated
    public static CategoryIdentifier<CreateReiDisplay> category(String namespace, String path) {
        return CategoryIdentifier.of(namespace, path);
    }

    public static Identifier categoryId(CategoryIdentifier<?> category) {
        return Identifier.fromNamespaceAndPath(category.getNamespace(), category.getPath());
    }

    @Deprecated
    public static <T extends Recipe<?>> void fill(
        ServerDisplayRegistry registry,
        Class<T> recipeClass,
        RecipeType<T> type,
        Function<RecipeHolder<T>, CreateReiDisplay> mapper
    ) {
        registry.<T, CreateReiDisplay>beginRecipeFiller(recipeClass).filterType(type).fill(mapper);
    }

    @Deprecated
    public static <T extends Recipe<?>> void fill(
        ServerDisplayRegistry registry,
        Class<T> recipeClass,
        RecipeType<T> type,
        Predicate<RecipeHolder<T>> filter,
        Function<RecipeHolder<T>, CreateReiDisplay> mapper
    ) {
        registry.<T, CreateReiDisplay>beginRecipeFiller(recipeClass).filterType(type).filter(filter).fill(mapper);
    }

    @Deprecated
    public static void report(
        PluginManager<REICommonPlugin> manager,
        ReloadStage stage,
        String label,
        List<CategoryIdentifier<? extends CreateReiDisplay>> categories,
        Logger logger
    ) {
        if (stage != ReloadStage.END || FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            return;
        }
        ServerDisplayRegistry registry = manager.get(ServerDisplayRegistry.class);
        if (registry == null) {
            logger.warn("{}: no ServerDisplayRegistry after reload; no displays were built", label);
            return;
        }
        int total = 0;
        int empty = 0;
        for (CategoryIdentifier<? extends CreateReiDisplay> category : categories) {
            List<? extends Display> displays = registry.get(category);
            total += displays.size();
            logger.info("Registered {} displays for category {}", displays.size(), category.getIdentifier());
            if (displays.isEmpty()) {
                empty++;
                logger.warn("Category {} built ZERO displays", category.getIdentifier());
            }
        }
        logger.info("{} display total: {} across {} categories, {} empty", label, total, categories.size(), empty);
    }
}
