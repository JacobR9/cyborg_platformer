import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class MainMenuControllerTest {

    private MainMenuController controller;
    private GameController gameController;
    private GameSettings settings;

    @BeforeEach
    void setUp() {
        settings = new GameSettings();
        gameController = mock(GameController.class);
        controller = new MainMenuController(gameController, settings);
    }

    @Test
    void testStartGameInitialisesGame() {
        controller.onStartGame();
        verify(gameController).startNewGame();
    }

    @Test
    void testMusicToggleOn() {
        controller.setMusicEnabled(true);
        assertTrue(settings.isMusicEnabled());
    }

    @Test
    void testMusicToggleOff() {
        controller.setMusicEnabled(false);
        assertFalse(settings.isMusicEnabled());
    }

    @Test
    void testTextSizeStored() {
        controller.setTextScale(1.5);
        assertEquals(1.5, settings.getTextScale());
    }
}
