package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.stockTicker.StockKeeperCategoryScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(StockKeeperCategoryScreen.class)
public abstract class StockKeeperCategoryScreenMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF3D3C48))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }

    @ModifyConstant(method = "renderScheduleEntry(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lorg/joml/Matrix3x2fStack;ILnet/minecraft/world/item/ItemStack;I)I", constant = @Constant(intValue = 0xFF656565))
    private int createreiviewer$darkEntry(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
