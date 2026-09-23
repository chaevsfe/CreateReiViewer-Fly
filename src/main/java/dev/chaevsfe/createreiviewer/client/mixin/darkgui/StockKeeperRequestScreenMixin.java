package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.stockTicker.StockKeeperRequestScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StockKeeperRequestScreen.class)
public abstract class StockKeeperRequestScreenMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF252525))
    private int createreiviewer$darkLabel(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }

    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0x252525))
    private int createreiviewer$darkFadingLabel(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY_RGB);
    }

    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF4A2D31))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0x8C5D4B))
    private int createreiviewer$darkBannerText(int original) {
        return DarkGui.text(original, DarkGui.CREAM_RGB);
    }

    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF714A40))
    private int createreiviewer$darkAddress(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "init()V", constant = @Constant(intValue = 0xFF4A2D31))
    private int createreiviewer$darkSearchBox(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }

    @ModifyConstant(method = "init()V", constant = @Constant(intValue = 0xFF714A40))
    private int createreiviewer$darkAddressBox(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
