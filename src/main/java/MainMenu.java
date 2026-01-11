import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;

/**
 * Main menu view displayed when the application starts.
 *
 * <p>Provides options to start the game, quit the application,
 * toggle music, and adjust text scaling.</p>
 */
public class MainMenu extends StackPane {

    /** Callback executed when "Start Game" is pressed. */
    private Runnable onStartGame;

    /** Font used throughout the menu. */
    private final Font gameFont;

    /** Shared game settings instance. */
    private final GameSettings settings;

    /** Audio controller for menu music. */
    private AudioManager audio;

    /** Media player for the animated background. */
    private MediaPlayer mp;

    /**
     * Creates the main menu UI.
     *
     * @param gameFont font used for menu text
     * @param settings game settings to read and update
     * @param audio audio manager for background music
     */
    public MainMenu(Font gameFont, GameSettings settings, AudioManager audio) {
        this.gameFont = gameFont;
        this.settings = settings;
        this.audio = audio;

        MediaView background = createBlurredGifBackground();
        VBox menuContent = createMenuContent();

        getChildren().addAll(background, menuContent);
    }

    /**
     * Sets the action to run when the user starts the game.
     *
     * @param onStartGame callback to execute
     */
    public void setOnStartGame(Runnable onStartGame) {
        this.onStartGame = onStartGame;
    }

    /**
     * Creates and starts the blurred animated background.
     *
     * @return media view displaying the background animation
     */
    private MediaView createBlurredGifBackground() {
        setStyle("-fx-background-color: black;");

        Media bg = new Media(
                getClass().getResource("/Gifs/Start.mp4").toExternalForm()
        );

        mp = new MediaPlayer(bg);
        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.setMute(true);
        mp.play();

        MediaView mv = new MediaView(mp);
        mv.setPreserveRatio(false);
        mv.fitWidthProperty().bind(widthProperty());
        mv.fitHeightProperty().bind(heightProperty());
        mv.setEffect(new GaussianBlur(18));

        return mv;
    }

    /**
     * Builds the menu controls and layout.
     *
     * @return VBox containing menu UI elements
     */
    private VBox createMenuContent() {
        Label title = new Label("Cyborg Platform");
        Label textLabel = new Label("Text Size");

        CheckBox musicToggle = new CheckBox("Music");
        musicToggle.setSelected(settings.isMusicEnabled());
        musicToggle.selectedProperty().addListener((obs, was, isOn) -> {
            settings.setMusicEnabled(isOn);
            if (isOn) audio.play();
            else audio.pause();
        });

        Button start = new Button("Start Game");
        start.setOnAction(e -> {
            if (onStartGame != null) onStartGame.run();
        });

        Button quit = new Button("Quit");
        quit.setOnAction(e -> getScene().getWindow().hide());

        Slider textSize = new Slider(0.75, 1.5, settings.getTextScale());
        textSize.setShowTickLabels(true);
        textSize.setShowTickMarks(true);
        textSize.setMaxWidth(220);
        textSize.valueProperty().addListener((obs, oldV, newV) -> {
            double scale = newV.doubleValue();
            settings.setTextScale(scale);
            applyMenuScale(title, textLabel, musicToggle, start, quit, scale);
        });

        VBox box = new VBox(16, title, textLabel, textSize, musicToggle, start, quit);
        box.setAlignment(Pos.CENTER);

        start.setMinWidth(220);
        quit.setMinWidth(220);

        applyMenuScale(title, textLabel, musicToggle, start, quit, settings.getTextScale());

        return box;
    }

    /**
     * Applies the current text scale to all menu controls.
     *
     * @param title menu title label
     * @param textLabel label for text size slider
     * @param musicToggle music toggle checkbox
     * @param start start game button
     * @param quit quit button
     * @param scale scale factor to apply
     */
    private void applyMenuScale(
            Label title,
            Label textLabel,
            CheckBox musicToggle,
            Button start,
            Button quit,
            double scale
    ) {
        title.setFont(Font.font(gameFont.getFamily(), 48 * scale));
        textLabel.setFont(Font.font(gameFont.getFamily(), 16 * scale));
        musicToggle.setFont(Font.font(gameFont.getFamily(), 18 * scale));
        start.setFont(Font.font(gameFont.getFamily(), 18 * scale));
        quit.setFont(Font.font(gameFont.getFamily(), 18 * scale));
    }

    /**
     * Starts background animation playback.
     */
    public void playBackground() {
        if (mp != null) mp.play();
    }

    /**
     * Stops background animation playback.
     */
    public void stopBackground() {
        if (mp != null) {
            mp.stop();
            mp.dispose();
            mp = null;
        }
    }
}

