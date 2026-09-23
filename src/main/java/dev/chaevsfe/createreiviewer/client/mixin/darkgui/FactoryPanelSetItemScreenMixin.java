package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.factoryBoard.FactoryPanelSetItemScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FactoryPanelSetItemScreen.class)
public abstract class FactoryPanelSetItemScreenMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF3D3C48))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }
}
