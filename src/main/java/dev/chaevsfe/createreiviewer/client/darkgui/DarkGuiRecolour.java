package dev.chaevsfe.createreiviewer.client.darkgui;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.platform.NativeImage;

public final class DarkGuiRecolour {
    private static final double NEUTRAL_SAT = 0.10;
    private static final double WARM_LO = 340.0;
    private static final double WARM_HI = 75.0;
    private static final double PANEL_SAT = 0.50;
    private static final double DEEP_SAT = 0.75;
    private static final double DEEP_VAL = 0.65;
    private static final double DARK = 0.27;
    private static final double DEEP_DARK = 0.62;
    private static final double TINT_HUE = 30.0 / 360.0;
    private static final double TINT_SAT = 0.20;

    private final Map<Integer, Integer> cache = new HashMap<>();

    public NativeImage recolour(NativeImage image) {
        boolean changed = false;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getPixel(x, y);
                int alpha = argb >>> 24;
                if (alpha == 0) {
                    continue;
                }
                int rgb = argb & 0xFFFFFF;
                int mapped = cache.computeIfAbsent(rgb, DarkGuiRecolour::recolourRgb);
                if (mapped != rgb) {
                    image.setPixel(x, y, alpha << 24 | mapped);
                    changed = true;
                }
            }
        }
        return changed ? image : null;
    }

    public static int recolourRgb(int rgb) {
        double r = (rgb >> 16 & 0xFF) / 255.0;
        double g = (rgb >> 8 & 0xFF) / 255.0;
        double b = (rgb & 0xFF) / 255.0;
        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double v = max;
        double h;
        double s;
        if (min == max) {
            h = 0.0;
            s = 0.0;
        } else {
            double range = max - min;
            s = range / max;
            double rc = (max - r) / range;
            double gc = (max - g) / range;
            double bc = (max - b) / range;
            if (r == max) {
                h = bc - gc;
            } else if (g == max) {
                h = 2.0 + rc - bc;
            } else {
                h = 4.0 + gc - rc;
            }
            h = floorModOne(h / 6.0);
        }
        if (s <= NEUTRAL_SAT) {
            return hsvToRgb(TINT_HUE, TINT_SAT, v * DARK);
        }
        double degrees = h * 360.0;
        if (degrees >= WARM_LO || degrees <= WARM_HI) {
            if (s <= PANEL_SAT) {
                return hsvToRgb(h, s, v * DARK);
            }
            if (s <= DEEP_SAT && v <= DEEP_VAL) {
                return hsvToRgb(h, s, v * DEEP_DARK);
            }
        }
        return rgb;
    }

    private static double floorModOne(double value) {
        double mod = value % 1.0;
        if (mod != 0.0 && mod < 0.0) {
            mod += 1.0;
        }
        return mod == 0.0 ? 0.0 : mod;
    }

    private static int hsvToRgb(double h, double s, double v) {
        double r;
        double g;
        double b;
        if (s == 0.0) {
            r = v;
            g = v;
            b = v;
        } else {
            int i = (int) (h * 6.0);
            double f = h * 6.0 - i;
            double p = v * (1.0 - s);
            double q = v * (1.0 - s * f);
            double t = v * (1.0 - s * (1.0 - f));
            switch (Math.floorMod(i, 6)) {
                case 0 -> { r = v; g = t; b = p; }
                case 1 -> { r = q; g = v; b = p; }
                case 2 -> { r = p; g = v; b = t; }
                case 3 -> { r = p; g = q; b = v; }
                case 4 -> { r = t; g = p; b = v; }
                default -> { r = v; g = p; b = q; }
            }
        }
        return channel(r) << 16 | channel(g) << 8 | channel(b);
    }

    private static int channel(double value) {
        return (int) Math.rint(value * 255.0);
    }
}
