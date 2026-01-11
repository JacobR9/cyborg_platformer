import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreManagerTest {

    private ScoreManager sm;

    @BeforeEach
    void setUp() {
        sm = new ScoreManager();
    }

    @Test
    void initialStateScoreIsZeroUntilFirstRecalcTrigger() {
        // ScoreManager starts with score=0 and does not recalc until update/onKill/onDeath/reset called
        assertEquals(0, sm.getScore());
        assertEquals(0.0, sm.getElapsedSeconds(), 1e-9);
        assertEquals(0, sm.getKills());
        assertEquals(0, sm.getDeaths());
    }

    @Test
    void updateAddsElapsedTimeAndRecalculatesScore() {
        sm.update(1.0);

        assertEquals(1.0, sm.getElapsedSeconds(), 1e-9);
        // At t=1, timeScore = round(10000 * 120/1) = 1,200,000
        assertEquals(1_200_000, sm.getScore());
    }

    @Test
    void updateWithZeroDoesNotIncreaseElapsedButStillRecalculates() {
        sm.update(0.0);

        assertEquals(0.0, sm.getElapsedSeconds(), 1e-9);
        // elapsedSeconds=0 -> t=max(1,0)=1 -> timeScore still 1,200,000
        assertEquals(1_200_000, sm.getScore());
    }

    @Test
    void updateWithNegativeDoesNotDecreaseElapsedButRecalculates() {
        sm.update(2.0);
        assertEquals(2.0, sm.getElapsedSeconds(), 1e-9);

        sm.update(-5.0); // ignored for elapsedSeconds, but recalc still happens

        assertEquals(2.0, sm.getElapsedSeconds(), 1e-9);
        // timeScore at t=2 -> round(10000*120/2)=600,000
        assertEquals(600_000, sm.getScore());
    }

    @Test
    void onKillIncrementsKillsAndAddsKillPoints() {
        sm.update(2.0); // lock time at 2s -> timeScore 600,000
        assertEquals(600_000, sm.getScore());

        sm.onKill();
        assertEquals(1, sm.getKills());
        // 600,000 + 150
        assertEquals(600_150, sm.getScore());

        sm.onKill();
        assertEquals(2, sm.getKills());
        // 600,000 + 300
        assertEquals(600_300, sm.getScore());
    }

    @Test
    void onDeathIncrementsDeathsAndAppliesPenalty() {
        sm.update(2.0); // 600,000 baseline
        sm.onDeath();

        assertEquals(1, sm.getDeaths());
        // 600,000 - 250
        assertEquals(599_750, sm.getScore());

        // Force many deaths; score should never go negative due to max(0, ...)
        for (int i = 0; i < 1_000_000; i++) {
            sm.onDeath();
            if (sm.getScore() == 0) break;
        }
        assertTrue(sm.getScore() >= 0);
    }

    @Test
    void resetClearsCountersAndScoreAndElapsed() {
        sm.update(5.0);
        sm.onKill();
        sm.onDeath();

        assertNotEquals(0.0, sm.getElapsedSeconds(), 1e-9);
        assertTrue(sm.getKills() > 0 || sm.getDeaths() > 0 || sm.getScore() > 0);

        sm.reset();

        assertEquals(0, sm.getScore());
        assertEquals(0.0, sm.getElapsedSeconds(), 1e-9);
        assertEquals(0, sm.getKills());
        assertEquals(0, sm.getDeaths());
    }

    @Test
    void scoreDecreasesAsTimeIncreasesIfKillsDeathsUnchanged() {
        sm.update(1.0);
        int at1 = sm.getScore(); // 1,200,000

        sm.update(1.0); // total 2.0s
        int at2 = sm.getScore(); // 600,000

        assertTrue(at2 < at1);
    }
}
