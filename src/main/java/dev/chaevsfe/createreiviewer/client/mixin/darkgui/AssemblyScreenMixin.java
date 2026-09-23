package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.trains.station.AssemblyScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AssemblyScreen.class)
public abstract class AssemblyScreenMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF0E2233))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }

    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF775B5B))
    private int createreiviewer$darkWarning(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF7A7A7A))
    private int createreiviewer$darkHint(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
