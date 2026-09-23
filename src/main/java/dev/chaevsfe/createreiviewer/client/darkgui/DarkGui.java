package dev.chaevsfe.createreiviewer.client.darkgui;

import dev.chaevsfe.createreiviewer.CreateReiViewer;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public final class DarkGui {
    public static final Identifier PACK = CreateReiViewer.id("dark_gui");
    public static final Identifier LISTENER = CreateReiViewer.id("dark_gui_textures");
    public static final Identifier MARKER = CreateReiViewer.id("dark_gui/marker.json");
    public static final String PACK_NAME = "Create Fly: Dark GUI";
    public static final String CREATE_MOD_ID = "create";

    public static final int CREAM = 0xFFF3EBDE;
    public static final int CREAM_RGB = 0xF3EBDE;
    public static final int PALE_BLUE = 0xFFDDEEFF;
    public static final int PALE_GREY = 0xFFDDDDDD;
    public static final int PALE_GREY_RGB = 0xDDDDDD;

    private static volatile boolean active;

    private DarkGui() {
    }

    public static boolean active() {
        return active;
    }

    public static int text(int original, int light) {
        return active ? light : original;
    }

    static void setActive(boolean value) {
        active = value;
    }

    public static void register() {
        ModContainer viewer = FabricLoader.getInstance().getModContainer(CreateReiViewer.MOD_ID).orElseThrow();
        boolean listed = ResourceLoader.registerBuiltinPack(PACK, viewer, Component.literal(PACK_NAME), PackActivationType.NORMAL);
        ResourceLoader loader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
        loader.registerReloadListener(LISTENER, new DarkGuiReloadListener());
        loader.addListenerOrdering(ResourceReloaderKeys.Client.TEXTURES, LISTENER);
        CreateReiViewer.LOGGER.info("Dark GUI: built-in resource pack '{}' ({}) registered: {}, off by default", PACK, PACK_NAME, listed);
    }
}
