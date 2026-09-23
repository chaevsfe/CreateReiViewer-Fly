package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.schematics.cannon.SchematicannonScreen;
import com.zurrtum.create.client.content.schematics.table.SchematicTableScreen;
import com.zurrtum.create.client.content.trains.schedule.ScheduleScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({SchematicannonScreen.class, SchematicTableScreen.class, ScheduleScreen.class})
public abstract class SchematicExtractBackgroundMixin {
    @ModifyConstant(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF505050))
    private int createreiviewer$darkLabel(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
