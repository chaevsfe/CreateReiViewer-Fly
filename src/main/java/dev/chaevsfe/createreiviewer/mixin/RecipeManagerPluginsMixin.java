package dev.chaevsfe.createreiviewer.mixin;

import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ReloadableServerResources.class)
public class RecipeManagerPluginsMixin {
    @Inject(method = "loadResources", at = @At("HEAD"))
    private static void createreiviewer$loadAddonPlugins(CallbackInfoReturnable<CompletableFuture<ReloadableServerResources>> info) {
        ViewerPlugins.load();
    }
}
