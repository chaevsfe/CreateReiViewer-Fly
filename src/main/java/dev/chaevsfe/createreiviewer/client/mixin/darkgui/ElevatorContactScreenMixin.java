package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.contraptions.elevator.ElevatorContactScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ElevatorContactScreen.class)
public abstract class ElevatorContactScreenMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF2F3738))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }
}
