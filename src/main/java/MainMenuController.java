/**
 * Controller for the main menu.
 *
 * <p>Handles user actions from the menu and applies them to
 * game state and settings.</p>
 */
public class MainMenuController {

    /** Controls overall game flow. */
    private final GameController gameController;

    /** Shared game settings. */
    private final GameSettings settings;

    /**
     * Creates a controller for the main menu.
     *
     * @param gameController main game controller
     * @param settings game settings to update
     */
    public MainMenuController(GameController gameController, GameSettings settings) {
        this.gameController = gameController;
        this.settings = settings;
    }

    /**
     * Starts a new game.
     */
    public void onStartGame() {
        gameController.startNewGame();
    }

    /**
     * Enables or disables background music.
     *
     * @param enabled {@code true} to enable music
     */
    public void setMusicEnabled(boolean enabled) {
        settings.setMusicEnabled(enabled);
    }

    /**
     * Updates the UI text scale setting.
     *
     * @param scale scale factor to apply
     */
    public void setTextScale(double scale) {
        settings.setTextScale(scale);
    }
}
