package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.compat.computercraft.ComputerScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ComputerScreen.class)
public abstract class ComputerScreenMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF442000))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF7A7A7A))
    private int createreiviewer$darkHint(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
