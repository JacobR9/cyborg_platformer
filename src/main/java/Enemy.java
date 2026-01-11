import javafx.scene.image.Image;

import java.util.List;

/**
 * Enemy AI-controlled entity.
 * <p>
 * Extends {@link Entity} and implements enemy behaviour such as chasing the player
 * within a detection range, jumping when appropriate, colliding with the player to
 * apply damage, taking damage from bullets, and updating sprite animations based on
 * state (idle, walking, running, aerial, hurt).
 *
 * @author psyjr14
 */
public class Enemy extends Entity {

    /** True when the enemy is moving faster to get player. */
    public boolean isRunning;

    /** Current facing direction used for rendering/animation. */
    public boolean isFacingForwards;

    private final Image[] idleSprites;
    private final Image[] walkingSprites;
    private final Image[] runningSprites;
    private final Image hurtSprite;

    /**
     * Constructs a new enemy at the given position.
     * <p>
     * Enemy health is initialised to 2 (ignoring the {@code health} parameter in the current
     * implementation), with ammo set to 0 and a fixed hitbox.
     *
     * @param x initial x-position in pixels
     * @param y initial y-position in pixels
     * @param health (currently unused) requested starting health
     * @param idleSprites idle animation sprites
     * @param walkingSprites walking animation sprites
     * @param runningSprites running animation sprites
     * @param hurtSprite sprite shown while hurt
     */
    public Enemy(
            int x,
            int y,
            int health,
            Image[] idleSprites,
            Image[] walkingSprites,
            Image[] runningSprites,
            Image hurtSprite
    ) {
        super(idleSprites[0], x, y, 2, 0, 30);
        this.idleSprites = idleSprites;
        this.walkingSprites = walkingSprites;
        this.runningSprites = runningSprites;
        this.hurtSprite = hurtSprite;
    }

    /**
     * Per-frame update for enemy AI.
     * <p>
     * Applies gravity, decides whether the player is within aggro range, moves toward the
     * player, performs jump logic, handles collision with the player, applies hurt timers,
     * and updates animation state.
     *
     * @param p player to chase and collide with
     * @param enemies list of enemies (available for group behaviour if needed)
     * @param blocks map blocks used for collision detection
     */
    public void update(Player p, List<Enemy> enemies, List<MapBlock> blocks) {
        double dist = distanceFromPlayer(p);

        applyGravity(blocks);
        if (shouldChasePlayer(p, dist)) {
            updateChaseMovement(p, dist, blocks);
            handleJump(p, dist);
            handlePlayerCollision(p);
        } else {
            stopChasing();
        }
        applyDamageSlowdown();
        updateDamageTimers();
        updateState();
        animate();
    }

    /**
     * Applies gravity to the enemy and resets jump tracking when grounded.
     *
     * @param blocks map blocks used for collision detection
     */
    private void applyGravity(List<MapBlock> blocks) {
        gravity(blocks);
        if (isGrounded) {
            jumpCounter = 0;
        }
    }

    /**
     * Determines whether this enemy should chase the player.
     * <p>
     * The enemy will attempt to chase when the player is within a distance threshold
     * and roughly on the same vertical level.
     *
     * @param p player instance
     * @param distanceFromPlayer precomputed distance to the player
     * @return true if the enemy should chase, false otherwise
     */
    private boolean shouldChasePlayer(Player p, double distanceFromPlayer) {
        return distanceFromPlayer <= 400 && (y - p.y) < 100;
    }

    /**
     * Updates horizontal chase movement toward the player and resolves block collisions.
     * <p>
     * When close to the player the enemy switches into a running mode with a higher
     * maximum speed.
     *
     * @param p player instance to chase
     * @param distanceFromPlayer precomputed distance to the player
     * @param blocks map blocks used for collision detection
     */
    private void updateChaseMovement(Player p, double distanceFromPlayer, List<MapBlock> blocks) {
        double maxSpeed = 1;
        isRunning = false;

        if (distanceFromPlayer <= 200) {
            maxSpeed = 3;
            isRunning = true;
        }

        Entity entityCopy = copy(x + speed, y);
        if (entityCopy.intersect(blocks)) {
            return;
        }

        x += speed;

        if (p.x < x) {
            if (speed > -maxSpeed) speed -= 1;
            isFacingForwards = false;
        }

        if (p.x > x) {
            if (speed < maxSpeed) speed += 1;
            isFacingForwards = true;
        } else if (Math.abs(x - p.x) < 20) {
            speed = 0;
        }
    }

