import javafx.scene.image.Image;

/**
 * Represents a single solid tile in the game map.
 * <p>
 * Each MapBlock has a fixed position and image and is used for
 * rendering terrain and handling collision detection.
 *
 * @author psyjr14
 */
public class MapBlock {

    /** X-coordinate of the block in pixels. */
    public final int x;

    /** Y-coordinate of the block in pixels. */
    public final int y;

    /** Image used to render this block. */
    public final Image image;

    /**
     * Constructs a map block at the given position.
     *
     * @param image image used to draw the block
     * @param x x-coordinate in pixels
     * @param y y-coordinate in pixels
     */
    public MapBlock(Image image, int x, int y) {
        this.image = image;
        this.x = x;
        this.y = y;
    }
}
