package dev.chaevsfe.createreiviewer.client.darkgui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.NativeImage;

import dev.chaevsfe.createreiviewer.CreateReiViewer;

import net.fabricmc.fabric.api.resource.v1.reloader.SimpleReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

final class DarkGuiReloadListener extends SimpleReloadListener<DarkGuiReloadListener.Prepared> {
    private static final String GUI_ROOT = "assets/" + DarkGui.CREATE_MOD_ID + "/textures/gui";
    private static final Set<String> SKIP = Set.of(
        "title/background/panorama_0.png",
        "title/background/panorama_1.png",
        "title/background/panorama_2.png",
        "title/background/panorama_3.png",
        "title/background/panorama_4.png",
        "title/background/panorama_5.png",
        "platform_icons/curseforge.png",
        "platform_icons/modrinth.png",
        "logo.png",
        "icons.png",
        "trainmap_sprite_sheet.png"
    );

    private Set<Identifier> registered = Set.of();

    record Texture(Identifier id, NativeImage image) {
    }

    record Prepared(
        boolean enabled,
        List<Texture> textures,
        int scanned,
        int skipped,
        int unchanged,
        int leftToHigherPack,
        List<String> higherPacks,
        int errors,
        String ourPackId,
        String createPackId,
        int ourIndex,
        int packCount,
        String topPack,
        List<String> order
    ) {
    }

    @Override
    protected Prepared prepare(PreparableReloadListener.SharedState state) {
        ResourceManager resources = state.resourceManager();
        List<String> order = resources.listPacks().map(PackResources::packId).toList();
        Optional<Resource> marker = resources.getResource(DarkGui.MARKER);
        if (marker.isEmpty()) {
            return new Prepared(false, List.of(), 0, 0, 0, 0, List.of(), 0, "", "", -1, order.size(), last(order), order);
        }
        String ours = marker.get().sourcePackId();
        DarkGuiRank rank = new DarkGuiRank(order, ours, DarkGui.CREATE_MOD_ID);
        List<Texture> textures = new ArrayList<>();
        List<String> higherPacks = new ArrayList<>();
        DarkGuiRecolour recolour = new DarkGuiRecolour();
        int scanned = 0;
        int skipped = 0;
        int unchanged = 0;
        int left = 0;
        int errors = 0;
        String createPackId = "";
        for (GuiFile file : createGuiTextures()) {
            String relative = file.relative();
            scanned++;
            if (SKIP.contains(relative)) {
                skipped++;
                continue;
            }
            Identifier id = Identifier.fromNamespaceAndPath(DarkGui.CREATE_MOD_ID, "textures/gui/" + relative);
            Optional<Resource> current = resources.getResource(id);
            String provider = current.map(Resource::sourcePackId).orElse(null);
            if (rank.higherPackWins(provider)) {
                left++;
                if (!higherPacks.contains(provider)) {
                    higherPacks.add(provider);
                }
                continue;
            }
            if (createPackId.isEmpty() && provider != null) {
                createPackId = provider;
            }
            NativeImage image = null;
            try (InputStream in = Files.newInputStream(file.path())) {
                image = NativeImage.read(in);
                if (recolour.recolour(image) == null) {
                    image.close();
                    unchanged++;
                    continue;
                }
                textures.add(new Texture(id, image));
            } catch (IOException | RuntimeException e) {
                if (image != null) {
                    image.close();
                }
                errors++;
                CreateReiViewer.LOGGER.warn("Dark GUI: could not recolour {}", id, e);
            }
        }
        return new Prepared(true, textures, scanned, skipped, unchanged, left, higherPacks, errors, ours, createPackId,
            rank.indexOfOurs(), rank.size(), rank.top(), order);
    }

    @Override
    protected void apply(Prepared prepared, PreparableReloadListener.SharedState state) {
        TextureManager textures = Minecraft.getInstance().getTextureManager();
        Set<Identifier> now = new HashSet<>();
        for (Texture texture : prepared.textures()) {
            Identifier id = texture.id();
            textures.register(id, new DynamicTexture(() -> "Create Fly: Dark GUI " + id, texture.image()));
            now.add(id);
        }
        int released = 0;
        for (Identifier id : registered) {
            if (!now.contains(id)) {
                textures.release(id);
                released++;
            }
        }
        registered = Set.copyOf(now);
        DarkGui.setActive(prepared.enabled());
        List<String> selected = List.copyOf(Minecraft.getInstance().getResourcePackRepository().getSelectedIds());
        if (!prepared.enabled()) {
            CreateReiViewer.LOGGER.info(
                "Dark GUI: disabled, 0 textures registered, {} released, 0 left to a higher pack, 0 errors (pack '{}' not selected; {} packs, top = '{}', selected ids first = '{}', last = '{}')",
                released, DarkGui.PACK, prepared.packCount(), prepared.topPack(), first(selected), last(selected)
            );
            return;
        }
        CreateReiViewer.LOGGER.info(
            "Dark GUI: enabled, {} textures registered, {} left to a higher pack {}, {} unchanged, {} skipped by name, {} scanned, {} released, {} errors (our pack '{}' at {} of {} bottom to top, top = '{}', Create Fly textures from pack '{}', selected ids first = '{}', last = '{}', same order as the reload: {})",
            prepared.textures().size(), prepared.leftToHigherPack(), prepared.higherPacks(), prepared.unchanged(),
            prepared.skipped(), prepared.scanned(), released, prepared.errors(), prepared.ourPackId(), prepared.ourIndex(),
            prepared.packCount(), prepared.topPack(), prepared.createPackId(), first(selected), last(selected),
            selected.equals(prepared.order())
        );
    }

    private static List<GuiFile> createGuiTextures() {
        Optional<Path> root = FabricLoader.getInstance().getModContainer(DarkGui.CREATE_MOD_ID)
            .flatMap(mod -> mod.findPath(GUI_ROOT));
        if (root.isEmpty()) {
            CreateReiViewer.LOGGER.warn("Dark GUI: Create Fly's {} was not found", GUI_ROOT);
            return List.of();
        }
        try (Stream<Path> walk = Files.walk(root.get())) {
            return walk.filter(Files::isRegularFile)
                .filter(path -> path.getFileName().toString().endsWith(".png"))
                .map(path -> new GuiFile(path, relative(root.get(), path)))
                .sorted(Comparator.comparing(GuiFile::relative))
                .toList();
        } catch (IOException e) {
            CreateReiViewer.LOGGER.warn("Dark GUI: could not list Create Fly's {}", GUI_ROOT, e);
            return List.of();
        }
    }

    private record GuiFile(Path path, String relative) {
    }

    private static String relative(Path root, Path file) {
        List<String> parts = new ArrayList<>();
        for (Path part : root.relativize(file)) {
            parts.add(part.toString());
        }
        return String.join("/", parts);
    }

    private static String first(List<String> ids) {
        return ids.isEmpty() ? "" : ids.get(0);
    }

    private static String last(List<String> ids) {
        return ids.isEmpty() ? "" : ids.get(ids.size() - 1);
    }
}
