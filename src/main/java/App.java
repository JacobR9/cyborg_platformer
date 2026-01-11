import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyCode;
import java.io.File;
import javafx.scene.effect.GaussianBlur;

/**
 * JavaFX application entry point for the Cyborg Platform game.
 *
 * <p>Responsible for bootstrapping the UI, switching between menus and gameplay,
 * and starting the main game loop.</p>
 */
public class App extends Application {

    /** Fixed viewport width in pixels. */
    private static final int W = 1280;

    /** Fixed viewport height in pixels. */
    private static final int H = 720;

    /** Main animation timer driving the game loop. */
    private AnimationTimer timer;

    /** User-configurable game settings. */
    private GameSettings settings;

    /** Background music and sound controller. */
    private AudioManager audio;

    /** Primary application stage. */
    private Stage stage;

    /** Controls game state and updates. */
    private GameController controller;


    /**
     * Initialises application-wide resources.
     *
     * <p>This method is called exactly once by the JavaFX runtime
     * before {@link #start(Stage)}.
     * <p>No JavaFX UI components are created here, as this method
     * runs off the JavaFX Application Thread.</p>
     */
    @Override
    public void init() {
        settings = new GameSettings();
        audio = new AudioManager("/Audio/Menumusic.mp3");
        audio.setVolume(0.6);
    }

    /**
     * Called by JavaFX when the application starts.
     *
     * @param stage primary window provided by JavaFX
     */
    @Override
    public void start(Stage stage) {
        if (settings == null) settings = new GameSettings();
        if (audio == null) {
            audio = new AudioManager("/Audio/Menumusic.mp3");
            audio.setVolume(0.6);
        }

        this.stage = stage;
        stage.setTitle("Cyborg Platform");
        stage.setResizable(false);

        // Apply current setting whenever menu is shown
        if (settings.isMusicEnabled()) audio.play();
        else audio.pause();

        Game game = new Game();
        game.loadImages();
        game.init();

        MainMenu menu = new MainMenu(game.getFont(), settings, audio);
        Scene scene = new Scene(menu, W, H);

        menu.setOnStartGame(this::startGame);

        stage.setScene(scene);
        menu.playBackground();
        stage.show();
    }

    /**
     * Starts a new gameplay session and replaces the main menu.
     */
    private void startGame() {
        if (timer != null) timer.stop();

        if (stage.getScene() != null && stage.getScene().getRoot() instanceof MainMenu menu) {
            menu.stopBackground();
        }

        Game game = new Game();
        game.loadImages();

        // Score and leaderboard setup
        ScoreManager scoreManager = new ScoreManager();
        final ScoreEntry[] lastSaved = { null };

        String leaderboardPath =
                System.getProperty("user.home")
                        + File.separator
                        + ".cyborg-platform"
                        + File.separator
                        + "leaderboard.dat";

        LeaderboardService leaderboard = new LeaderboardService(leaderboardPath);
        leaderboard.load();

        controller = new GameController(game, scoreManager, leaderboard);
        controller.startNewGame();

        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GaussianBlur blur = new GaussianBlur(12);

        // Pause menu overlay
        PauseMenuView pauseView =
                new PauseMenuView(controller.getGame().getFont(), settings);
        pauseView.setId("pauseView");
        pauseView.setVisible(false);

        // End-of-game screen
        EndView endView = new EndView(controller.getGame().getFont(), settings);
        endView.setVisible(false);
        endView.getLeaderboardView().setScale(settings.getTextScale() * 0.65);

        final boolean[] savedThisRun = { false };

        // Save score button
        endView.getSaveButton().setOnAction(e -> {
            if (savedThisRun[0]) {
                endView.setStatus("Already saved for this run");
                return;
            }

            String name = endView.getEnteredName();
            int score = scoreManager.getScore();

            ScoreEntry entry = new ScoreEntry(name, score);
            leaderboard.addEntry(name, score);
            boolean ok = leaderboard.save();
            lastSaved[0] = entry;

            endView.getLeaderboardView()
                    .setEntries(leaderboard.getEntries(), 10, null);
            endView.setStatus(ok ? "Saved!" : "Save failed");

            savedThisRun[0] = true;
            endView.getSaveButton().setDisable(true);
        });

        // Root layout containing game and overlays
        StackPane root = new StackPane(canvas, pauseView, endView);
        Scene scene = new Scene(root, W, H);
        stage.setScene(scene);
        stage.show();

        // Pause menu actions
        pauseView.resumeButton().setOnAction(e -> controller.resume());
        pauseView.restartButton().setOnAction(e -> {
            savedThisRun[0] = false;
            endView.getSaveButton().setDisable(false);
            controller.startNewGame();
            canvas.requestFocus();
        });

        pauseView.menuButton().setOnAction(e -> start(stage));

        // End screen actions
        endView.setOnRestart(() -> {
            savedThisRun[0] = false;
            lastSaved[0] = null;
            endView.resetUi();
            endView.getSaveButton().setDisable(false);
            controller.startNewGame();
            endView.hide();
            canvas.requestFocus();
        });

        endView.setOnBackToMenu(() -> {
            if (timer != null) timer.stop();
            start(stage);
        });

        // Keyboard input handling
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                controller.togglePause();
                e.consume();
                return;
            }

            // Debug keys (remove for release)
            if (e.getCode() == KeyCode.F8) {
                leaderboard.clear();
                endView.getLeaderboardView()
                        .setEntries(leaderboard.getEntries(), 10, null);
                endView.setStatus("Leaderboard reset");
                e.consume();
                return;
            }

            if (e.getCode() == KeyCode.F9) {
                controller.forceGameOver();
                e.consume();
                return;
            }

            if (controller.getState() == GameState.RUNNING) {
                controller.getGame().getInputHandler()
                        .onKeyPressed(e.getCode());
            }
        });

        scene.setOnKeyReleased(e -> {
            if (controller.getState() == GameState.RUNNING) {
                controller.getGame().getInputHandler()
                        .onKeyReleased(e.getCode());
            }
        });

        canvas.setFocusTraversable(true);
        canvas.requestFocus();

        timer = new AnimationTimer() {

            /** Fixed update step for 60Hz simulation. */
            private static final long STEP = 1_000_000_000L / 60;

            /** Timestamp of the previous frame. */
            private long last = 0;

            /** Accumulated unprocessed time. */
            private long acc = 0;

            /**
             * Called once per frame by JavaFX.
             *
             * @param now current time in nanoseconds
             */
            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }

                acc += (now - last);
                last = now;

                while (acc >= STEP) {
                    controller.update(STEP / 1_000_000_000.0);
                    acc -= STEP;
                }

                boolean paused = controller.getState() == GameState.PAUSED;
                boolean gameOver = controller.getState() == GameState.GAME_OVER;

                pauseView.setVisible(paused);
                endView.setVisible(gameOver);
                canvas.setEffect((paused || gameOver) ? blur : null);

                if (gameOver) {
                    endView.setScore(scoreManager.getScore());
                    endView.getLeaderboardView()
                            .setEntries(leaderboard.getEntries(), 10, lastSaved[0]);
                    endView.runStats(
                            scoreManager.getElapsedSeconds(),
                            scoreManager.getKills(),
                            scoreManager.getDeaths()
                    );
                }

                FxRenderer.draw(gc, controller.getGame(), settings, W, H);
            }
        };

        timer.start();
    }

    /**
     * Standard JavaFX entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
