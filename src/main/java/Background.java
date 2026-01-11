import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Handles rendering of parallax background layers.
 * <p>
 * Each layer scrolls at a different speed based on the camera offset
 * to create a depth effect.
 *
 * @author psyjr14
 */
public class Background {

    /** Array of background layer images. */
    private final Image[] background;

    /**
     * Constructs a Background renderer.
     *
     * @param background array of background images
     */
    public Background(Image[] background) {
        this.background = background;
    }

    /**
     * Draws the background layers using parallax scrolling.
     *
     * @param gc graphics context used for rendering
     * @param cameraOffset current camera X offset
     * @param mapWidth total width of the map in pixels
     */
    public void draw(GraphicsContext gc, int cameraOffset, int mapWidth) {
        for (int mapX = 0; mapX < mapWidth; mapX += 1280) {
            gc.drawImage(background[0], mapX, 0);
            gc.drawImage(background[1], mapX - cameraOffset / 2.0, 0);
            gc.drawImage(background[2], mapX - cameraOffset / 4.0, 0);
            gc.drawImage(background[3], mapX - cameraOffset / 16.0, 0);
        }
    }
}
