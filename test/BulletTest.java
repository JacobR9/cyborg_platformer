import static org.junit.jupiter.api.Assertions.*;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

public class BulletTest {

    private Image bulletImg;

    @BeforeEach
    void setUp() {
        bulletImg = new WritableImage(10, 10);
    }

    @Test
    void testBulletTravelRange() {
        Bullet b = new Bullet(0, 0, bulletImg);
        b.speed = 10;
        b.startPoint = new Point2D(b.x, b.y);

        // After 59 moves: 590px (should still be active => update returns false)
        boolean removed = false;
        for (int i = 0; i < 59; i++) {
            removed = b.update(Collections.emptyList(), Collections.emptyList());
            assertFalse(removed, "Bullet should still be active before reaching 600px");
        }

        // 60th move: 600px => should return true (remove)
        removed = b.update(Collections.emptyList(), Collections.emptyList());
        assertTrue(removed, "Bullet should be removed at >= 600px travelled");
    }

    @Test
    void testBulletHitsEnemy() {
        // Enemy needs real image sizes (collides uses image.getWidth/Height)
        Image enemyImg = new WritableImage(48, 48);
        Image[] enemyIdle = new Image[] { enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg };
        Image[] enemyWalk = new Image[] { enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg };
        Image[] enemyRun  = new Image[] { enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg, enemyImg };
        Image enemyHurt = enemyImg;

        // Player is not needed for this test, only enemy list.
        Enemy e = new Enemy(100, 100, 2, enemyIdle, enemyWalk, enemyRun, enemyHurt);
        int startHealth = e.health;

        Bullet b = new Bullet(100, 100, bulletImg);
        b.speed = 0; // keep bullet where it is so copy(x+speed,y) stays overlapping
        b.startPoint = new Point2D(b.x, b.y);

        ArrayList<Enemy> enemies = new ArrayList<>();
        enemies.add(e);

        boolean removed = b.update(enemies, Collections.emptyList());

        assertTrue(removed, "Bullet should be removed when it hits an enemy");
        assertEquals(startHealth - 1, e.health, "Enemy health should reduce by 1 on hit");
    }
}
