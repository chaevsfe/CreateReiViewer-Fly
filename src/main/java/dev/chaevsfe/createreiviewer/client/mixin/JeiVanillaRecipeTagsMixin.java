package dev.chaevsfe.createreiviewer.client.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "mezz.jei.common.recipes.VanillaClientRecipeLoader", remap = false)
public class JeiVanillaRecipeTagsMixin {
    @Redirect(
        method = "loadVanillaRecipeRegistry",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/tags/TagLoader;loadTagsForExistingRegistries(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/core/RegistryAccess;)Ljava/util/List;",
            ordinal = 0
        ),
        require = 0
    )
    private static List<Registry.PendingTags<?>> createreiviewer$resolveTagsAgainstConnection(ResourceManager resources, RegistryAccess registries) {
        if (!ViewerConfig.fixJeiVanillaRecipeTags()) {
            return TagLoader.loadTagsForExistingRegistries(resources, registries);
        }
        try {
            List<Registry.PendingTags<?>> tags = registries.registries()
                .flatMap(entry -> createreiviewer$connectionTags(entry.value()).stream())
                .toList();
            CreateReiViewer.LOGGER.info(
                "JEI is reading vanilla recipes from client resources; resolving their tags against the connection's registries ({} is true)",
                ViewerConfig.FIX_JEI_VANILLA_RECIPE_TAGS
            );
            return tags;
        } catch (RuntimeException exception) {
            CreateReiViewer.LOGGER.warn("Could not keep the connection's tags while JEI reads vanilla recipes from client resources", exception);
            return TagLoader.loadTagsForExistingRegistries(resources, registries);
        }
    }

    private static <T> Optional<Registry.PendingTags<?>> createreiviewer$connectionTags(Registry<T> registry) {
        Map<TagKey<T>, List<Holder<T>>> tags = new HashMap<>();
        registry.getTags().forEach(named -> tags.put(named.key(), named.stream().toList()));
        if (tags.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(registry.prepareTagReload(new TagLoader.LoadResult<>(registry.key(), tags)));
    }
}
