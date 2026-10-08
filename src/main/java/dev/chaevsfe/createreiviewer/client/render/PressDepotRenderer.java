package dev.chaevsfe.createreiviewer.client.render;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.client.catnip.gui.render.BlockBakedQuadOutput;
import com.zurrtum.create.client.flywheel.lib.model.baked.ModelRenderHelper;
import com.zurrtum.create.client.flywheel.lib.model.baked.SinglePosVirtualBlockGetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PressDepotRenderer extends PictureInPictureRenderer<PressDepotRenderState> {
    private final BlockBakedQuadOutput output;

    public PressDepotRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
        output = new BlockBakedQuadOutput(bufferSource);
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return PressDepotRenderState.PRESS_ORIGIN * guiScale;
    }

    @Override
    protected void renderToTexture(PressDepotRenderState state, PoseStack matrices) {
        Minecraft mc = Minecraft.getInstance();
        mc.gameRenderer.getLighting().setupFor(Lighting.Entry.ENTITY_IN_UI);
        matrices.scale(1, 1, -1);
        matrices.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrices.mulPose(Axis.YP.rotationDegrees(22.5f));
        matrices.translate(-0.5f, -1.14f, -0.5f);
        matrices.scale(1, -1, 1);
        matrices.translate(0, -1.8125f, 0);
        output.setPoseStack(matrices);
        BlockState depot = AllBlocks.DEPOT.defaultBlockState();
        SinglePosVirtualBlockGetter world = SinglePosVirtualBlockGetter.createFullBright().blockState(depot);
        BlockStateModel model = mc.getModelManager().getBlockStateModelSet().get(depot);
        output.updateBuffer(model);
        ModelRenderHelper.getHelper(output).tesselateBlock(0, 0, 0, world, BlockPos.ZERO, depot, model, 42L);
        output.clear();
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
