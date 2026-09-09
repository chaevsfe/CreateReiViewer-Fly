package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.AllIcons;
import com.zurrtum.create.client.foundation.gui.render.DeployerRenderState;
import com.zurrtum.create.client.foundation.gui.render.PressRenderState;
import dev.chaevsfe.createreiviewer.client.widget.OneItemRenderer;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiSequenceDisplay;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SequencedAssemblyCategory extends CreateReiCategory<CreateReiSequenceDisplay> {
    private static final String[] ROMANS = {"I", "II", "III", "IV", "V", "VI", "-"};
    private static final int LABEL_COLOR = -7829368;
    private static final float PRESS_SCALE = 0.6333333f;
    private static final float DEPLOYER_SCALE = 0.75641024f;

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

        panel.texture(AllGuiTextures.JEI_LONG_ARROW, 47, 94);
        if (display.loops() > 1) {
            panel.icon(AllIcons.I_SEQ_REPEAT, 60, 99);
            panel.text(Component.translatable("create.recipe.assembly.repeat", display.loops()), 76, 104, LABEL_COLOR);
        }

        List<Identifier> types = display.stepTypes();
        int steps = types.size();
        int startX = 94 - 14 * steps;
        for (int i = 0; i < steps; i++) {
            int x = startX + 28 * i;
            panel.texture(AllGuiTextures.JEI_SLOT, x - 1, 14);
            panel.bareSlot(x, 15, display.stepEntries().get(i));
            String roman = ROMANS[Math.min(i, ROMANS.length - 1)];
            panel.text(Component.literal(roman), x + 8 - Minecraft.getInstance().font.width(roman) / 2, 2, LABEL_COLOR);
            step(panel, types.get(i), i, x);
        }
    }

    private static void step(Panel panel, Identifier type, int index, int x) {
        String path = type.getPath();
        if (path.equals("pressing")) {
            panel.pipScaled(x, 15, PRESS_SCALE, (pose, px, py) -> new PressRenderState(index, pose, px - 3, py + 18, index));
            return;
        }
        if (path.equals("deploying")) {
            panel.pipScaled(x, 15, DEPLOYER_SCALE, (pose, px, py) -> new DeployerRenderState(index, pose, px - 3, py + 18, index));
        }
    }
}
