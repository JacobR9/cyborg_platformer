import java.util.List;
import javafx.scene.image.Image;

/**
 * Base class for all in-game entities such as Player, Enemy, and Bullet.
 * <p>
 * Stores shared attributes including position, velocity, health, and
 * physics behaviour such as jumping, gravity, and collision detection.
 *
 * @author psyjr14
 */
public class Entity {

    protected boolean isDamaged;
    protected double damagedTime;
    protected boolean isGrounded = false;
    protected int speed = 0;
    protected Image image;
    protected int x;
    protected int y;
    protected double velocity;
    protected double acceleration = 0.5;
    protected int health;
    protected int ammo;
    protected EntityState state;
    public int jumpCounter = 0;
    protected int jumpX;
    protected int jumpY;
    protected double lastTime = System.currentTimeMillis();
    public int lastAnimation = 0;
    public int hitBox;

    /**
     * Constructs a new entity with the given attributes.
     *
     * @param image entity sprite
     * @param x initial x-position
     * @param y initial y-position
     * @param health starting health value
     * @param ammo starting ammo count
     * @param hitBox hitbox width in pixels
     */
    public Entity(Image image, int x, int y, int health, int ammo, int hitBox) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.health = health;
        this.ammo = ammo;
        this.hitBox = hitBox;
        this.state = new EntityState(true, "idle");
    }

    /**
     * Checks collision between this entity and map blocks.
     *
     * @param blocks list of map blocks to test against
     * @return true if this entity intersects a block
     */
    public boolean intersect(List<MapBlock> blocks) {
        if (blocks == null || image == null) {
            return false;
        }

        int x2 = x + hitBox;
        double y2 = y + image.getHeight();

        for (MapBlock block : blocks) {
            int blockX = block.x;
            int blockY = block.y;
            double blockX2 = block.x + block.image.getWidth();
            double blockY2 = block.y + block.image.getHeight();

            boolean widthIsPositive = Math.min(x2, blockX2) > Math.max(x, blockX);
            boolean heightIsPositive = Math.min(y2, blockY2) > Math.max(y, blockY);

            if (widthIsPositive && heightIsPositive) {
                return true;
            }
        }
        return false;
    }

    /**
     * Applies an upward velocity to simulate jumping.
     * <p>
     * A second jump applies a weaker force.
     */
    public void jump() {
        if (jumpCounter == 1) {
            velocity = -6;
        } else {
            velocity = -8;
        }
        jumpX = x;
        jumpY = y;
    }

    /**
     * Creates a shallow copy of this entity at a new position.
     * Used for collision prediction during gravity updates.
     *
     * @param newX x-position of the copy
     * @param newY y-position of the copy
     * @return copied entity
     */
    public Entity copy(int newX, int newY) {
        Entity copy = new Entity(image, newX, newY, health, ammo, hitBox);
        copy.image = image;
        return copy;
    }

    /**
     * Applies gravity and resolves vertical collisions with the map.
     *
     * @param blocks list of map blocks used for collision detection
     */
    public void gravity(List<MapBlock> blocks) {
        Entity entityCopy = copy(x, (int) (y + velocity));

        if (!entityCopy.intersect(blocks)) {
            y += velocity;
            velocity += acceleration;
            isGrounded = false;
        } else {
            if (velocity > 1.5) {
                velocity /= 1.5;
            }
            if (velocity < 0) {
                velocity = -(velocity / 4);
            } else {
                isGrounded = true;
            }
        }
    }
}
