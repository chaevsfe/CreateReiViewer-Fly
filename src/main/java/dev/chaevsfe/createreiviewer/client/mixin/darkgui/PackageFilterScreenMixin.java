package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.filter.PackageFilterScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PackageFilterScreen.class)
public abstract class PackageFilterScreenMixin {
    @ModifyConstant(method = "getTitleColor()I", constant = @Constant(intValue = 0xFF3D3C48))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.PALE_BLUE);
    }
}
