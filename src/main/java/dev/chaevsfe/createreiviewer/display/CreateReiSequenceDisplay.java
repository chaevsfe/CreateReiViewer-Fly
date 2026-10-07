package dev.chaevsfe.createreiviewer.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreateReiSequenceDisplay extends CreateReiDisplay {
    public static final MapCodec<CreateReiSequenceDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(CreateReiDisplay::inputs),
        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(CreateReiDisplay::outputs),
        Codec.FLOAT.listOf().fieldOf("chances").forGetter(CreateReiDisplay::chances),
        Identifier.CODEC.listOf().fieldOf("step_types").forGetter(display -> display.stepTypes),
        EntryIngredient.codec().listOf().fieldOf("step_entries").forGetter(display -> display.stepEntries),
        Codec.INT.fieldOf("loops").forGetter(CreateReiDisplay::flags),
        Identifier.CODEC.optionalFieldOf("location").forGetter(CreateReiDisplay::getDisplayLocation),
        ComponentSerialization.CODEC.listOf().lenientOptionalFieldOf("step_names", List.of()).forGetter(display -> display.stepNames)
    ).apply(instance, CreateReiSequenceDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateReiSequenceDisplay> STREAM_CODEC = new StreamCodec<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, List<EntryIngredient>> ingredients =
            EntryIngredient.streamCodec().apply(ByteBufCodecs.list());
        private final StreamCodec<RegistryFriendlyByteBuf, List<Component>> names =
            ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list());

        @Override
        public CreateReiSequenceDisplay decode(RegistryFriendlyByteBuf buf) {
            List<EntryIngredient> inputs = ingredients.decode(buf);
            List<EntryIngredient> outputs = ingredients.decode(buf);
            int chanceCount = buf.readVarInt();
            List<Float> chances = new ArrayList<>(chanceCount);
            for (int i = 0; i < chanceCount; i++) {
                chances.add(buf.readFloat());
            }
            int stepCount = buf.readVarInt();
            List<Identifier> stepTypes = new ArrayList<>(stepCount);
            for (int i = 0; i < stepCount; i++) {
                stepTypes.add(Identifier.STREAM_CODEC.decode(buf));
            }
            List<EntryIngredient> stepEntries = ingredients.decode(buf);
            int loops = buf.readVarInt();
            Optional<Identifier> location = buf.readOptional(Identifier.STREAM_CODEC);
            List<Component> stepNames = List.of();
            if (buf.isReadable()) {
                try {
                    stepNames = names.decode(buf);
                } catch (RuntimeException exception) {
                    CreateReiViewer.LOGGER.debug("Could not read the step names of sequenced assembly display {}", location.orElse(null), exception);
                }
            }
            return new CreateReiSequenceDisplay(
                inputs,
                outputs,
                chances,
                stepTypes,
                stepEntries,
                loops,
                location,
                stepNames
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CreateReiSequenceDisplay display) {
            ingredients.encode(buf, display.inputs());
            ingredients.encode(buf, display.outputs());
            buf.writeVarInt(display.chances().size());
            for (float chance : display.chances()) {
                buf.writeFloat(chance);
            }
            buf.writeVarInt(display.stepTypes.size());
            for (Identifier type : display.stepTypes) {
                Identifier.STREAM_CODEC.encode(buf, type);
            }
            ingredients.encode(buf, display.stepEntries);
            buf.writeVarInt(display.flags());
            buf.writeOptional(display.getDisplayLocation(), Identifier.STREAM_CODEC);
            names.encode(buf, display.stepNames);
        }
    };

    public static final DisplaySerializer<CreateReiSequenceDisplay> SERIALIZER = DisplaySerializer.of(MAP_CODEC, STREAM_CODEC);

    private final List<Identifier> stepTypes;
    private final List<EntryIngredient> stepEntries;
    private final List<Component> stepNames;

    public CreateReiSequenceDisplay(
        List<EntryIngredient> inputs,
        List<EntryIngredient> outputs,
        List<Float> chances,
        List<Identifier> stepTypes,
        List<EntryIngredient> stepEntries,
        int loops,
        Optional<Identifier> location,
        List<Component> stepNames
    ) {
        super(
            CreateReiDisplays.identifierOf(CreateReiCategories.SEQUENCED_ASSEMBLY),
            inputs,
            List.of(),
            outputs,
            chances,
            0,
            HEAT_NONE,
            Math.max(1, loops),
            location
        );
        this.stepTypes = List.copyOf(stepTypes);
        this.stepEntries = List.copyOf(stepEntries);
        this.stepNames = List.copyOf(stepNames);
    }

    @Override
    public DisplaySerializer<? extends Display> getSerializer() {
        return SERIALIZER;
    }

    public List<Identifier> stepTypes() {
        return stepTypes;
    }

    public List<EntryIngredient> stepEntries() {
        return stepEntries;
    }

    public Optional<Component> stepName(int step) {
        if (step < 0 || step >= stepNames.size()) {
            return Optional.empty();
        }
        Component name = stepNames.get(step);
        boolean blank = name.getContents() instanceof PlainTextContents text && text.text().isEmpty() && name.getSiblings().isEmpty();
        return blank ? Optional.empty() : Optional.of(name);
    }

    public int loops() {
        return flags();
    }

    public int steps() {
        return stepTypes.size();
    }

    public static Identifier serializerId() {
        return CreateReiViewer.id("sequence_display");
    }
}
