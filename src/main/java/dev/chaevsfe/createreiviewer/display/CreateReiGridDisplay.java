package dev.chaevsfe.createreiviewer.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public class CreateReiGridDisplay extends CreateReiDisplay {
    public static final MapCodec<CreateReiGridDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(CreateReiDisplay::inputs),
        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(CreateReiDisplay::outputs),
        Codec.INT.fieldOf("width").forGetter(display -> display.width),
        Codec.INT.fieldOf("height").forGetter(display -> display.height),
        Identifier.CODEC.optionalFieldOf("location").forGetter(CreateReiDisplay::getDisplayLocation)
    ).apply(instance, CreateReiGridDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateReiGridDisplay> STREAM_CODEC = new StreamCodec<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, List<EntryIngredient>> ingredients =
            EntryIngredient.streamCodec().apply(ByteBufCodecs.list());

        @Override
        public CreateReiGridDisplay decode(RegistryFriendlyByteBuf buf) {
            List<EntryIngredient> inputs = ingredients.decode(buf);
            List<EntryIngredient> outputs = ingredients.decode(buf);
            int width = buf.readVarInt();
            int height = buf.readVarInt();
            return new CreateReiGridDisplay(inputs, outputs, width, height, buf.readOptional(Identifier.STREAM_CODEC));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CreateReiGridDisplay display) {
            ingredients.encode(buf, display.inputs());
            ingredients.encode(buf, display.outputs());
            buf.writeVarInt(display.width);
            buf.writeVarInt(display.height);
            buf.writeOptional(display.getDisplayLocation(), Identifier.STREAM_CODEC);
        }
    };

    public static final DisplaySerializer<CreateReiGridDisplay> SERIALIZER = DisplaySerializer.of(MAP_CODEC, STREAM_CODEC);

    private final int width;
    private final int height;

    public CreateReiGridDisplay(
        List<EntryIngredient> inputs,
        List<EntryIngredient> outputs,
        int width,
        int height,
        Optional<Identifier> location
    ) {
        super(
            CreateReiDisplays.identifierOf(CreateReiCategories.MECHANICAL_CRAFTING),
            inputs,
            List.of(),
            outputs,
            List.of(),
            0,
            HEAT_NONE,
            0,
            location
        );
        this.width = Math.max(1, width);
        this.height = Math.max(1, height);
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public static Identifier serializerId() {
        return CreateReiViewer.id("grid_display");
    }
}
