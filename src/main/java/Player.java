import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.List;

/**
 * Player-controlled entity.
 * <p>
 * Extends {@link Entity} and implements player-specific behaviour including
 * movement, jumping, gravity interaction, damage handling, win detection,
 * and sprite animation state updates.
 *
 * @author psyjr14
 */
public class Player extends Entity {

    /** Respawn checkpoint position (in world coordinates). */
    protected Point2D checkPoint = new Point2D(20, 300);

    /** True for a short window after firing, used for animation/state. */
    private boolean justShot;

    /** Timestamp (ms) of the last shot. */
    private double lastShot;

    /** True once the player reaches the win threshold. */
    private boolean won;

    private final Image[] idleSprites;
    private final Image[] runningSprites;
    private final Image[] hurtSprites;
    private final Image shootingSprite;

    /**
     * Constructs a new player with the given starting position, health, and sprite sets.
     *
     * @param x initial x-position in pixels
     * @param y initial y-position in pixels
     * @param health starting health value
     * @param idleSprites idle animation sprites (index 0 is used as the initial image)
     * @param runningSprites running animation sprites
     * @param hurtSprites hurt animation sprites
     * @param shootingSprite sprite shown while shooting
     */
    public Player(
            int x,
            int y,
            int health,
            Image[] idleSprites,
            Image[] runningSprites,
            Image[] hurtSprites,
            Image shootingSprite
    ) {
        super(idleSprites[0], x, y, health, 10, 30);
        this.idleSprites = idleSprites;
        this.runningSprites = runningSprites;
        this.hurtSprites = hurtSprites;
        this.shootingSprite = shootingSprite;
    }

    /**
     * Per-frame update for the player.
     * <p>
     * Applies gravity, horizontal movement, jump handling (including double jump),
     * variable jump height, damage timers, win condition checks, and animation updates.
     *
     * @param keys key state array (commonly: 0 = left, 1 = right, 2 = jump-held)
     * @param lastDir last facing direction when no directional keys are pressed
     * @param jumpPressed true if a jump was requested this frame
     * @param blocks map blocks used for collision detection
     */
    public void update(boolean[] keys, boolean lastDir, boolean jumpPressed, List<MapBlock> blocks) {
        applyGravity(blocks);
        updateHorizontalMovement(keys, blocks);
        handleJump(jumpPressed);
        updateJumpAcceleration(keys);
        updateTimers();
        checkWin();
        updateState(keys, lastDir);
        animate();
    }

    /**
     * Updates horizontal movement and resolves collisions with solid map blocks.
     * <p>
     * Horizontal speed is adjusted using left/right inputs and clamped to a maximum.
     * If the next predicted position intersects a block, the speed is dampened.
     *
     * @param keys input state (0 = left, 1 = right)
     * @param blocks map blocks used for collision detection
     */
    private void updateHorizontalMovement(boolean[] keys, List<MapBlock> blocks) {
        int maxSpeed = 5;

        Entity entityCopy = copy(x + speed, y);
        if (!entityCopy.intersect(blocks)) {
            x += speed;

            if (keys[0] && speed > -maxSpeed) speed -= 1;
            if (keys[1] && speed < maxSpeed) speed += 1;

            if (((!keys[0] && !keys[1]) || (keys[0] && keys[1])) && !isDamaged) {
                speed = 0;
            }
        } else {
            speed /= 2;
        }
    }

    /**
     * Applies gravity to the player and resets jump tracking when grounded.
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
     * Processes a jump request and enforces the double-jump limit.
     *
     * @param jumpPressed true if a jump was requested this frame
     */
    private void handleJump(boolean jumpPressed) {
        if (jumpPressed && jumpCounter < 2) {
            jump();
            jumpCounter++;
        }
    }

    /**
     * Applies variable jump height behaviour.
     * <p>
     * While the jump key is held and the player is rising, gravity is reduced
     * to allow higher jumps.
     *
     * @param keys input state (2 = jump key held)
     */
    private void updateJumpAcceleration(boolean[] keys) {
        if (isGrounded || !keys[2] || velocity > 0) acceleration = 0.5;
        if (keys[2] && acceleration > 0.25) acceleration -= 0.02;
    }

    /**
     * Updates internal timers controlling damage invincibility and shooting cooldown.
     */
    private void updateTimers() {
        if ((System.currentTimeMillis() - damagedTime) > 1000 && isDamaged) isDamaged = false;
        if ((System.currentTimeMillis() - lastShot) > 500 && justShot) justShot = false;
    }

