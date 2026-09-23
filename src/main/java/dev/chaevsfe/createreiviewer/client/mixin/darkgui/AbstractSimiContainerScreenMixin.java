package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.foundation.gui.menu.AbstractSimiContainerScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractSimiContainerScreen.class)
public abstract class AbstractSimiContainerScreenMixin {
    @ModifyConstant(method = "renderPlayerInventory(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", constant = @Constant(intValue = 0xFF404040))
    private int createreiviewer$darkInventoryLabel(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
