package dev.chaevsfe.createreiviewer.client.widget;

import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;

@FunctionalInterface
public interface PipFactory {
    PictureInPictureRenderState create(Matrix3x2f pose, int x, int y);
}
