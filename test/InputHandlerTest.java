import static org.junit.jupiter.api.Assertions.*;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputHandlerTest {

    private InputHandler input;

    @BeforeEach
    void setUp() {
        input = new InputHandler();
    }

    @Test
    void testMovementKeys() {
        boolean[] keys = input.getKeysPressed();

        // Press A (left)
        input.onKeyPressed(KeyCode.A);
        assertTrue(keys[0], "A should set move-left true");
        assertFalse(input.isLastDirectionForwards(), "A should set facing to left (false)");

        // Release A
        input.onKeyReleased(KeyCode.A);
        assertFalse(keys[0], "Releasing A should set move-left false");

        // Press D (right)
        input.onKeyPressed(KeyCode.D);
        assertTrue(keys[1], "D should set move-right true");
        assertTrue(input.isLastDirectionForwards(), "D should set facing to right (true)");

        // Release D
        input.onKeyReleased(KeyCode.D);
        assertFalse(keys[1], "Releasing D should set move-right false");
    }

    @Test
    void testJumpOneShot() {
        boolean[] keys = input.getKeysPressed();

        // First press should request jump
        input.onKeyPressed(KeyCode.W);
        assertTrue(keys[2], "W should set jump-held true");
        assertTrue(input.processJump(), "First processJump() after press should be true");
        assertFalse(input.processJump(), "Second processJump() without re-press should be false");

        // While still held, repeated keyPressed events should NOT re-request jump
        input.onKeyPressed(KeyCode.W);
        assertFalse(input.processJump(), "Holding W should not re-trigger jump request");

        // Release then press again -> should request jump again
        input.onKeyReleased(KeyCode.W);
        assertFalse(keys[2], "Releasing W should set jump-held false");

        input.onKeyPressed(KeyCode.W);
        assertTrue(input.processJump(), "Pressing W again after release should trigger jump request");
    }

    @Test
    void testShootOneShot() {
        input.onKeyPressed(KeyCode.SPACE);
        assertTrue(input.processShoot(), "First processShoot() after SPACE should be true");
        assertFalse(input.processShoot(), "Second processShoot() without another press should be false");

        // Press again -> should trigger again
        input.onKeyPressed(KeyCode.SPACE);
        assertTrue(input.processShoot(), "Pressing SPACE again should trigger shoot request again");
    }

    @Test
    void testRestartOneShot() {
        input.onKeyPressed(KeyCode.R);
        assertTrue(input.processRestart(), "First processRestart() after ESC should be true");
        assertFalse(input.processRestart(), "Second processRestart() without another press should be false");

        // Press again -> should trigger again
        input.onKeyPressed(KeyCode.R);
        assertTrue(input.processRestart(), "Pressing ESC again should trigger restart request again");
    }
}
