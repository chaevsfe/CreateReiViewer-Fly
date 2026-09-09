package dev.chaevsfe.createreiviewer.compat.jei;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.config.ViewerConfig;
import dev.chaevsfe.createreiviewer.mixin.RecipeSerializerAccessor;

import com.zurrtum.create.AllRecipeSerializers;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class SequencedAssemblySyncFix {
    private static final StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> SUB_RECIPE_BY_NAME = new StreamCodec<>() {
        @Override
        public Recipe<?> decode(RegistryFriendlyByteBuf buffer) {
            Identifier id = buffer.readIdentifier();
            RecipeSerializer<?> serializer = BuiltInRegistries.RECIPE_SERIALIZER.getValue(id);
            if (serializer == null) {
                throw new IllegalStateException("Unknown recipe serializer " + id);
            }
            return serializer.streamCodec().decode(buffer);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void encode(RegistryFriendlyByteBuf buffer, Recipe<?> recipe) {
            RecipeSerializer serializer = recipe.getSerializer();
            Identifier id = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
            if (id == null) {
                throw new IllegalStateException("Unregistered recipe serializer for " + recipe);
            }
            buffer.writeIdentifier(id);
            ((StreamCodec) serializer.streamCodec()).encode(buffer, recipe);
        }
    };

    private SequencedAssemblySyncFix() {
    }

    public static void apply() {
        StreamCodec<RegistryFriendlyByteBuf, SequencedAssemblyRecipe> codec = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            SequencedAssemblyRecipe::ingredient,
            ItemStackTemplate.STREAM_CODEC,
            SequencedAssemblyRecipe::transitionalItem,
            ProcessingOutput.STREAM_CODEC,
            SequencedAssemblyRecipe::result,
            ProcessingOutput.STREAM_CODEC.apply(ByteBufCodecs.list()),
            SequencedAssemblyRecipe::junks,
            ByteBufCodecs.INT,
            SequencedAssemblyRecipe::loops,
            SUB_RECIPE_BY_NAME.apply(ByteBufCodecs.list()),
            SequencedAssemblyRecipe::sequence,
            SequencedAssemblyRecipe::new
        );
        ((RecipeSerializerAccessor) (Object) AllRecipeSerializers.SEQUENCED_ASSEMBLY).createreiviewer$setStreamCodec(codec);
        CreateReiViewer.LOGGER.info(
            "Sequenced assembly sync fix active: nested sub recipes go on the wire by name, so this side no longer depends on recipe serializer raw ids. The server and every client must have {} set to true.",
            ViewerConfig.FIX_SEQUENCED_ASSEMBLY_SYNC
        );
    }
}
