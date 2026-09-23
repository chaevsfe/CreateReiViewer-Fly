package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.equipment.clipboard.ClipboardScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ClipboardScreen.class)
public abstract class ClipboardScreenMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF311A00))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "renderCursor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lcom/zurrtum/create/client/content/equipment/clipboard/ClipboardScreen$Pos2i;Z)V", constant = @Constant(intValue = 0xFF000000))
    private int createreiviewer$darkCursor(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
