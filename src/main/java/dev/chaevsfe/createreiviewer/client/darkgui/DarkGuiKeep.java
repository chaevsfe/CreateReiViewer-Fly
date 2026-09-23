package dev.chaevsfe.createreiviewer.client.darkgui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record DarkGuiKeep(int rows, List<Rect> rects) {
    private static final DarkGuiKeep NONE = new DarkGuiKeep(Integer.MAX_VALUE, List.of());
    private static final Map<String, Integer> ROWS = Map.of(
        "schedule.png", 226,
        "schedule_2.png", 238
    );

    public record Rect(int x, int y, int width, int height) {
        boolean contains(int px, int py) {
            return px >= x && px < x + width && py >= y && py < y + height;
        }
    }

    public enum Sprite {
        JEI_ARROW("jei/widgets.png", 19, 10, 42, 10),
        JEI_LONG_ARROW("jei/widgets.png", 19, 0, 71, 10),
        JEI_DOWN_ARROW("jei/widgets.png", 0, 21, 18, 14),
        JEI_LIGHT("jei/widgets.png", 0, 42, 52, 11),
        JEI_HEAT_BAR("jei/widgets.png", 0, 201, 169, 19),
        JEI_NO_HEAT_BAR("jei/widgets.png", 0, 221, 169, 19);

        private final String texture;
        private final Rect rect;

        Sprite(String texture, int x, int y, int width, int height) {
            this.texture = texture;
            this.rect = new Rect(x, y, width, height);
        }

        public String texture() {
            return texture;
        }

        public Rect rect() {
            return rect;
        }
    }

    public static DarkGuiKeep forTexture(String relative) {
        List<Rect> rects = new ArrayList<>();
        for (Sprite sprite : Sprite.values()) {
            if (sprite.texture.equals(relative)) {
                rects.add(sprite.rect);
            }
        }
        int rows = ROWS.getOrDefault(relative, Integer.MAX_VALUE);
        if (rows == Integer.MAX_VALUE && rects.isEmpty()) {
            return NONE;
        }
        return new DarkGuiKeep(rows, List.copyOf(rects));
    }

    public boolean recolours(int x, int y) {
        if (y >= rows) {
            return false;
        }
        for (Rect rect : rects) {
            if (rect.contains(x, y)) {
                return false;
            }
        }
        return true;
    }
}
