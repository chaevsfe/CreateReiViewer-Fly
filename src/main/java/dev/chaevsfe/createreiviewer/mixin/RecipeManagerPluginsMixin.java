package dev.chaevsfe.createreiviewer.mixin;

import dev.chaevsfe.createreiviewer.registry.ViewerPlugins;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeManager.class)
public class RecipeManagerPluginsMixin {
    @Inject(
        method = "prepare(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)Lnet/minecraft/world/item/crafting/RecipeMap;",
        at = @At("HEAD")
    )
    private void createreiviewer$loadAddonPlugins(ResourceManager resources, ProfilerFiller profiler, CallbackInfoReturnable<RecipeMap> info) {
        ViewerPlugins.load();
    }
}
