package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderState;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
import dev.chaevsfe.createreiviewer.client.widget.Panel;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.network.chat.Component;

import java.util.List;

public abstract class BasinCategory extends CreateReiCategory<CreateReiDisplay> {
    protected BasinCategory(String titleKey) {
        super(titleKey);
    }

    @Override
    protected int contentHeight() {
        return 103;
    }

    @Override
    protected int contentOverhangTop() {
        return 5;
    }

    protected static HeatCondition heatOf(CreateReiDisplay display) {
        return HeatCondition.values()[display.heat()];
    }

    protected static void basinInputs(Panel panel, CreateReiDisplay display) {
        List<EntryIngredient> inputs = display.inputs();
        int count = inputs.size();
        for (int i = 0; i < count; i++) {
            panel.slot(CreateReiWidgets.basinInputX(i, count), CreateReiWidgets.basinInputY(i, count), inputs.get(i));
        }
    }

    protected static void basinOutputs(Panel panel, CreateReiDisplay display, int yBase) {
        List<EntryIngredient> outputs = display.outputs();
        int count = outputs.size();
        boolean centreLast = count % 2 != 0;
        for (int i = 0; i < count; i++) {
            panel.output(
                CreateReiWidgets.basinOutputX(i, count - 1, centreLast),
                CreateReiWidgets.basinOutputY(i, yBase),
                outputs.get(i),
                display.chance(i)
            );
        }
    }

    protected static void heatSlots(Panel panel, CreateReiDisplay display) {
        List<EntryIngredient> catalysts = display.catalysts();
        for (int i = 0; i < catalysts.size() && i < 2; i++) {
            panel.bareSlot(i == 0 ? 134 : 153, 81, catalysts.get(i));
        }
    }

    protected static void basinBackground(Panel panel, HeatCondition heat, int outputCount) {
        int arrowY = (outputCount <= 4 ? 32 : 41) - ((outputCount - 1) / 2) * 19;
        panel.texture(AllGuiTextures.JEI_DOWN_ARROW, 136, arrowY);
        if (heat == HeatCondition.NONE) {
            panel.texture(AllGuiTextures.JEI_NO_HEAT_BAR, 4, 80);
            panel.texture(AllGuiTextures.JEI_SHADOW, 81, 68);
            return;
        }
        panel.texture(AllGuiTextures.JEI_HEAT_BAR, 4, 80);
        panel.texture(AllGuiTextures.JEI_LIGHT, 81, 88);
        panel.pip(91, 69, (pose, x, y) -> new BasinBlazeBurnerRenderState(pose, x, y, heat.visualizeAsBlazeBurner()));
        panel.text(Component.translatable(heat.getTranslationKey()), 9, 86, heat.getColor());
    }
}
