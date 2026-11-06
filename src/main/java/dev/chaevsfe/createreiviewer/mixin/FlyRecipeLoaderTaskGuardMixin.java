package dev.chaevsfe.createreiviewer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.zurrtum.create.content.processing.RecipeLoaderTask;
import net.minecraft.resources.RegistryLoadTask.PendingRegistration;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeLoaderTask.class)
public class FlyRecipeLoaderTaskGuardMixin {
    @Inject(
        method = "apply(Lnet/minecraft/resources/Identifier;Lnet/minecraft/server/packs/resources/Resource;)Lnet/minecraft/resources/RegistryLoadTask$PendingRegistration;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/RegistryLoadTask$PendingRegistration;value()Lcom/mojang/datafixers/util/Either;"),
        cancellable = true,
        require = 0
    )
    private void createreiviewer$skipFalseCondition(CallbackInfoReturnable<PendingRegistration<Recipe<?>>> info, @Local PendingRegistration<Recipe<?>> entry) {
        if (entry == null) {
            info.setReturnValue(null);
        }
    }
}
