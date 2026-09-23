package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.equipment.symmetryWand.SymmetryWandScreen;
import com.zurrtum.create.client.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.zurrtum.create.client.content.redstone.displayLink.DisplayLinkScreen;
import com.zurrtum.create.client.content.redstone.thresholdSwitch.ThresholdSwitchScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin({SymmetryWandScreen.class, SequencedGearshiftScreen.class, DisplayLinkScreen.class, ThresholdSwitchScreen.class})
public abstract class TitleRenderWindowMixin {
    @ModifyConstant(method = "renderWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", constant = @Constant(intValue = 0xFF592424))
    private int createreiviewer$darkTitle(int original) {
        return DarkGui.text(original, DarkGui.CREAM);
    }
}
