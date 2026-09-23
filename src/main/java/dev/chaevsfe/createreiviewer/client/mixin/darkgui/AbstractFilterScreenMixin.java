package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.logistics.filter.AbstractFilterScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractFilterScreen.class)
public abstract class AbstractFilterScreenMixin {
    @ModifyConstant(method = "getTitleColor()I", constant = @Constant(intValue = 0xFF592424))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