    /**
     * Handles jump decision logic.
     * <p>
     * Enemy jumps when the player is above and either already airborne or
     * trying to close distance while within an aggressive range.
     *
     * @param p player instance
     * @param distanceFromPlayer precomputed distance to the player
     */
    private void handleJump(Player p, double distanceFromPlayer) {
        if (((!isGrounded) || (distanceFromPlayer <= 200 && Math.abs(x - p.x) > 40))
                && jumpCounter < 1
                && p.y < y) {
            jump();
            jumpCounter++;
        }
    }

    /**
     * Applies damage to the player if this enemy is currently colliding with them.
     *
     * @param p player instance
     */
    private void handlePlayerCollision(Player p) {
        if (collidesPlayer(p)) {
            p.damage(this);
        }
    }

    /**
     * Stops chase behaviour and resets chase-related flags.
     */
    private void stopChasing() {
        speed = 0;
        isRunning = false;
    }

    /**
     * Applies a movement slowdown while the enemy is in the damaged state.
     */
    private void applyDamageSlowdown() {
        if (isDamaged) {
            speed /= 2;
        }
    }

    /**
     * Updates the damaged timer and clears the damaged flag once the
     * invincibility/hurt window has expired.
     */
    private void updateDamageTimers() {
        if ((System.currentTimeMillis() - damagedTime) > 300 && isDamaged) {
            isDamaged = false;
        }
    }

    /**
     * Computes the straight-line distance from this enemy to the player.
     *
     * @param p player instance
     * @return Euclidean distance in pixels
     */
    public double distanceFromPlayer(Player p) {
        int dx = p.x - x;
        int dy = p.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Checks whether this enemy overlaps the player hit area.
     *
     * @param p player instance
     * @return true if the enemy intersects the player
     */
    public boolean collidesPlayer(Player p) {
        double x2 = x + image.getWidth();
        double y2 = y + image.getHeight();

        double playerX2 = p.x + p.image.getWidth();
        double playerY2 = p.y + p.image.getHeight();

        boolean widthIsPositive = Math.min(x2, playerX2) > Math.max(x, p.x);
        boolean heightIsPositive = Math.min(y2, playerY2) > Math.max(y, p.y);

        return widthIsPositive && heightIsPositive;
    }

    /**
     * Checks whether the enemy is currently moving horizontally.
     *
     * @return true if horizontal speed is non-zero
     */
    public boolean isMoving() {
        return speed != 0;
    }

    /**
     * Updates the enemy logical state (idle, walking, running, aerial, hurt)
     * based on movement, grounded status, and damage state.
     */
    public void updateState() {
        String newState = "idle";

        if (isGrounded && isMoving()) newState = "walking";
        if (isGrounded && isMoving() && isRunning) newState = "running";
        if (isGrounded && !isMoving()) newState = "idle";
        if (!isGrounded) newState = "aerial";
        if (isDamaged) newState = "hurt";

        state = new EntityState(isFacingForwards, newState);
    }

    /**
     * Updates the current sprite based on {@link #state} and timing.
     */
    public void animate() {
        switch (state.getState()) {
            case "idle" -> {
                if (lastAnimation >= idleSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 250) {
                    image = idleSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "walking" -> {
                if (lastAnimation >= walkingSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = walkingSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "running" -> {
                if (lastAnimation >= runningSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = runningSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "aerial" -> {
                if (velocity < 0) image = runningSprites[4];
            }
            case "hurt" -> image = hurtSprite;
        }
    }

    /**
     * Applies damage to the enemy if not currently in the damaged state.
     * <p>
     * Reduces health by 1, triggers a brief hurt window, and applies upward knockback.
     */
    public void damage() {
        if (!isDamaged) {
            health--;
            damagedTime = System.currentTimeMillis();
            isDamaged = true;
            velocity = -6;
        }
    }

    /**
     * Checks whether the enemy should be considered dead.
     *
     * @return true if health is depleted or the enemy fell out of bounds
     */
    public boolean isDead() {
        return health <= 0 || y > 900;
    }
}
