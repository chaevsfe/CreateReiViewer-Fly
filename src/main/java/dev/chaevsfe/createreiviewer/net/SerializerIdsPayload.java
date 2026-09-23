package dev.chaevsfe.createreiviewer.net;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

public record SerializerIdsPayload(Map<Integer, Identifier> ids) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SerializerIdsPayload> TYPE =
        new CustomPacketPayload.Type<>(CreateReiViewer.id("recipe_serializer_ids"));

    public static final StreamCodec<FriendlyByteBuf, SerializerIdsPayload> STREAM_CODEC =
        StreamCodec.of(SerializerIdsPayload::write, SerializerIdsPayload::read);

    public static SerializerIdsPayload of(RegistryAccess access) {
        Registry<RecipeSerializer<?>> registry = access.lookupOrThrow(Registries.RECIPE_SERIALIZER);
        Map<Integer, Identifier> ids = new LinkedHashMap<>();
        for (RecipeSerializer<?> serializer : registry) {
            ids.put(registry.getId(serializer), registry.getKey(serializer));
        }
        return new SerializerIdsPayload(ids);
    }

    private static void write(FriendlyByteBuf buffer, SerializerIdsPayload payload) {
        buffer.writeVarInt(payload.ids.size());
        payload.ids.forEach((rawId, id) -> {
            buffer.writeVarInt(rawId);
            buffer.writeIdentifier(id);
        });
    }

    private static SerializerIdsPayload read(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        Map<Integer, Identifier> ids = new LinkedHashMap<>();
        for (int i = 0; i < size; i++) {
            int rawId = buffer.readVarInt();
            ids.put(rawId, buffer.readIdentifier());
        }
        return new SerializerIdsPayload(Map.copyOf(ids));
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
