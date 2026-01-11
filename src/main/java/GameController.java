import static java.util.Objects.requireNonNull;

/**
 * Controls high-level game flow and state transitions.
 *
 * <p>Acts as the controller in the MVC architecture, coordinating
 * updates between {@link Game}, {@link ScoreManager}, and
 * {@link LeaderboardService}.</p>
 */
public class GameController {

    /** Core game model. */
    private final Game game;

    /** Manages scoring and run statistics. */
    private final ScoreManager scoreManager;

    /** Persistent leaderboard service. */
    private final LeaderboardService leaderboard;

    /** Current high-level game state. */
    private GameState state = GameState.MENU;

    /** Timestamp when the game was paused (ms). */
    private long pauseStartedMs = -1;

    /** Last synced death count from the game. */
    private int lastDeaths = 0;

    /** Last synced kill count from the game. */
    private int lastKills = 0;

    /**
     * Creates a new game controller.
     *
     * @param game core game model
     * @param scoreManager score tracker
     * @param leaderboard leaderboard persistence service
     */
    public GameController(Game game, ScoreManager scoreManager, LeaderboardService leaderboard) {
        this.game = requireNonNull(game);
        this.scoreManager = requireNonNull(scoreManager);
        this.leaderboard = requireNonNull(leaderboard);
    }

    /**
     * Starts a fresh game run and resets all state.
     */
    public void startNewGame() {
        lastDeaths = 0;
        lastKills = 0;
        scoreManager.reset();
        state = GameState.RUNNING;
        game.getInputHandler().clearAll();
        game.resetRunStats();
        Game.startTime = System.currentTimeMillis();
        game.init();
    }

    /**
     * @return the active game instance
     */
    public Game getGame() {
        return game;
    }

    /**
     * Updates the game based on the current state.
     *
     * @param interval time step in seconds
     */
    public void update(double interval) {
        if (game.getInputHandler().processPause()) {
            if (state == GameState.RUNNING) {
                pause();
            } else if (state == GameState.PAUSED) {
                resume();
            }
        }

        if (state != GameState.RUNNING) return;

        game.update();
        scoreManager.update(interval);

        // Sync deaths
        int d = game.getDeathCounter();
        while (lastDeaths < d) {
            scoreManager.onDeath();
            lastDeaths++;
        }

        // Sync kills
        int k = game.getKillCounter();
        while (lastKills < k) {
            scoreManager.onKill();
            lastKills++;
        }

        if (game.isWon()) {
            state = GameState.GAME_OVER;
            game.getInputHandler().clearAll();
            leaderboard.save();
        }
    }

    /**
     * Pauses the game and clears active input.
     */
    public void pause() {
        if (state != GameState.RUNNING) return;
        state = GameState.PAUSED;
        pauseStartedMs = System.currentTimeMillis();
        game.getInputHandler().clearAll();
    }

    /**
     * Toggles between paused and running states.
     */
    public void togglePause() {
        if (state == GameState.RUNNING) {
            pause();
        } else if (state == GameState.PAUSED) {
            resume();
        }
    }

    /**
     * Resumes gameplay after a pause.
     */
    public void resume() {
        if (state != GameState.PAUSED) return;
        long now = System.currentTimeMillis();
        if (pauseStartedMs > 0) {
            Game.startTime += (now - pauseStartedMs);
            pauseStartedMs = -1;
        }
        state = GameState.RUNNING;
    }

    /**
     * Restarts the game from the beginning.
     */
    public void restart() {
        startNewGame();
    }

    /**
     * @return the current game state
     */
    public GameState getState() {
        return state;
    }

    /**
     * Forces the game into a game-over state.
     */
    public void forceGameOver() {
        state = GameState.GAME_OVER;
    }
}
