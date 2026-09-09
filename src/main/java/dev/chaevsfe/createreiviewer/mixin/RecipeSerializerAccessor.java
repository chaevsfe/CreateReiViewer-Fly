package dev.chaevsfe.createreiviewer.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

@Mixin(RecipeSerializer.class)
public interface RecipeSerializerAccessor {
    @Mutable
    @Accessor("streamCodec")
    void createreiviewer$setStreamCodec(StreamCodec<RegistryFriendlyByteBuf, ? extends Recipe<?>> codec);
}
