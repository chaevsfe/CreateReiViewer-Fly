package dev.chaevsfe.createreiviewer.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.render.CachedBuffers;
import com.zurrtum.create.client.foundation.gui.render.GuiBlockRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class PressDepotRenderer extends GuiBlockRenderer<PressDepotRenderState> {
    @Override
    protected float getTranslateY(int height, int guiScale) {
        return PressDepotRenderState.PRESS_ORIGIN * guiScale;
    }

    @Override
    protected void renderToTexture(PressDepotRenderState state, PoseStack matrices, SubmitNodeCollector queue) {
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.14f, -0.5f);
        matrices.scale(1, -1, 1);
        matrices.translate(0, -2.06f, 0);
        CachedBuffers.block(AllBlocks.DEPOT.defaultBlockState()).submit(matrices, queue);
    }

    @Override
    protected String getTextureLabel() {
        return "Press Depot";
    }

    @Override
    public Class<PressDepotRenderState> getRenderStateClass() {
        return PressDepotRenderState.class;
    }
}
