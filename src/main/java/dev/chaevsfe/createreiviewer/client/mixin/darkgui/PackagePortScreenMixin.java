package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.packagePort.PackagePortScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PackagePortScreen.class)
public abstract class PackagePortScreenMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF3D3C48))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }

    @ModifyConstant(method = "init()V", constant = @Constant(intValue = 0xFF3D3C48))
    private int createreiviewer$darkAddressBox(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }
}
