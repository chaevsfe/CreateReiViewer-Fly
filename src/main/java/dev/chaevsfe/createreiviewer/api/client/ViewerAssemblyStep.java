package dev.chaevsfe.createreiviewer.api.client;

/**
 * Draws the machine of one step in Create's Recipe Sequence category, for a recipe type an add-on uses as a sequenced
 * assembly step. Registered with {@link ViewerCategoryRegistry#addAssemblyStep}.
 *
 * <p>{@code index} is the step's position in the sequence, {@code x} the left edge of its column and {@code y} the top
 * of its slot row, in the category's coordinates; Create draws its machines between {@code y + 7} and {@code y + 65}.
 * Only the canvas's texture, icon, text and picture methods draw here; slots and tooltips are ignored.
 */
@FunctionalInterface
public interface ViewerAssemblyStep {
    void draw(ViewerCanvas canvas, int index, int x, int y);
}
