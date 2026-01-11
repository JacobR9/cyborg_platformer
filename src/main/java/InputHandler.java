import java.io.Serializable;
import javafx.scene.input.KeyCode;

/**
 * Handles keyboard input for the game.
 *
 * <p>This class acts as the input controller in the MVC architecture.
 * It translates JavaFX {@link KeyCode} events into a simple internal
 * representation that the {@link Game} and {@link Player} logic can consume
 * without depending on JavaFX APIs.</p>
 *
 * <p>Input is stored as state (e.g. movement keys held) and as one-shot
 * requests (jump, shoot, restart) that are consumed during the game update
 * cycle.</p>
 *
 * @author psyjr14
 */
public class InputHandler implements Serializable {

    /**
     * Array representing currently held movement keys.
     *
     * <ul>
     *   <li>index 0: move left (A)</li>
     *   <li>index 1: move right (D)</li>
     *   <li>index 2: jump (W)</li>
     *   <li>index 3: unused / reserved</li>
     * </ul>
     */
    private final boolean[] keysPressed = new boolean[4];

    /** Tracks the last horizontal movement direction for sprite facing. */
    private boolean lastDirectionForwards = true;

    /** One-shot flag indicating a jump was requested. */
    private boolean jumpRequested = false;

    /** One-shot flag indicating a restart was requested. */
    private boolean restartRequested = false;

    /** One-shot flag indicating a shoot action was requested. */
    private boolean shootRequested = false;

    private boolean pauseRequested = false;

    /**
     * Consumes and returns the jump request flag.
     *
     * <p>This method resets the flag after reading it so the jump is
     * processed only once per key press.</p>
     *
     * @return {@code true} if a jump was requested since the last update
     */
    public boolean processJump() {
        boolean v = jumpRequested;
        jumpRequested = false;
        return v;
    }

    /**
     * Consumes and returns the restart request flag.
     *
     * @return {@code true} if a restart was requested since the last update
     */
    public boolean processRestart() {
        boolean v = restartRequested;
        restartRequested = false;
        return v;
    }

    /**
     * Consumes and returns the shoot request flag.
     *
     * @return {@code true} if a shot was requested since the last update
     */
    public boolean processShoot() {
        boolean v = shootRequested;
        shootRequested = false;
        return v;
    }

    /**
     * Returns whether a pause was requested and consumes the request.
     *
     * @return {@code true} if pause was requested since the last call
     */
    public boolean processPause() {
        boolean v = pauseRequested;
        pauseRequested = false;
        return v;
    }

    /**
     * Clears all input state.
     *
     * <p>Resets pressed keys and one-shot action flags, typically used
     * when pausing or resetting the game.</p>
     */
    public void clearAll() {
        for (int i = 0; i < keysPressed.length; i++) {
            keysPressed[i] = false;
        }
        jumpRequested = false;
        shootRequested = false;
        restartRequested = false;
    }

    /**
     * Returns the current movement key state array.
     *
     * @return boolean array representing held movement keys
     */
    public boolean[] getKeysPressed() {
        return keysPressed;
    }

    /**
     * Returns the last horizontal movement direction.
     *
     * <p>Used to determine which way the player sprite should face when idle
     * or shooting.</p>
     *
     * @return {@code true} if last direction was right, {@code false} if left
     */
    public boolean isLastDirectionForwards() {
        return lastDirectionForwards;
    }

    /**
     * Handles a JavaFX key press event.
     *
     * <p>Updates held key state and sets one-shot action flags where
     * appropriate.</p>
     *
     * <ul>
     *   <li>A → move left</li>
     *   <li>D → move right</li>
     *   <li>W → jump</li>
     *   <li>SPACE → shoot</li>
     *   <li>ESCAPE → restart</li>
     *   <li>ENTER → exit game</li>
     * </ul>
     *
     * @param code the {@link KeyCode} that was pressed
     */
    public void onKeyPressed(KeyCode code) {
        switch (code) {
            case A -> {
                keysPressed[0] = true;
                lastDirectionForwards = false;
            }
            case D -> {
                keysPressed[1] = true;
                lastDirectionForwards = true;
            }
            case W -> {
                if (!keysPressed[2]) jumpRequested = true;
                keysPressed[2] = true;
            }
            case R -> restartRequested = true;
            case SPACE -> shootRequested = true;
            case ESCAPE -> pauseRequested = true;
            case ENTER -> System.exit(0);
            default -> { }
        }
    }

    /**
     * Handles a JavaFX key release event.
     *
     * <p>Updates held key state so movement stops when keys are released.</p>
     *
     * @param code the {@link KeyCode} that was released
     */
    public void onKeyReleased(KeyCode code) {
        switch (code) {
            case A -> keysPressed[0] = false;
            case D -> keysPressed[1] = false;
            case W -> keysPressed[2] = false;
            case SPACE -> keysPressed[3] = false; // currently unused, reserved
            default -> { }
        }
    }

}
