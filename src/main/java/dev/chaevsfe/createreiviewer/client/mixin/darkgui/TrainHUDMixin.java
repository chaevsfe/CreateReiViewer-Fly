package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.trains.TrainHUD;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(TrainHUD.class)
public abstract class TrainHUDMixin {
    @ModifyConstant(method = "renderOverlay(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)Z", constant = @Constant(intValue = 0xFF544D45))
    private static int createreiviewer$darkPrompt(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
