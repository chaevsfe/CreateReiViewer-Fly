package dev.chaevsfe.createreiviewer.client.jei;

import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.client.compat.jei.category.SequencedAssemblyCategory;
import com.zurrtum.create.client.foundation.gui.render.SawRenderState;
import com.zurrtum.create.content.kinetics.saw.CuttingRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;

import java.util.Optional;

final class CuttingStepRenderer extends SequencedAssemblyCategory.SequencedRenderer<CuttingRecipe> {
    private static final float SCALE = 0.57575756f;

    static boolean registerIfMissing() {
        if (SequencedAssemblyCategory.RENDER.containsKey(AllRecipeTypes.CUTTING)) {
            return false;
        }
        SequencedAssemblyCategory.registerRenderer(AllRecipeTypes.CUTTING, new CuttingStepRenderer());
        return true;
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int index, int x, int y, Optional<IRecipeSlotView> slot) {
        Matrix3x2fStack pose = graphics.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(SCALE, SCALE);
        pose.translate(-x, -y);
        graphics.guiRenderState.addPicturesInPictureState(new SawRenderState(new Matrix3x2f(pose), x - 3, y + 90));
        pose.popMatrix();
    }

    @Override
    public Component getSequenceName(CuttingRecipe recipe, Optional<IRecipeSlotView> slot) {
        return Component.translatable("create.recipe.assembly.cutting");
    }

    @Override
    public IRecipeSlotBuilder addSlot(IRecipeLayoutBuilder builder, int x, int y, CuttingRecipe recipe) {
        return null;
    }
}
