import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
public class AppIntegrationTest extends ApplicationTest {

    private Stage stageUnderTest;

    @Override
    public void start(Stage stage) throws Exception {
        this.stageUnderTest = stage;
        new App().start(stage);
    }

    @Test
    void testAppBoots() {
        assertNotNull(stageUnderTest);
        assertEquals("Cyborg Platform", stageUnderTest.getTitle());
        assertFalse(stageUnderTest.isResizable());

        Scene scene = stageUnderTest.getScene();
        assertNotNull(scene);

        // App dimensions set to 1280x720
        assertEquals(1280.0, scene.getWidth(), 0.001);
        assertEquals(720.0, scene.getHeight(), 0.001);
    }

    @Test
    void testGameSceneContainsCanvas() {
        assertTrue(stageUnderTest.getScene().getRoot() instanceof MainMenu);
        clickOn("Start Game");

        StackPane root = (StackPane) stageUnderTest.getScene().getRoot();
        assertTrue(root.getChildren().get(0) instanceof Canvas);
    }

    @Test
    void testKeyHandlersConnected() {
        FxRobot robot = new FxRobot();
        robot.press(KeyCode.A).release(KeyCode.A);
        robot.press(KeyCode.D).release(KeyCode.D);
        robot.press(KeyCode.SPACE).release(KeyCode.SPACE);
        robot.press(KeyCode.ESCAPE).release(KeyCode.ESCAPE);
    }
}
