package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.equipment.toolbox.ToolboxScreen;
import com.zurrtum.create.client.content.redstone.link.controller.LinkedControllerScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({ToolboxScreen.class, LinkedControllerScreen.class})
public abstract class TitleExtractBackgroundMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF592424))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
