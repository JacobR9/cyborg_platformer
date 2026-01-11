import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class PlayerTest {

    private Player player;
    private Image[] idle;
    private Image[] running;
    private Image[] hurt;
    private Image shooting;
    private Image bulletImg;

    @BeforeEach
    void setUp() {
        idle = new Image[] { new WritableImage(48, 48), new WritableImage(48, 48), new WritableImage(48, 48), new WritableImage(48, 48) };
        running = new Image[] {
                new WritableImage(48, 48), new WritableImage(48, 48), new WritableImage(48, 48),
                new WritableImage(48, 48), new WritableImage(48, 48), new WritableImage(48, 48)
        };
        hurt = new Image[] { new WritableImage(48, 48), new WritableImage(48, 48) };
        shooting = new WritableImage(48, 48);
        bulletImg = new WritableImage(10, 10);

        player = new Player(100, 200, 3, idle, running, hurt, shooting);
    }

    @Test
    void testDesiredCameraX() {
        // desiredCameraX = x - (viewWidth - hitBox)/2
        // hitBox from Player constructor super(..., hitBox=30)
        int viewWidth = 1280;

        player.x = 500;
        int expected = 500 - (viewWidth - player.hitBox) / 2;
        assertEquals(expected, player.desiredCameraX(viewWidth));
    }

    @Test
    void testFacingDirection() {
        boolean[] keys = new boolean[4];

        // Pressing right only => true
        keys[0] = false;
        keys[1] = true;
        assertTrue(player.isFacingForwards(keys, false));

        // Pressing left only => false
        keys[0] = true;
        keys[1] = false;
        assertFalse(player.isFacingForwards(keys, true));

        // No input => lastDir
        keys[0] = false;
        keys[1] = false;
        assertTrue(player.isFacingForwards(keys, true));
        assertFalse(player.isFacingForwards(keys, false));

        // Both pressed => lastDir
        keys[0] = true;
        keys[1] = true;
        assertTrue(player.isFacingForwards(keys, true));
        assertFalse(player.isFacingForwards(keys, false));
    }

    @Test
    void testTryShootSuccess() {
        player.ammo = 2;

        Bullet b = player.tryShoot(true, bulletImg);
        assertNotNull(b, "Bullet should spawn when ammo > 0 and not on cooldown");
        assertEquals(1, player.ammo, "Ammo should decrement after shooting");
        assertEquals(10, b.speed, "Facing right should set bullet speed positive");
        assertNotNull(b.startPoint, "Bullet should have a startPoint set");
    }

    @Test
    void testTryShootBlocked() {
        // Blocked by no ammo
        player.ammo = 0;
        assertNull(player.tryShoot(true, bulletImg), "Should not shoot when ammo == 0");

        // Blocked by cooldown (justShot)
        player.ammo = 2;
        Bullet first = player.tryShoot(true, bulletImg);
        assertNotNull(first);

        Bullet second = player.tryShoot(true, bulletImg);
        assertNull(second, "Second shot immediately should be blocked by cooldown (justShot)");
    }

    @Test
    void testPlayerDeath() {
        player.y = 901;
        assertTrue(player.isDead(), "Player should be dead when falling out of bounds (y > 900)");

        player.y = 200;
        player.health = 0;
        assertTrue(player.isDead(), "Player should be dead when health <= 0");
    }

    @Test
    void testPlayerWin() {
        //set player pos to be after x that triggers win (x=8000)
        player.x = 8001;

        boolean[] keys = new boolean[4];
        player.update(keys, true, false, Collections.emptyList());

        assertTrue(player.hasWon(), "Player should win when x > 8000");
    }
}