    /**
     * Checks whether the player has reached the win threshold and sets the win flag.
     */
    private void checkWin() {
        if (x > 8000) won = true;
    }

    /**
     * Calculates the desired camera x-position so the player is centred in the view.
     *
     * @param viewWidth width of the viewport in pixels
     * @return desired camera x-position
     */
    public int desiredCameraX(int viewWidth) {
        return x - (viewWidth - hitBox) / 2;
    }

    /**
     * Determines the direction the player should face based on inputs.
     *
     * @param keys key state array (0 = left, 1 = right)
     * @param lastDir last facing direction used when no input is active
     * @return true if facing forwards, false if facing backwards
     */
    public boolean isFacingForwards(boolean[] keys, boolean lastDir) {
        if (!keys[0] && keys[1]) return true;
        if (keys[0] && !keys[1]) return false;
        return lastDir;
    }

    /**
     * Checks whether the player is moving horizontally.
     *
     * @param keys key state array (0 = left, 1 = right)
     * @return true if left or right is pressed
     */
    public boolean isMoving(boolean[] keys) {
        return (keys[0] || keys[1]);
    }

    /**
     * Checks whether the player is currently jumping (or has jumped).
     *
     * @return true if jumpCounter is greater than zero
     */
    public boolean isJumping() {
        return (jumpCounter > 0);
    }

    /**
     * Updates the player's logical animation state.
     * <p>
     * State priority (highest to lowest): shooting, hurt, aerial, running, idle.
     *
     * @param keys key state array
     * @param lastDir last known direction used when idle
     */
    public void updateState(boolean[] keys, boolean lastDir) {
        String newState = "idle";

        if (isGrounded && isMoving(keys)) newState = "running";
        if (isGrounded && !isMoving(keys)) newState = "idle";
        if (!isGrounded) newState = "aerial";
        if (isDamaged) newState = "hurt";
        if (justShot) newState = "shooting";

        state = new EntityState(isFacingForwards(keys, lastDir), newState);
    }

    /**
     * Updates the currently displayed sprite based on state and timing.
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
            case "running" -> {
                if (lastAnimation >= runningSprites.length) lastAnimation = 0;
                if ((System.currentTimeMillis() - lastTime) > 180) {
                    image = runningSprites[lastAnimation];
                    lastAnimation++;
                    lastTime = System.currentTimeMillis();
                }
            }
            case "aerial" -> {
                if (velocity < 0) image = runningSprites[5];
            }
            case "hurt" -> image = hurtSprites[1];
            case "shooting" -> image = shootingSprite;
        }
    }

    /**
     * Attempts to spawn a bullet if shooting is currently allowed.
     * <p>
     * Shooting is limited by a cooldown window ({@code justShot}) and available ammunition.
     * When a shot is successful, ammunition is reduced, the cooldown timer is started, and
     * a new {@link Bullet} is created at an offset relative to the player's position.
     * <p>
     * This keeps player-specific shooting rules encapsulated in {@code Player}, while the
     * {@code Game} class remains responsible for owning and updating the global bullet list.
     *
     * @param facing true if the player is facing forwards (right), false if facing backwards (left)
     * @param bulletImage sprite used for the bullet entity
     * @return a newly created bullet if the shot was fired, or {@code null} if shooting was not allowed
     */
    public Bullet tryShoot(boolean facing, Image bulletImage) {
        if (justShot || ammo <= 0) return null;

        ammo--;
        justShot = true;
        lastShot = System.currentTimeMillis();

        int bx = facing ? x + 47 : x - 25;
        int by = y + 10;
        int sp = facing ? 10 : -10;

        Bullet b = new Bullet(bx, by, bulletImage);
        b.speed = sp;
        b.startPoint = new Point2D(b.x, b.y);
        return b;
    }


    /**
     * Applies damage and knockback when colliding with an enemy.
     * <p>
     * Includes a brief invincibility window controlled by {@code isDamaged}.
     *
     * @param e enemy that caused the damage
     */
    public void damage(Enemy e) {
        if (!isDamaged) {
            health--;
            damagedTime = System.currentTimeMillis();
            isDamaged = true;

            if (x < e.x) {
                speed = -4;
            } else {
                speed = 4;
            }
            velocity = -6;
        }
    }

    /**
     * Returns whether the player has met the win condition.
     *
     * @return true if the player has won
     */
    public boolean hasWon() {
        return won;
    }

    /**
     * Checks whether the player is dead due to falling out of bounds or losing all health.
     *
     * @return true if dead
     */
    public boolean isDead() {
        return y > 900 || health <= 0;
    }
}
