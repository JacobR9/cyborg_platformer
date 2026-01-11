import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BulletTest {

    private static Image dummyImg() {
        return new BufferedImage(30, 30, BufferedImage.TYPE_INT_RGB);
    }

    //setup that makes stubs and mocks
    @BeforeEach
    void setup() {
        MapBlocks.map.clear();

        Canvas.activeBullets = new ArrayList<>();
        Canvas.enemies = new ArrayList<>();

        Bullet.bulletImage = dummyImg();

        Image e = dummyImg();
        Enemy.idleSprites = new Image[]{ e };
        Enemy.walkingSprites = new Image[]{ e };
        Enemy.runningSprites = new Image[]{ e, e, e, e, e };
        Enemy.hurtSprite = e;
    }

    //T23
    @Test
    @DisplayName("Checks that a new Bullet initialises correctly.")
    void testMakeBullet() {
        Bullet b = new Bullet(10, 20);
        b.startPoint = new Point(10, 20);

        assertNotNull(b, "Bullet should not be null");
        assertEquals(10, b.x);
        assertEquals(20, b.y);
        assertEquals(1, b.health);
    }

    //T24
    @Test
    @DisplayName("Verifies Bullet collision detection with Enemy.")
    void testBulletCollides() {
        Enemy enemy = new Enemy(5, 0, 2);
        Canvas.enemies.add(enemy);
        Bullet b = new Bullet(0, 0);
        b.speed = 5;

        assertTrue(b.collidesEnemy(), "Bullet should detect collision with enemy");
    }

    //T25
    @Test
    @DisplayName("Ensures update applies collision effects.")
    void testBulletUpdatesOnCollision() {
        Enemy enemy = new Enemy(5, 0, 2);
        Canvas.enemies.add(enemy);

        Bullet b = new Bullet(0, 0);
        b.startPoint = new Point(0, 0);
        b.speed = 5;
        Canvas.activeBullets.add(b);
        int startHealth = enemy.health;
        b.update();

        assertEquals(startHealth - 1, enemy.health);
        assertFalse(Canvas.activeBullets.contains(b), "Bullet should be removed after collision");
    }

    //T26
    @Test
    @DisplayName("Checks Bullet deactivates after exceeding max range.")
    void testTravelledDistance() {
        Bullet b = new Bullet(600, 0);
        b.startPoint = new Point(0, 0);
        b.speed = 5;
        Canvas.activeBullets.add(b);
        b.update();

        assertFalse(Canvas.activeBullets.contains(b), "Bullet should be removed after max distance");
    }
}
