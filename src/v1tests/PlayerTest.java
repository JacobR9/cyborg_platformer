import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    //T5
    @Test
    @DisplayName("Checks that a new Player object initialises correctly.")
    void testPlayerCreates() {
        //pre reqs
        BufferedImage bfr = new  BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{bfr};
        Player player = new Player(0, 0, 10);
        assertNotNull(player, "Player object should not be null");
        assertEquals(10, player.ammo, "Player ammo should be 10");
        assertEquals(30, player.hitBox, "Player hitBox should be 30");
    }

    //T6
    @Test
    @DisplayName("Simulates pressing “D” and checks that Player moves right on update().")
    void testPlayerUpdates() {
        //stub pre reqs
        BufferedImage bfr = new  BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{bfr};
        Bullet.bulletImage = bfr;
        //initial clear map
        MapBlocks.map.clear();
        Arrays.fill(Canvas.getKeysPressed(), false);
        Player player = new Player(0, 0, 10);

        int startX = player.x;
        Canvas.activeBullets.clear();
        //move right
        Canvas.getKeysPressed()[1] = true;
        //first update sets speed > 0, second update actually moves the player
        player.update();
        player.update();
        Canvas.getKeysPressed()[1] = false;

        //check movement and that ammo hasn't changed
        assertTrue(player.x > startX, "Player should have moved to the right when D is pressed");
        assertEquals(10, player.ammo, "Player ammo should remain unchanged by movement");

    }

    //T7
    @Test
    @DisplayName("checks that isMoving flag reflects current movement state.")
    void testPlayerIsMoving() {
        BufferedImage bfr = new  BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{bfr};
        Player player = new Player(0, 0, 10);
        //initial clear map
        MapBlocks.map.clear();
        //move right
        Canvas.getKeysPressed()[1] = true;
        //first update sets speed > 0, second update actually moves the player
        player.update();
        player.update();
        assertTrue(player.isMoving(), "flag for player moving should be true");
        Canvas.getKeysPressed()[1] = false;
        player.update();
        player.update();
        assertFalse(player.isMoving(), "flag for player moving should be false");
    }

    // T8
    @Test
    @DisplayName("Checks that pressing shoot key creates a bullet and reduces ammo.")
    void testPlayerShooting() {
        //pre reqs again to avoid using game.loadImages
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{ bfr };
        Player.shootingSprite = bfr;
        Arrays.fill(Canvas.getKeysPressed(), false);
        Canvas.activeBullets.clear();
        MapBlocks.map.clear();

        Player player = new Player(100, 100, 3); // start with 3 ammo
        int initialAmmo = player.ammo;
        int initialBulletCount = Canvas.activeBullets.size();
        Bullet.bulletImage = bfr;
        //press shoot key
        Canvas.getKeysPressed()[3] = true;
        player.update();
        Canvas.getKeysPressed()[3] = false;

        //check ammo decreased
        assertEquals(initialAmmo - 1, player.ammo, "Player ammo should decrease after shooting");
        //active bullets should match the bullet count
        assertEquals(initialBulletCount + 1, Canvas.activeBullets.size(), "A new bullet should be added to activeBullets when shooting");
        assertTrue(player.justShot, "justShot should be true right after firing a bullet");
    }

    // T9
    @Test
    @DisplayName("Verifies that damaging the player reduces health and sets damage state.")
    void testPlayerDamaged() {
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{ bfr };
        Enemy.idleSprites = new Image[]{ bfr };
        MapBlocks.map.clear();

        Player player = new Player(100, 100, 0);
        int initialHealth = player.health;

        // Place enemy to the right of the player so knockback goes left (speed -4)
        Enemy enemy = new Enemy(200, 100, 2);

        //player should not be damaged initially, sanity check
        assertFalse(player.isDamaged, "Player should not start in damaged state");
        player.damage(enemy);
        assertEquals(initialHealth - 1, player.health, "Player health should decrease by 1 when damaged");

        //check damaged flag correct and knockback applied
        assertTrue(player.isDamaged, "Player should be marked as damaged after taking a hit");
        assertEquals(-4, player.speed, "Player should be knocked back to the left when hit from the right");
        assertEquals(-6.0, player.velocity, 0.0001, "Player should be knocked upwards when damaged");
    }

    // T10
    @Test
    @DisplayName("Ensures that a fatal hit increments deathCounter and respawns the level.")
    void testPlayerKilled() {
        //pre reqs
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{ bfr };
        Enemy.idleSprites = new Image[]{ bfr };
        CyborgPlatform.game = new Game();

        Canvas.activeBullets.clear();
        Arrays.fill(Canvas.getKeysPressed(), false);
        MapBlocks.map.clear();

        Player.deathCounter = 0;
        Player player = new Player(100, 100, 3);

        //player health 0, so player's dead
        player.health = 0;
        player.update();

        // deathCounter should have incremented
        assertEquals(1, Player.deathCounter, "deathCounter should increment when the player dies");

        // spawnEntities() should have created a new Player at the checkpoint
        assertNotNull(Canvas.getPlayer(), "spawnEntities() should create a new player");
        assertEquals(20, Canvas.getPlayer().x, "Respawned player should start at checkpoint X position");
        assertEquals(300, Canvas.getPlayer().y, "Respawned player should start at checkpoint Y position");
    }

    // T11
    @Test
    @DisplayName("Tests that updateCmr() keeps the camera within level bounds.")
    void testUpdateCamera() {
        //pre reqs
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{ bfr };
        MapBlocks.mapWidth = 8000;

        Player player = new Player(0, 0, 0);

        // Left edge: player near start of level
        player.x = 0;
        int camLeft = player.updateCmr();
        assertEquals(0, camLeft, "Camera should be 0 at the left edge of the map");

        // Middle of the map: camera should centre around player.x
        player.x = 4000;
        int camMid = player.updateCmr();
        int expectedMid = 4000 - (1280 - 30) / 2; // x - half viewport width (with 30 offset)
        assertEquals(expectedMid, camMid, "Camera should centre around player in middle of map");

        // Right edge: camera should be clamped to max
        player.x = 8000;
        int camRight = player.updateCmr();
        int maxCam = MapBlocks.mapWidth - 1280 - 30;
        assertEquals(maxCam, camRight, "Camera should be clamped at the right edge of the map");
    }

    // T12
    @Test
    @DisplayName("Pressing W increments jumpCounter and applies jump")
    void testPlayerJump() {
        BufferedImage bfr = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Player.idleSprites = new Image[]{ bfr };

        Arrays.fill(Canvas.getKeysPressed(), false);
        MapBlocks.map.clear();

        Player player = new Player(0, 0, 10);
        player.isGrounded = true;
        player.jumpCounter = 0;
        player.velocity = 0;

        Canvas.getKeysPressed()[2] = true; // W
        player.update();
        Canvas.getKeysPressed()[2] = false;

        assertEquals(1, player.jumpCounter, "jumpCounter should increment when W is pressed");
        //assertTrue(player.velocity < 0, "Velocity should be negative after jump");
    }

}