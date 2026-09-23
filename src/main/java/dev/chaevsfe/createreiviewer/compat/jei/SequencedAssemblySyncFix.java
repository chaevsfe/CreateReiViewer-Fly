package dev.chaevsfe.createreiviewer.compat.jei;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.mixin.RecipeSerializerAccessor;

import com.zurrtum.create.AllRecipeSerializers;
import com.zurrtum.create.content.processing.recipe.ProcessingOutput;
import com.zurrtum.create.content.processing.sequenced.SequencedAssemblyRecipe;

import io.netty.handler.codec.DecoderException;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Map;

public final class SequencedAssemblySyncFix {
    private static volatile Map<Integer, Identifier> serverIds;

    private static final StreamCodec<RegistryFriendlyByteBuf, Recipe<?>> SUB_RECIPE = new StreamCodec<>() {
        @Override
        public Recipe<?> decode(RegistryFriendlyByteBuf buffer) {
            Map<Integer, Identifier> ids = serverIds;
            if (ids == null) {
                return Recipe.STREAM_CODEC.decode(buffer);
            }
            int rawId = buffer.readVarInt();
            Identifier id = ids.get(rawId);
            if (id == null) {
                throw new DecoderException("The server sent recipe serializer id " + rawId + ", which is not in the serializer list it sent");
            }
            RecipeSerializer<?> serializer = buffer.registryAccess().lookupOrThrow(Registries.RECIPE_SERIALIZER).getValue(id);
            if (serializer == null) {
                throw new DecoderException("The server sent a sequenced assembly step of recipe serializer " + id + ", which this client does not have");
            }
            return serializer.streamCodec().decode(buffer);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, Recipe<?> recipe) {
            Recipe.STREAM_CODEC.encode(buffer, recipe);
        }
    };

    private SequencedAssemblySyncFix() {
    }

    public static void useServerIds(Map<Integer, Identifier> ids) {
        serverIds = ids;
        if (ids == null) {
            return;
        }
        Registry<RecipeSerializer<?>> local = BuiltInRegistries.RECIPE_SERIALIZER;
        int moved = 0;
        int missing = 0;
        for (Map.Entry<Integer, Identifier> entry : ids.entrySet()) {
            if (!local.containsKey(entry.getValue())) {
                missing++;
            } else {
                RecipeSerializer<?> here = local.byId(entry.getKey());
                if (here == null || !entry.getValue().equals(local.getKey(here))) {
                    moved++;
                }
            }
        }
        CreateReiViewer.LOGGER.info(
            "Decoding Create sequenced assembly steps with the server's ids for {} recipe serializers: {} have a different id on this client, {} are missing here",
            ids.size(),
            moved,
            missing
        );
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
            SUB_RECIPE.apply(ByteBufCodecs.list()),
            SequencedAssemblyRecipe::sequence,
            SequencedAssemblyRecipe::new
        );
        ((RecipeSerializerAccessor) (Object) AllRecipeSerializers.SEQUENCED_ASSEMBLY).createreiviewer$setStreamCodec(codec);
    }
}
