import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnemyTest {

    private Enemy enemy;
    private Player player;

    @BeforeEach
    void setUp() {
        Image img = new WritableImage(48, 48);

        Image[] pIdle = new Image[] { img, img, img, img };
        Image[] pRun  = new Image[] { img, img, img, img, img, img };
        Image[] pHurt = new Image[] { img, img };
        Image pShoot  = img;

        player = new Player(0, 0, 3, pIdle, pRun, pHurt, pShoot);

        Image[] eIdle = new Image[] { img, img, img, img, img, img, img, img };
        Image[] eWalk = new Image[] { img, img, img, img, img, img, img, img };
        Image[] eRun  = new Image[] { img, img, img, img, img, img, img };
        Image eHurt   = img;

        enemy = new Enemy(0, 0, 2, eIdle, eWalk, eRun, eHurt);
    }

    @Test
    void testDistanceFromPlayer() {
        player.x = 3;
        player.y = 4;
        enemy.x = 0;
        enemy.y = 0;

        assertEquals(5.0, enemy.distanceFromPlayer(player), 1e-9);
    }

    @Test
    void testEnemyCollidesPlayer() {
        // Overlap
        enemy.x = 100;
        enemy.y = 100;
        player.x = 110;
        player.y = 110;
        assertTrue(enemy.collidesPlayer(player));

        // No overlap
        player.x = 1000;
        player.y = 1000;
        assertFalse(enemy.collidesPlayer(player));
    }

    @Test
    void testEnemyDamage() {
        int startHealth = enemy.health;

        enemy.damage();
        assertEquals(startHealth - 1, enemy.health, "Health should decrement on first damage");
        assertTrue(enemy.isDamaged, "Enemy should enter damaged state after hit");

        // Second call during damaged window should not reduce health
        enemy.damage();
        assertEquals(startHealth - 1, enemy.health, "Health should not decrement again while damaged");
    }

    @Test
    void testEnemyDeath() {
        enemy.health = 0;
        assertTrue(enemy.isDead(), "Enemy should be dead when health <= 0");

        enemy.health = 2;
        enemy.y = 901;
        assertTrue(enemy.isDead(), "Enemy should be dead when y > 900");
    }
}
