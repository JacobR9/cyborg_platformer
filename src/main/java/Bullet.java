import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Projectile entity fired by the player.
 * <p>
 * A bullet moves horizontally, is removed when it hits a solid map block or an enemy,
 * or once it exceeds its maximum travel distance.
 *
 * @author psyjr14
 */
public class Bullet extends Entity {

    /** World position where the bullet was created, used to compute travel distance. */
    public Point2D startPoint;

    /** Cached bullet sprite used when creating predictive copies. */
    private final Image bulletImage;

    /**
     * Constructs a new bullet at the given position.
     *
     * @param x initial x-position in pixels
     * @param y initial y-position in pixels
     * @param bulletImage sprite used to render the bullet
     */
    public Bullet(int x, int y, Image bulletImage) {
        super(bulletImage, x, y, 1, 0, (int) bulletImage.getWidth());
        this.bulletImage = bulletImage;
    }

    /**
     * Updates bullet movement and resolves collisions.
     * <p>
     * The bullet advances by its current {@code speed} unless the next position would
     * collide with a map block or an enemy. If the bullet hits something or travels
     * beyond its maximum range, this method returns {@code true} to indicate removal.
     *
     * @param enemies list of enemies to test collision against
     * @param blocks list of map blocks used for collision detection
     * @return true if the bullet should be removed; false if it remains active
     */
    public boolean update(List<Enemy> enemies, List<MapBlock> blocks) {
        Bullet bulletCopy = copy(x + speed, y);

        if (!bulletCopy.intersect(blocks) && !bulletCopy.collidesEnemy(enemies)) {
            x += speed;
            return travelledDistance() >= 600;
        }
        return true;
    }

    /**
     * Creates a temporary bullet copy used to test collisions before applying movement.
     *
     * @param newX x-position for the copy
     * @param newY y-position for the copy
     * @return a new {@link Bullet} instance representing the predicted position
     */
    public Bullet copy(int newX, int newY) {
        Bullet copy = new Bullet(newX, newY, bulletImage);
        copy.speed = 0;
        copy.velocity = 0;
        copy.image = image;
        copy.state = state;
        return copy;
    }

    /**
     * Computes the distance travelled from the {@link #startPoint} to the current position.
     *
     * @return Euclidean distance travelled in pixels
     */
    public double travelledDistance() {
        double dx = startPoint.getX() - x;
        double dy = startPoint.getY() - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks for collision with any enemy. If a hit occurs, the enemy is damaged.
     *
     * @param enemies list of enemies to test collision against
     * @return true if this bullet intersects at least one enemy
     */
    public boolean collidesEnemy(List<Enemy> enemies) {
        boolean isInside = false;

        for (Enemy e : enemies) {
            double x2 = x + image.getWidth();
            double y2 = y + image.getHeight();
            double eX2 = e.x + e.image.getWidth();
            double eY2 = e.y + e.image.getHeight();

            boolean widthIsPositive = Math.min(x2, eX2) > Math.max(x, e.x);
            boolean heightIsPositive = Math.min(y2, eY2) > Math.max(y, e.y);

            if (widthIsPositive && heightIsPositive) {
                isInside = true;
                e.damage();
            }
        }

        return isInside;
    }
}
