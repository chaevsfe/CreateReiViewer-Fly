package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.equipment.zapper.ZapperScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ZapperScreen.class)
public abstract class ZapperScreenMixin {
    @ModifyConstant(method = "drawOnBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", constant = @Constant(intValue = 0xFF54214F))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
