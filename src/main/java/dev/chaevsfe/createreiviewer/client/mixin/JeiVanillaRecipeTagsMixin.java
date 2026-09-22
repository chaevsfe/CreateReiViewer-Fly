package dev.chaevsfe.createreiviewer.client.mixin;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.packs.resources.CloseableResourceManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "mezz.jei.common.recipes.VanillaClientRecipeLoader", remap = false)
public class JeiVanillaRecipeTagsMixin {
    @Inject(method = "createVanillaServerDataSerializationContext", at = @At("HEAD"), cancellable = true, require = 0)
    private static void createreiviewer$resolveTagsAgainstConnection(
        RegistryAccess registryAccess,
        CloseableResourceManager resourceManager,
        CallbackInfoReturnable<RegistryOps<JsonElement>> info
    ) {
        if (!ViewerConfig.fixJeiVanillaRecipeTags()) {
            return;
        }
        CreateReiViewer.LOGGER.info(
            "JEI is reading vanilla recipes from client resources; resolving their tags against the connection's registries ({} is true)",
            ViewerConfig.FIX_JEI_VANILLA_RECIPE_TAGS
        );
        info.setReturnValue(registryAccess.createSerializationContext(JsonOps.INSTANCE));
    }
}
