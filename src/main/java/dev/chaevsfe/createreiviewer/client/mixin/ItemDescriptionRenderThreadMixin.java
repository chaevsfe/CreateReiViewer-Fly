package dev.chaevsfe.createreiviewer.client.mixin;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zurrtum.create.client.foundation.item.ItemDescription;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemDescription.Modifier.class)
public class ItemDescriptionRenderThreadMixin {
    @Inject(method = "modify", at = @At("HEAD"), cancellable = true)
    private void createreiviewer$describeOnlyOnRenderThread(List<Component> tooltip, Player player, CallbackInfo info) {
        if (!RenderSystem.isOnRenderThread()) {
            info.cancel();
        }
    }
}
