package dev.chaevsfe.createreiviewer.client.render;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import org.joml.Matrix3x2f;

public record PressDepotRenderState(Matrix3x2f pose, int x0, int y0, ScreenRectangle bounds) implements PictureInPictureRenderState {
    public static final int TOP = 60;
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    public static final int PRESS_ORIGIN = 64 - TOP;

    public PressDepotRenderState(Matrix3x2f pose, int pressX, int pressY) {
        this(pose, pressX, pressY + TOP, new ScreenRectangle(pressX, pressY + TOP, WIDTH, HEIGHT).transformMaxBounds(pose));
    }

    @Override
    public int x1() {
        return x0 + WIDTH;
    }

    @Override
    public int y1() {
        return y0 + HEIGHT;
    }

    @Override
    public float scale() {
        return 23;
    }

    @Override
    public ScreenRectangle scissorArea() {
        return null;
    }
}
