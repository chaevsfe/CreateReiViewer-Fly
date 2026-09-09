package dev.chaevsfe.createreiviewer.plugin;

import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.kinetics.mixer.MixingRecipe;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplays;
import io.netty.buffer.Unpooled;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.plugins.PluginManager;
import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
import me.shedaniel.rei.api.common.registry.ReloadStage;
import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.List;

public class CreateReiCommonPlugin implements REICommonPlugin {
    private static final List<CategoryIdentifier<CreateReiDisplay>> CATEGORIES = List.of(CreateReiCategories.MIXING);

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(CreateReiDisplay.serializerId(), CreateReiDisplay.SERIALIZER);
        CreateReiViewer.LOGGER.info("Display serializer registered as {}", CreateReiDisplay.serializerId());
    }

    @Override
    public void registerDisplays(ServerDisplayRegistry registry) {
        registry.<MixingRecipe, CreateReiDisplay>beginRecipeFiller(MixingRecipe.class)
            .filterType(AllRecipeTypes.MIXING)
            .fill(CreateReiDisplays::mixing);
        CreateReiViewer.LOGGER.info("Recipe fillers registered for {} category/categories", CATEGORIES.size());
    }

    @Override
    public void postStage(PluginManager<REICommonPlugin> manager, ReloadStage stage) {
        if (stage != ReloadStage.END) {
            return;
        }
        ServerDisplayRegistry registry = manager.get(ServerDisplayRegistry.class);
        if (registry == null) {
            CreateReiViewer.LOGGER.warn("No ServerDisplayRegistry after reload; no Create displays were built");
            return;
        }
        for (CategoryIdentifier<CreateReiDisplay> category : CATEGORIES) {
            List<CreateReiDisplay> displays = registry.get(category);
            CreateReiViewer.LOGGER.info("Registered {} displays for category {}", displays.size(), category.getIdentifier());
            if (displays.isEmpty()) {
                CreateReiViewer.LOGGER.warn("Category {} built ZERO displays", category.getIdentifier());
            }
            reportWireRoundTrip(category, displays);
        }
    }

    private static void reportWireRoundTrip(CategoryIdentifier<CreateReiDisplay> category, List<CreateReiDisplay> displays) {
        if (displays.isEmpty()) {
            return;
        }
        int checked = 0;
        int mismatched = 0;
        for (CreateReiDisplay display : displays) {
            try {
                byte[] first = encode(display);
                RegistryFriendlyByteBuf in = buffer();
                in.writeBytes(first);
                CreateReiDisplay decoded = CreateReiDisplay.STREAM_CODEC.decode(in);
                byte[] second = encode(decoded);
                checked++;
                if (!java.util.Arrays.equals(first, second)) {
                    mismatched++;
                    CreateReiViewer.LOGGER.warn(
                        "Wire round trip changed display {}",
                        display.getDisplayLocation().map(Object::toString).orElse("<unknown>")
                    );
                }
            } catch (Exception exception) {
                mismatched++;
                CreateReiViewer.LOGGER.warn(
                    "Wire round trip failed for display {}",
                    display.getDisplayLocation().map(Object::toString).orElse("<unknown>"),
                    exception
                );
            }
        }
        CreateReiViewer.LOGGER.info(
            "Wire round trip for category {}: {} checked, {} mismatched",
            category.getIdentifier(),
            checked,
            mismatched
        );
    }

    private static byte[] encode(CreateReiDisplay display) {
        RegistryFriendlyByteBuf buf = buffer();
        CreateReiDisplay.STREAM_CODEC.encode(buf, display);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    private static RegistryFriendlyByteBuf buffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), me.shedaniel.rei.api.common.display.basic.BasicDisplay.registryAccess());
    }
}
