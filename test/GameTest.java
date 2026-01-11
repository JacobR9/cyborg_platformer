import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();

        // Inject dummy assets so spawnEntities() works without loadImages().
        Image img48 = new WritableImage(48, 48);
        Image bulletImg = new WritableImage(10, 10);

        Image[] playerIdle = new Image[] { img48, img48, img48, img48 };
        Image[] playerRun  = new Image[] { img48, img48, img48, img48, img48, img48 };
        Image[] playerHurt = new Image[] { img48, img48 };

        Image[] enemyIdle = new Image[] { img48, img48, img48, img48, img48, img48, img48, img48 };
        Image[] enemyWalk = new Image[] { img48, img48, img48, img48, img48, img48, img48, img48 };
        Image[] enemyRun  = new Image[] { img48, img48, img48, img48, img48, img48, img48 };

        setField(game, "playerIdleSprites", playerIdle);
        setField(game, "playerRunningSprites", playerRun);
        setField(game, "playerHurtSprites", playerHurt);
        setField(game, "playerShootingSprite", img48);
        setField(game, "bulletImage", bulletImg);

        setField(game, "enemyIdleSprites", enemyIdle);
        setField(game, "enemyWalkingSprites", enemyWalk);
        setField(game, "enemyRunningSprites", enemyRun);
        setField(game, "enemyHurtSprite", img48);

        // Inject a minimal map with known width and empty blocks.
        MapBlocks map = new MapBlocks();
        setField(map, "mapWidth", 9000);
        // blocks list is already initialised empty in MapBlocks
        setField(game, "map", map);

        // Now we can spawn baseline entities
        game.spawnEntities();
    }

    @Test
    void testRestartRespawns() {
        Player before = game.getPlayer();

        // Trigger restart
        game.getInputHandler().onKeyPressed(KeyCode.R);
        game.update();

        Player after = game.getPlayer();
        assertNotSame(before, after, "Restart should respawn a new Player instance");

        // Separate assertion: spawnEntities clears bullets
        game.getActiveBullets().add(mock(Bullet.class));
        game.spawnEntities();
        assertEquals(0, game.getActiveBullets().size(), "spawnEntities should clear bullets");
    }


    @Test
    void testWinStopsUpdate() {
        // Force win this tick: player.x > 8000, then update() sets game.isWon true and returns early.
        game.getPlayer().x = 8001;
        game.update();
        assertTrue(game.isWon(), "Game should set isWon when player hasWon()");
    }

    @Test
    void testDeathRespawn() {
        int deathsBefore = game.getDeathCounter();
        Player before = game.getPlayer();

        // Force death by health <= 0
        game.getPlayer().health = 0;
        game.update();

        assertEquals(deathsBefore + 1, game.getDeathCounter(), "Death should increment deathCounter");
        assertNotSame(before, game.getPlayer(), "Death should respawn a new Player instance");
    }

    @Test
    void testCameraClamping() {
        MapBlocks map = game.getMap();
        int viewWidth = 1280;
        int max = map.getMapWidth() - viewWidth; // 9000 - 1280 = 7720

        // Near left edge => clamp to 0
        game.getPlayer().x = 0;
        game.updateCamera(viewWidth);
        assertEquals(0, game.getCameraOffset(), "Camera should clamp to 0 at left edge");

        // Far right => clamp to max
        game.getPlayer().x = 9000;
        game.updateCamera(viewWidth);
        assertEquals(max, game.getCameraOffset(), "Camera should clamp to mapWidth - viewWidth at right edge");
    }

    // ---- reflection helper ----
    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (NoSuchFieldException e) {
            // Try superclass (useful for MapBlocks fields if needed)
            try {
                Field f = target.getClass().getSuperclass().getDeclaredField(fieldName);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to set field: " + fieldName, ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
