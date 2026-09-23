package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.render.DeployerRenderState;
import com.zurrtum.create.client.foundation.gui.render.PressRenderState;
import com.zurrtum.create.client.foundation.gui.render.SawRenderState;
import com.zurrtum.create.client.foundation.gui.render.SpoutRenderState;
import dev.architectury.fluid.FluidStack;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiSequenceDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SequencedAssemblyCategory extends CreateReiCategory<CreateReiSequenceDisplay> {
    private static final String[] ROMANS = {"I", "II", "III", "IV", "V", "VI", "-"};
    private static final int LABEL_COLOR = -7829368;
    private static final float PRESS_SCALE = 0.6333333f;
    private static final float DEPLOYER_SCALE = 0.75641024f;
    private static final float SPOUT_SCALE = 0.76086956f;
    private static final float SAW_SCALE = 0.57575756f;
    private static final int SPOUT_IDS = 12;

    private final AtomicInteger spoutId = new AtomicInteger();

    public SequencedAssemblyCategory() {
        super("create.recipe.sequenced_assembly");
    }

    @Override
    public CategoryIdentifier<? extends CreateReiSequenceDisplay> getCategoryIdentifier() {
        return CreateReiCategories.SEQUENCED_ASSEMBLY;
    }

    @Override
    public Renderer getIcon() {
        return new OneItemRenderer(AllItems.PRECISION_MECHANISM);
    }

    @Override
    protected int contentHeight() {
        return 115;
    }

    @Override
    protected void build(CreateReiSequenceDisplay display, Panel panel) {
        boolean junk = display.outputs().size() > 1;
        int shift = junk ? -7 : 0;

        panel.slot(22 + shift, 91, display.inputs().get(0));
        panel.output(127 + shift, 91, display.outputs().get(0), display.chance(0));
        if (junk) {
            panel.junk(146 + shift, 91, display.outputs().get(1), display.chance(1));
        }

        panel.texture(AllGuiTextures.JEI_LONG_ARROW, shift + 47, 94);
        if (display.loops() > 1) {
            panel.icon(AllIcons.I_SEQ_REPEAT, shift + 60, 99);
            panel.text(Component.literal("x" + display.loops()), shift + 76, 104, LABEL_COLOR);
            panel.tooltip(43, 92, 65, 24, Component.translatable("create.recipe.assembly.repeat", display.loops()));
        }

        List<Identifier> types = display.stepTypes();
        int steps = types.size();
        int startX = 94 - 14 * steps;
        for (int i = 0; i < steps; i++) {
            int x = startX + 28 * i;
            EntryIngredient entries = display.stepEntries().get(i);
            if (!entries.isEmpty()) {
                panel.texture(AllGuiTextures.JEI_SLOT, x - 1, 14);
                panel.bareSlot(x, 15, entries);
            }
            String roman = ROMANS[Math.min(i, ROMANS.length - 1)];
            panel.text(Component.literal(roman), x + 8 - Minecraft.getInstance().font.width(roman) / 2, 2, LABEL_COLOR);
            step(panel, types.get(i), i, x, entries);
            if (entries.isEmpty()) {
                panel.tooltip(x - 5, 0, 26, 86,
                    Component.translatable("create.recipe.assembly.step", i + 1),
                    stepName(types.get(i)).copy().withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }

    private static Component stepName(Identifier type) {
        return switch (type.getPath()) {
            case "pressing" -> Component.translatable("create.recipe.assembly.pressing");
            case "cutting" -> Component.translatable("create.recipe.assembly.cutting");
            default -> Component.literal(type.toString());
        };
    }

    private void step(Panel panel, Identifier type, int index, int x, EntryIngredient entries) {
        String path = type.getPath();
        if (path.equals("pressing")) {
            panel.pipScaled(x, 15, PRESS_SCALE, (pose, px, py) -> new PressRenderState(index, pose, px - 3, py + 18, index));
            return;
        }
        if (path.equals("deploying")) {
            panel.pipScaled(x, 15, DEPLOYER_SCALE, (pose, px, py) -> new DeployerRenderState(index, pose, px - 3, py + 18, index));
            return;
        }
        if (path.equals("cutting")) {
            panel.pipScaled(x, 15, SAW_SCALE, (pose, px, py) -> new SawRenderState(pose, px - 3, py + 90));
            return;
        }
        if (path.equals("filling")) {
            FluidStack fluid = DrainingCategory.firstFluid(entries);
            if (fluid == null) {
                return;
            }
            int id = nextSpoutId();
            panel.pipScaled(x, 15, SPOUT_SCALE,
                (pose, px, py) -> new SpoutRenderState(id, pose, fluid.getFluid(), fluid.getPatch(), px - 2, py + 24, index));
        }
    }

    private int nextSpoutId() {
        int id = spoutId.getAndIncrement();
        if (id >= SPOUT_IDS) {
            spoutId.set(1);
            return 0;
        }
        return id;
    }
}
