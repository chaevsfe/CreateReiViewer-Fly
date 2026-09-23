package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.trains.station.StationScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StationScreen.class)
public abstract class StationScreenMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF7A7A7A))
    private int createreiviewer$darkHint(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
