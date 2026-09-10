package dev.chaevsfe.createreiviewer.mixin;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.architectury.fluid.fabric.FluidStackImpl$Pair", remap = false)
public abstract class ArchitecturyFluidPatchMixin {
    @Shadow
    public Fluid fluid;

    @Shadow
    public PatchedDataComponentMap components;

    @Shadow
    public long amount;

    @Inject(method = "getPatch", at = @At("HEAD"), cancellable = true, require = 0)
    private void createreiviewer$keepComponents(CallbackInfoReturnable<DataComponentPatch> info) {
        boolean empty = amount <= 0 || fluid == Fluids.EMPTY;
        info.setReturnValue(empty ? DataComponentPatch.EMPTY : components.asPatch());
    }
}
