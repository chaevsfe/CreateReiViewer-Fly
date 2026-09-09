package dev.chaevsfe.createreiviewer.net;

import dev.chaevsfe.createreiviewer.CreateReiViewer;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiGridDisplay;
import dev.chaevsfe.createreiviewer.display.CreateReiSequenceDisplay;
import io.netty.buffer.Unpooled;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class ResyncDisplays {
    public static final int KIND_BASIC = 0;
    public static final int KIND_GRID = 1;
    public static final int KIND_SEQUENCE = 2;

    private ResyncDisplays() {
    }

    public static int kindOf(CreateReiDisplay display) {
        if (display instanceof CreateReiGridDisplay) {
            return KIND_GRID;
        }
        if (display instanceof CreateReiSequenceDisplay) {
            return KIND_SEQUENCE;
        }
        return KIND_BASIC;
    }

    public static byte[] encode(CreateReiDisplay display, int kind, RegistryAccess access) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), access);
        switch (kind) {
            case KIND_GRID -> CreateReiGridDisplay.STREAM_CODEC.encode(buf, (CreateReiGridDisplay) display);
            case KIND_SEQUENCE -> CreateReiSequenceDisplay.STREAM_CODEC.encode(buf, (CreateReiSequenceDisplay) display);
            default -> CreateReiDisplay.STREAM_CODEC.encode(buf, display);
        }
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return bytes;
    }

    public static CreateReiDisplay decode(int kind, byte[] data, RegistryAccess access) {
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), access);
        try {
            return switch (kind) {
                case KIND_GRID -> CreateReiGridDisplay.STREAM_CODEC.decode(buf);
                case KIND_SEQUENCE -> CreateReiSequenceDisplay.STREAM_CODEC.decode(buf);
                case KIND_BASIC -> CreateReiDisplay.STREAM_CODEC.decode(buf);
                default -> null;
            };
        } catch (Throwable throwable) {
            CreateReiViewer.LOGGER.warn("Could not decode a resynced display of kind {} and {} bytes", kind, data.length, throwable);
            return null;
        }
    }

    public static String key(CreateReiDisplay display) {
        int kind = kindOf(display);
        try {
            byte[] bytes = encode(display, kind, BasicDisplay.registryAccess());
            return kind + ":" + HexFormat.of().formatHex(digest().digest(bytes));
        } catch (Throwable throwable) {
            return kind + ":identity:" + System.identityHashCode(display);
        }
    }

    private static MessageDigest digest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
