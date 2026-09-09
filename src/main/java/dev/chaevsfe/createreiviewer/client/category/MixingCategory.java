package dev.chaevsfe.createreiviewer.client.category;

import com.zurrtum.create.AllItems;
import com.zurrtum.create.client.foundation.gui.AllGuiTextures;
import com.zurrtum.create.client.foundation.gui.render.BasinBlazeBurnerRenderState;
import com.zurrtum.create.client.foundation.gui.render.MixingBasinRenderState;
import com.zurrtum.create.content.processing.recipe.HeatCondition;
import dev.chaevsfe.createreiviewer.client.widget.CreateReiWidgets;
import dev.chaevsfe.createreiviewer.client.widget.TwoItemRenderer;
import dev.chaevsfe.createreiviewer.display.CreateReiCategories;
import dev.chaevsfe.createreiviewer.display.CreateReiDisplay;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class MixingCategory extends CreateReiCategory {
    private static final int HEIGHT = 103;
    private static final int OVERHANG_TOP = 5;

    @Override
    public CategoryIdentifier<? extends CreateReiDisplay> getCategoryIdentifier() {
        return CreateReiCategories.MIXING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("create.recipe.mixing");
    }

    @Override
    public Renderer getIcon() {
        return new TwoItemRenderer(AllItems.MECHANICAL_MIXER, AllItems.BASIN);
    }

    @Override
    protected int contentHeight() {
        return HEIGHT;
    }

    @Override
    protected int contentOverhangTop() {
        return OVERHANG_TOP;
    }

    @Override
    public List<Widget> setupDisplay(CreateReiDisplay display, Rectangle bounds) {
        int ox = originX(bounds);
        int oy = originY(bounds);

        HeatCondition heat = HeatCondition.values()[display.heat()];
        List<EntryIngredient> inputs = display.inputs();
        List<EntryIngredient> outputs = display.outputs();
        List<EntryIngredient> catalysts = display.catalysts();
        int inputCount = inputs.size();
        int outputCount = outputs.size();
        boolean centreLast = outputCount % 2 != 0;
        int lastOutput = outputCount - 1;

        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int arrowY = (outputCount <= 4 ? 32 : 41) - ((outputCount - 1) / 2) * 19;
        widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_DOWN_ARROW, ox + 136, oy + arrowY));

        if (heat == HeatCondition.NONE) {
            widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_NO_HEAT_BAR, ox + 4, oy + 80));
            widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_SHADOW, ox + 81, oy + 68));
        } else {
            widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_HEAT_BAR, ox + 4, oy + 80));
            widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_LIGHT, ox + 81, oy + 88));
            int burnerX = ox + 91;
            int burnerY = oy + 69;
            widgets.add(CreateReiWidgets.pictureInPicture(
                pose -> new BasinBlazeBurnerRenderState(pose, burnerX, burnerY, heat.visualizeAsBlazeBurner())
            ));
        }

        int mixerX = ox + 91;
        int mixerY = oy - 5;
        widgets.add(CreateReiWidgets.pictureInPicture(pose -> new MixingBasinRenderState(pose, mixerX, mixerY)));

        for (int i = 0; i < inputCount; i++) {
            int x = ox + CreateReiWidgets.inputX(i, inputCount);
            int y = oy + CreateReiWidgets.inputY(i, inputCount);
            widgets.add(CreateReiWidgets.texture(AllGuiTextures.JEI_SLOT, x - 1, y - 1));
            widgets.add(CreateReiWidgets.inputSlot(x, y, inputs.get(i)));
        }

        for (int i = 0; i < outputCount; i++) {
            float chance = display.chance(i);
            int x = ox + CreateReiWidgets.outputX(i, lastOutput, centreLast);
            int y = oy + CreateReiWidgets.outputY(i, outputCount);
            widgets.add(CreateReiWidgets.texture(CreateReiWidgets.slotBackground(chance), x - 1, y - 1));
            widgets.add(CreateReiWidgets.outputSlot(x, y, outputs.get(i), chance));
        }

        for (int i = 0; i < catalysts.size() && i < 2; i++) {
            int x = ox + (i == 0 ? 134 : 153);
            widgets.add(CreateReiWidgets.inputSlot(x, oy + 81, catalysts.get(i)));
        }

        Component heatLabel = Component.translatable(heat.getTranslationKey());
        int heatColor = heat.getColor();
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) ->
            graphics.text(Minecraft.getInstance().font, heatLabel, ox + 9, oy + 86, heatColor, false)));

        return widgets;
    }
}
