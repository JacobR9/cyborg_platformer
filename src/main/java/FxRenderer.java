import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.time.Duration;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * JavaFX renderer for the game.
 *
 * <p>This class is the "View" layer in the MVC refactor. It contains only drawing
 * code and does not modify the game state. Rendering is performed using a
 * {@link GraphicsContext} obtained from a JavaFX {@link javafx.scene.canvas.Canvas}.</p>
 *
 * <p>World-space objects (map, player, enemies, bullets) are drawn using the
 * camera offset so that they scroll correctly. Screen-space UI (HUD text/icons)
 * is drawn without camera translation.</p>
 *
 * @author psyjr14
 */
public class FxRenderer {

    /**
     * Renders one frame of the game.
     *
     * <p>Clears the canvas and draws either the running-game view (background, map,
     * HUD, entities) or the end screen if the game has been won.</p>
     *
     * @param gc   graphics context used for drawing
     * @param game current game model containing all state to render
     * @param w    viewport width in pixels
     * @param h    viewport height in pixels
     */
    public static void draw(GraphicsContext gc, Game game, GameSettings settings, int w, int h) {
        double cw = gc.getCanvas().getWidth();
        double ch = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, cw, ch);
        int camOff = game.getCameraOffset();

        drawBackground(gc, game, camOff);
        drawMap(gc, game, camOff);
        drawGUI(gc, game, settings, camOff);
        drawPlayer(gc, game, camOff);
        drawEnemies(gc, game, camOff);
        drawBullets(gc, game, camOff);
    }

    /**
     * Draws the tile map.
     *
     * <p>The map is stored in world coordinates. Each block's x-position is
     * offset by the camera so it appears to scroll as the player moves.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing access to the map
     * @param x camera offset in pixels
     */
    public static void drawMap(GraphicsContext gc, Game game, int x) {
        MapBlocks map = game.getMap();
        for (MapBlock b : map.getBlocks()){
            gc.drawImage(b.image, b.x - x, b.y);
        }
    }

    /**
     * Draws the parallax background layers.
     *
     * <p>Delegates to {@link Background#draw(GraphicsContext, int, int)}
     * to render each background layer using the camera offset for parallax.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing background and map width
     * @param x camera offset in pixels
     */
    public static void drawBackground(GraphicsContext gc, Game game, int x){
        game.getBackground().draw(gc, x, game.getMap().getMapWidth());
    }

    /**
     * Draws the heads-up display (HUD).
     *
     * <p>Renders health, ammo, timer, control hints, and attempt count. HUD elements
     * should typically be drawn in screen coordinates (not affected by camera
     * scrolling). This method uses a transform reset while drawing the control
     * hints to avoid inheriting any previous transforms.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing player stats and HUD images
     * @param x camera offset in pixels (used where world-aligned text is desired)
     */
    public static void drawGUI(GraphicsContext gc, Game game, GameSettings settings, int x) {
        Player p = game.getPlayer();
        int health = p.health;
        int ammo = p.ammo;
        int deathCounter = game.getDeathCounter();
        double s = settings.getTextScale();

        Image heart = game.getHeart();
        Image ammoBox = game.getAmmoBox();

        gc.setFill(Color.BLACK);
        gc.setFont(Font.font(game.getFont().getFamily(), 60 * s));

        if (health > 0) gc.drawImage(heart, 20, 20);
        if (health > 1) gc.drawImage(heart, 89, 20);
        if (health > 2) gc.drawImage(heart, 158, 20);

        gc.drawImage(ammoBox, 1040, 20);
        gc.fillText(String.valueOf(ammo), 1130, 68);

        long gameTimer = (long) (System.currentTimeMillis() - Game.startTime);
        Duration duration = Duration.ofMillis(gameTimer);
        long minutes = duration.toMinutes();
        duration = duration.minusMinutes(minutes);
        long seconds = duration.getSeconds();
        String timer = String.format("%02d:%02d", minutes, seconds);
        gc.fillText(timer, 510, 68);

        gc.setFont(Font.font(game.getFont().getFamily(),20 * s));
        String[] controls = {
                "Use WASD to Move",
                "You can Double Jump",
                "Use SPACE to Shoot",
                "Press ESCAPE to Pause"
        };
        int y = 250;
        gc.save();
        gc.setTransform(1,0,0,1,0,0);
        for (String c : controls) {
            gc.fillText(c, 470 - x, y);
            y += 30;
        }
        gc.restore();

        gc.fillText("This is The Winner Tunnel", 6800 - x, 310);
        gc.fillText("Just Keep Walking!", 6800 - x, 340);
        gc.fillText("ATTEMPTS: " + deathCounter, 20, 110);
    }

    /**
     * Draws the player sprite and jump cloud effect.
     *
     * <p>The player is drawn in world coordinates with the camera offset applied.
     * If the player is facing left, the sprite is flipped horizontally.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing player state and sprites
     * @param camX camera offset in pixels
     */
    public static void drawPlayer(GraphicsContext gc, Game game, int camX){
        Player p = game.getPlayer();
        boolean facingForwards = p.isFacingForwards(game.getKeysPressed(), game.isLastDirectionForwards());

        double px = p.x - camX;
        double py = p.y;

        if (facingForwards) gc.drawImage(p.image, px, py);
        else gc.drawImage(p.image, px + 30, py, -p.image.getWidth(), p.image.getHeight());

        if (p.jumpCounter == 2 && p.velocity < 0)
            gc.drawImage(game.getCloud(), p.jumpX - camX, p.jumpY + 42);
    }

    /**
     * Draws all enemies currently active in the game.
     *
     * <p>Enemies are drawn in world coordinates with camera offset applied.
     * If an enemy is facing left, the sprite is flipped horizontally.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing enemy list
     * @param x camera offset in pixels
     */
    public static void drawEnemies(GraphicsContext gc, Game game, int x){
        for (Enemy e : game.getEnemies()) {
            Image img = e.image;
            double ex = e.x - x;
            double y = e.y;

            if (e.isFacingForwards) {
                gc.drawImage(img, ex, y);
            } else {
                gc.drawImage(img, ex + 30, y, -img.getWidth(), img.getHeight());
            }
        }
    }

    /**
     * Draws all bullets currently active in the game.
     *
     * <p>Bullets are drawn in world coordinates with camera offset applied.
     * If a bullet is travelling left, the sprite is flipped horizontally.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing bullet list
     * @param x camera offset in pixels
     */
    public static void drawBullets(GraphicsContext gc, Game game, int x) {
        for (Bullet b : game.getActiveBullets()) {
            Image img = b.image;
            double ex = b.x - x;
            double y = b.y;

            if (b.speed > 0) {
                gc.drawImage(img, ex, y);
            } else {
                gc.drawImage(img, ex + img.getWidth(), y, -img.getWidth(), img.getHeight());
            }
        }
    }

    /**
     * Draws the end screen shown after the player wins.
     *
     * <p>Displays a win message and summary stats, along with restart/exit hints.</p>
     *
     * @param gc graphics context used for drawing
     * @param game game model providing attempt count
     */
    public static void drawEnd(GraphicsContext gc, Game game, GameSettings settings) {
        gc.setFill(Color.BLACK);
        double s = settings.getTextScale();
        gc.setFont(Font.font(game.getFont().getFamily(), 20 * s));

        String[] ends = {
                "You Won!",
                "It Took You",
                game.getDeathCounter() + " Attempts",
                "Press ESC to Restart,",
                "Or ENTER to exit",
        };

        gc.fillText(ends[0], 460, 220);
        gc.fillText(ends[1], 375, 300);
        gc.fillText(ends[2], 405, 380);
        gc.fillText(ends[3], 150, 460);
        gc.fillText(ends[4], 270, 540);
    }

}
