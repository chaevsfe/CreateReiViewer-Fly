package dev.chaevsfe.createreiviewer.mixin;

import dev.chaevsfe.createreiviewer.compat.OldTrainData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.storage.SavedDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(SavedDataStorage.class)
public class OldTrainDataMixin {
    @Inject(method = "readTagFromDisk(Ljava/nio/file/Path;Lnet/minecraft/util/datafix/DataFixTypes;I)Lnet/minecraft/nbt/CompoundTag;", at = @At("RETURN"))
    private void createreiviewer$fixOldTrainData(Path path, DataFixTypes type, int version, CallbackInfoReturnable<CompoundTag> info) {
        OldTrainData.fix(path, info.getReturnValue(), version);
    }
}
