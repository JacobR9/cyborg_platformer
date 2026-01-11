import static org.junit.jupiter.api.Assertions.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;

@Tag("integration")
public class AppMenuTest extends ApplicationTest {

    private Stage stage;

    @Test
    void resourcesAreAvailable() {
        assertNotNull(getClass().getResource("/Gifs/Start.mp4"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        App app = new App();
        app.init();
        app.start(stage);
    }


    @Test
    void testAppShowsMainMenu() {
        assertNotNull(stage.getScene());
        assertInstanceOf(MainMenu.class, stage.getScene().getRoot());
    }

    @Test
    void clickingStartGameShowsGameView() {
        clickOn("Start Game");

        Scene scene = stage.getScene();
        Parent root = scene.getRoot();

        assertTrue(root instanceof StackPane);
        StackPane stack = (StackPane) root;
        assertEquals(3, stack.getChildren().size());
    }

    @Test
    void escapeShowsPauseMenu() {
        clickOn("Start Game");
        WaitForAsyncUtils.waitForFxEvents();
        push(KeyCode.ESCAPE);
        WaitForAsyncUtils.waitForFxEvents();

        StackPane root = (StackPane) stage.getScene().getRoot();

        boolean pauseVisible = root.getChildren().stream().anyMatch(n -> "pauseView".equals(n.getId()) && n.isVisible());

        assertTrue(pauseVisible, "Pause view should be visible after pressing ESC");
    }



}