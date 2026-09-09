package dev.chaevsfe.createreiviewer.display;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.chaevsfe.createreiviewer.CreateReiViewer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CreateReiDisplay extends BasicDisplay {
    public static final int HEAT_NONE = 0;
    public static final int HEAT_HEATED = 1;
    public static final int HEAT_SUPERHEATED = 2;

    public static final MapCodec<CreateReiDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Identifier.CODEC.fieldOf("category").forGetter(display -> display.category),
        EntryIngredient.codec().listOf().fieldOf("inputs").forGetter(display -> display.recipeInputs),
        EntryIngredient.codec().listOf().fieldOf("catalysts").forGetter(display -> display.catalysts),
        EntryIngredient.codec().listOf().fieldOf("outputs").forGetter(display -> display.recipeOutputs),
        com.mojang.serialization.Codec.FLOAT.listOf().fieldOf("chances").forGetter(display -> display.chances),
        com.mojang.serialization.Codec.INT.fieldOf("duration").forGetter(display -> display.duration),
        com.mojang.serialization.Codec.INT.fieldOf("heat").forGetter(display -> display.heat),
        com.mojang.serialization.Codec.INT.fieldOf("flags").forGetter(display -> display.flags),
        Identifier.CODEC.optionalFieldOf("location").forGetter(BasicDisplay::getDisplayLocation)
    ).apply(instance, CreateReiDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CreateReiDisplay> STREAM_CODEC = new StreamCodec<>() {
        private final StreamCodec<RegistryFriendlyByteBuf, List<EntryIngredient>> ingredients =
            EntryIngredient.streamCodec().apply(ByteBufCodecs.list());

        @Override
        public CreateReiDisplay decode(RegistryFriendlyByteBuf buf) {
            Identifier category = Identifier.STREAM_CODEC.decode(buf);
            List<EntryIngredient> inputs = ingredients.decode(buf);
            List<EntryIngredient> catalysts = ingredients.decode(buf);
            List<EntryIngredient> outputs = ingredients.decode(buf);
            int chanceCount = buf.readVarInt();
            List<Float> chances = new ArrayList<>(chanceCount);
            for (int i = 0; i < chanceCount; i++) {
                chances.add(buf.readFloat());
            }
            int duration = buf.readVarInt();
            int heat = buf.readVarInt();
            int flags = buf.readVarInt();
            Optional<Identifier> location = buf.readOptional(Identifier.STREAM_CODEC);
            return new CreateReiDisplay(category, inputs, catalysts, outputs, chances, duration, heat, flags, location);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, CreateReiDisplay display) {
            Identifier.STREAM_CODEC.encode(buf, display.category);
            ingredients.encode(buf, display.recipeInputs);
            ingredients.encode(buf, display.catalysts);
            ingredients.encode(buf, display.recipeOutputs);
            buf.writeVarInt(display.chances.size());
            for (float chance : display.chances) {
                buf.writeFloat(chance);
            }
            buf.writeVarInt(display.duration);
            buf.writeVarInt(display.heat);
            buf.writeVarInt(display.flags);
            buf.writeOptional(display.getDisplayLocation(), Identifier.STREAM_CODEC);
        }
    };

    public static final DisplaySerializer<CreateReiDisplay> SERIALIZER = DisplaySerializer.of(MAP_CODEC, STREAM_CODEC);

    private final Identifier category;
    private final List<EntryIngredient> recipeInputs;
    private final List<EntryIngredient> catalysts;
    private final List<EntryIngredient> recipeOutputs;
    private final List<Float> chances;
    private final int duration;
    private final int heat;
    private final int flags;

    public CreateReiDisplay(
        Identifier category,
        List<EntryIngredient> inputs,
        List<EntryIngredient> catalysts,
        List<EntryIngredient> outputs,
        List<Float> chances,
        int duration,
        int heat,
        int flags,
        Optional<Identifier> location
    ) {
        super(join(inputs, catalysts), List.copyOf(outputs), location);
        this.category = category;
        this.recipeInputs = List.copyOf(inputs);
        this.catalysts = List.copyOf(catalysts);
        this.recipeOutputs = List.copyOf(outputs);
        this.chances = padChances(chances, outputs.size());
        this.duration = Math.max(0, duration);
        this.heat = Math.clamp(heat, HEAT_NONE, HEAT_SUPERHEATED);
        this.flags = flags;
    }

    private static List<EntryIngredient> join(List<EntryIngredient> inputs, List<EntryIngredient> catalysts) {
        if (catalysts.isEmpty()) {
            return List.copyOf(inputs);
        }
        List<EntryIngredient> all = new ArrayList<>(inputs.size() + catalysts.size());
        all.addAll(inputs);
        all.addAll(catalysts);
        return Collections.unmodifiableList(all);
    }

    private static List<Float> padChances(List<Float> chances, int outputCount) {
        if (chances.size() == outputCount) {
            return List.copyOf(chances);
        }
        List<Float> padded = new ArrayList<>(outputCount);
        for (int i = 0; i < outputCount; i++) {
            padded.add(i < chances.size() ? chances.get(i) : 1.0f);
        }
        return Collections.unmodifiableList(padded);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(category);
    }

    @Override
    public DisplaySerializer<? extends me.shedaniel.rei.api.common.display.Display> getSerializer() {
        return SERIALIZER;
    }

    public Identifier category() {
        return category;
    }

    public List<EntryIngredient> inputs() {
        return recipeInputs;
    }

    public List<EntryIngredient> catalysts() {
        return catalysts;
    }

    public List<EntryIngredient> outputs() {
        return recipeOutputs;
    }

    public List<Float> chances() {
        return chances;
    }

    public float chance(int index) {
        return index >= 0 && index < chances.size() ? chances.get(index) : 1.0f;
    }

    public int duration() {
        return duration;
    }

    public int heat() {
        return heat;
    }

    public int flags() {
        return flags;
    }

    public static Identifier serializerId() {
        return CreateReiViewer.id("display");
    }
}
