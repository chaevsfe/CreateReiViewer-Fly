package dev.chaevsfe.createreiviewer.net;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record ResyncDisplaysPayload(int serverTotal, boolean last, List<ResyncDisplaysPayload.Entry> entries) implements CustomPacketPayload {
    public record Entry(int kind, byte[] data) {
    }

    public static final CustomPacketPayload.Type<ResyncDisplaysPayload> TYPE =
        new CustomPacketPayload.Type<>(CreateReiViewer.id("resync_displays"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResyncDisplaysPayload> STREAM_CODEC = StreamCodec.of(
        ResyncDisplaysPayload::write,
        ResyncDisplaysPayload::read
    );

    private static void write(RegistryFriendlyByteBuf buf, ResyncDisplaysPayload payload) {
        buf.writeVarInt(payload.serverTotal);
        buf.writeBoolean(payload.last);
        buf.writeVarInt(payload.entries.size());
        for (Entry entry : payload.entries) {
            buf.writeVarInt(entry.kind());
            buf.writeByteArray(entry.data());
        }
    }

    private static ResyncDisplaysPayload read(RegistryFriendlyByteBuf buf) {
        int serverTotal = buf.readVarInt();
        boolean last = buf.readBoolean();
        int count = buf.readVarInt();
        List<Entry> entries = new ArrayList<>(Math.min(count, 4096));
        for (int i = 0; i < count; i++) {
            entries.add(new Entry(buf.readVarInt(), buf.readByteArray()));
        }
        return new ResyncDisplaysPayload(serverTotal, last, entries);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
