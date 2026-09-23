package dev.chaevsfe.createreiviewer.client.mixin.darkgui;

import com.zurrtum.create.client.content.equipment.zapper.terrainzapper.WorldshaperScreen;

import dev.chaevsfe.createreiviewer.client.darkgui.DarkGui;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldshaperScreen.class)
public abstract class WorldshaperScreenMixin {
    @ModifyConstant(method = "<init>(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)V", constant = @Constant(intValue = 0xFF767676))
    private int createreiviewer$darkFontColor(int original) {
        return DarkGui.text(original, DarkGui.PALE_GREY);
    }
}
