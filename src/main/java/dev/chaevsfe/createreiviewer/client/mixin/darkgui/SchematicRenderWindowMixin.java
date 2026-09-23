package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.schematics.client.SchematicEditScreen;
import com.zurrtum.create.client.content.schematics.client.SchematicPromptScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({SchematicEditScreen.class, SchematicPromptScreen.class})
public abstract class SchematicRenderWindowMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF505050))
    private int createreiviewer$darkLabel(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
