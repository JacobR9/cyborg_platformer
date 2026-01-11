/**
 * Tracks and calculates the player's score for the current run.
 *
 * <p>Score is based on time survived, kills, and deaths (penalties).
 * Call {@link #update(double)} each tick, and notify events via
 * {@link #onKill()} and {@link #onDeath()}.</p>
 */
public class ScoreManager {

    /** Current total score (clamped to 0+). */
    private int score = 0;

    /** Total time elapsed in seconds for the current run. */
    private double elapsedSeconds = 0.0;

    /** Number of enemies defeated this run. */
    private int kills = 0;

    /** Number of player deaths this run. */
    private int deaths = 0;

    /** Target completion time in seconds used for time scoring. */
    private static final double PAR_TIME = 120.0;

    /** Maximum score awarded for time component at par time. */
    private static final int MAX_TIME_SCORE = 10_000;

    /** Points awarded per kill. */
    private static final int KILL_POINTS = 150;

    /** Points subtracted per death. */
    private static final int DEATH_PENALTY = 250;

    /**
     * Resets all score state for a new run.
     */
    public void reset() {
        score = 0;
        elapsedSeconds = 0.0;
        kills = 0;
        deaths = 0;
    }

    /**
     * Updates elapsed time and recalculates score.
     *
     * @param deltaSeconds time step in seconds
     */
    public void update(double deltaSeconds) {
        if (deltaSeconds > 0) elapsedSeconds += deltaSeconds;
        recalc();
    }

    /**
     * Records an enemy kill and recalculates score.
     */
    public void onKill() {
        kills++;
        recalc();
    }

    /**
     * Records a player death and recalculates score.
     */
    public void onDeath() {
        deaths++;
        recalc();
    }

    /**
     * Recomputes score from current time, kills, and deaths.
     */
    private void recalc() {
        double t = Math.max(1.0, elapsedSeconds);

        double factor = PAR_TIME / t;
        int timeScore = (int) Math.round(MAX_TIME_SCORE * factor);

        int killScore = kills * KILL_POINTS;
        int penalty = deaths * DEATH_PENALTY;

        score = Math.max(0, timeScore + killScore - penalty);
    }

    /** @return current score */
    public int getScore() { return score; }

    /** @return elapsed time in seconds */
    public double getElapsedSeconds() { return elapsedSeconds; }

    /** @return number of kills */
    public int getKills() { return kills; }

    /** @return number of deaths */
    public int getDeaths() { return deaths; }
}

