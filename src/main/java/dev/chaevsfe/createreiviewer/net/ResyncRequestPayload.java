package dev.chaevsfe.createreiviewer.net;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ResyncRequestPayload(int createHeld, int reiHeld) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ResyncRequestPayload> TYPE =
        CustomPacketPayload.createType(CreateReiViewer.MOD_ID + ":resync_request");

    public static final StreamCodec<RegistryFriendlyByteBuf, ResyncRequestPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeVarInt(payload.createHeld);
            buf.writeVarInt(payload.reiHeld);
        },
        buf -> new ResyncRequestPayload(buf.readVarInt(), buf.readVarInt())
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
