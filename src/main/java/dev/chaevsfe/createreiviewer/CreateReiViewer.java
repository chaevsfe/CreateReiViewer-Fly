package dev.chaevsfe.createreiviewer;

import dev.chaevsfe.createreiviewer.compat.jei.SequencedAssemblySyncFix;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateReiViewer implements ModInitializer {
    public static final String MOD_ID = "createreiviewer";
    public static final Logger LOGGER = LoggerFactory.getLogger("Create Fly Recipe Viewer");

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Create Fly Recipe Viewer loaded");
        if (ViewerConfig.fixSequencedAssemblySync()) {
            SequencedAssemblySyncFix.apply();
        } else if (recipeViewerNeedingTheFix() != null) {
            LOGGER.warn(
                "{} is installed and {} is false in {}: a client joining this server disconnects while decoding Create sequenced assembly recipes, because Create Fly writes a recipe serializer raw id on the wire and that id differs between the two sides. Set {} to true in that file on the server AND on every client, or remove {}.",
                recipeViewerNeedingTheFix(),
                ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC,
                ViewerConfig.path(),
                ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC,
                recipeViewerNeedingTheFix()
            );
        }
    }

    private static String recipeViewerNeedingTheFix() {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("jei")) {
            return "Just Enough Items";
        }
        if (loader.isModLoaded("rrv")) {
            return "Reliable Recipe Viewer";
        }
        return null;
    }
}
