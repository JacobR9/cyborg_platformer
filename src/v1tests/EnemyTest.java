import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EnemyTest {

    private static Image dummyImage(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    }
    //initialises stubs to replace and mock certain objects
    private void stubSprites() {
        Image dummy = dummyImage(30, 30);

        Enemy.idleSprites = new Image[]{ dummy };
        Enemy.walkingSprites = new Image[]{ dummy };
        Enemy.runningSprites = new Image[]{ dummy, dummy, dummy, dummy, dummy };
        Enemy.hurtSprite = dummy;

        Player.idleSprites = new Image[]{ dummy };
        Player.shootingSprite = (BufferedImage) dummy;

        Canvas.enemies = new ArrayList<>();
        Canvas.setPlayer(new Player(0, 0, 10));

        MapBlocks.map.clear();
    }

    // T17
    @Test
    @DisplayName("Checks that a new Enemy object initialises correctly.")
    void testEnemyCreates() {
        stubSprites();

        Enemy enemy = new Enemy(100, 200, 2);

        assertNotNull(enemy, "Enemy should not be null");
        assertEquals(100, enemy.x, "Enemy x should be initialised correctly");
        assertEquals(200, enemy.y, "Enemy y should be initialised correctly");
        assertEquals(2, enemy.health, "Enemy health should default to 2 (set in constructor)");
        assertEquals(30, enemy.hitBox, "Enemy hitBox should default to 30");
    }

    // T18
    @Test
    @DisplayName("Verifies distanceFromPlayer returns correct distance.")
    void testDistanceFromPlayerReturns() {
        stubSprites();

        Player p = new Player(0, 0, 10);
        Enemy e = new Enemy(3, 4, 2);
        double d = e.distanceFromPlayer(p);
        assertEquals(5.0, d, 0.0001, "Distance should be 5 based on player's distance");
    }

    // T19
    @Test
    @DisplayName("Checks Enemy collision with Player is correctly detected.")
    void testEnemyCollidesWithPlayer() {
        stubSprites();

        Player p = new Player(50, 50, 10);
        Enemy e = new Enemy(60, 60, 2); // overlaps given 30x30 dummy images

        assertTrue(e.collidesPlayer(p), "Enemy should collide with player when overlapping");
    }

    // T20
    @Test
    @DisplayName("Ensures Enemy isMoving reflects speed state.")
    void testEnemyIsMoving() {
        stubSprites();
        Enemy e = new Enemy(0, 0, 2);
        e.speed = 0;
        assertFalse(e.isMoving(), "Enemy should not be moving when speed is 0");

        e.speed = 1;
        assertTrue(e.isMoving(), "Enemy should be moving when speed is non-zero");
    }

    // T21
    @Test
    @DisplayName("Verifies that damaging the enemy reduces health and updates state.")
    void testEnemyDamaged() {
        stubSprites();
        Enemy e = new Enemy(0, 0, 2);
        int startHealth = e.health;
        e.damage();

        assertEquals(startHealth - 1, e.health, "Enemy health should reduce by 1 on damage()");
        assertTrue(e.isDamaged, "Enemy should enter damaged state after damage()");
        assertEquals(-6.0, e.velocity, 0.0001, "Enemy should be knocked upwards (velocity -6)");

        //calling damage again while already damaged should not reduce health further
        e.damage();
        assertEquals(startHealth - 1, e.health, "Enemy health should not reduce again while isDamaged is true");
    }

    // T22
    @Test
    @DisplayName("Ensures enemy is removed and player gets ammo when killed.")
    void testEnemyKilled() {
        stubSprites();
        Enemy e = new Enemy(0, 0, 2);
        Canvas.enemies.add(e);
        int startAmmo = Canvas.getPlayer().ammo;
        e.kill();

        assertFalse(Canvas.enemies.contains(e), "Enemy should be removed from canvas.enemies on kill()");
        assertEquals(startAmmo + 2, Canvas.getPlayer().ammo, "Player should gain 2 ammo when an enemy is killed");
    }
}
