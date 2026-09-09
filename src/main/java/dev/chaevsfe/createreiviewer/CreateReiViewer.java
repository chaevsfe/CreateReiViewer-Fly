package dev.chaevsfe.createreiviewer;

import net.fabricmc.api.ModInitializer;
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
    }
}
