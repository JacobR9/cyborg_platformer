import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class GameControllerTest {

    private GameController controller;
    private Game game;
    private ScoreManager scoreManager;
    private LeaderboardService leaderboard;
    private InputHandler input;

    @BeforeEach
    void setUp() {
        game = mock(Game.class);
        scoreManager = mock(ScoreManager.class);
        leaderboard = mock(LeaderboardService.class);
        input = mock(InputHandler.class);

        when(game.getInputHandler()).thenReturn(input);
        controller = new GameController(game, scoreManager, leaderboard);
    }

    @Test
    void constructorRejectsNulls() {
        assertThrows(NullPointerException.class, () -> new GameController(null, scoreManager, leaderboard));
        assertThrows(NullPointerException.class, () -> new GameController(game, null, leaderboard));
        assertThrows(NullPointerException.class, () -> new GameController(game, scoreManager, null));
    }

    @Test
    void startNewGameSetsRunningAndResetsSystems() {
        controller.startNewGame();

        assertEquals(GameState.RUNNING, controller.getState());
        verify(scoreManager).reset();
        verify(game).resetRunStats();
        verify(game).init();
    }

    @Test
    void updateDoesNothingWhenNotRunning() {
        //default state is MENU
        when(input.processPause()).thenReturn(false);

        controller.update(1.0);

        verify(game, never()).update();
        verify(scoreManager, never()).update(anyDouble());
        verify(scoreManager, never()).onDeath();
        verify(scoreManager, never()).onKill();
        verify(leaderboard, never()).save();
    }

    @Test
    void pauseFromRunningSetsPausedAndClearsInputs() {
        controller.startNewGame();
        reset(input);
        controller.pause();

        assertEquals(GameState.PAUSED, controller.getState());
        verify(input).clearAll();
    }

    @Test
    void pauseDoesNothingIfNotRunning() {
        //state MENU
        controller.pause();
        assertEquals(GameState.MENU, controller.getState());
        verify(input, never()).clearAll();
    }

    @Test
    void updateWhenRunningCallsGameAndScoreTick() {
        controller.startNewGame();
        when(input.processPause()).thenReturn(false);
        when(game.getDeathCounter()).thenReturn(0);
        when(game.getKillCounter()).thenReturn(0);
        when(game.isWon()).thenReturn(false);

        controller.update(0.5);

        verify(game).update();
        verify(scoreManager).update(0.5);
        verify(leaderboard, never()).save();
        assertEquals(GameState.RUNNING, controller.getState());
    }

    @Test
    void updateSyncsDeathsCallsOnDeathOncePerNewDeath() {
        controller.startNewGame();
        when(input.processPause()).thenReturn(false);
        when(game.getKillCounter()).thenReturn(0);
        when(game.isWon()).thenReturn(false);

        //first update sees 2 deaths
        when(game.getDeathCounter()).thenReturn(2);
        controller.update(0.1);
        verify(scoreManager, times(2)).onDeath();

        //Next update still 2 deaths
        controller.update(0.1);
        verify(scoreManager, times(2)).onDeath();
    }

    @Test
    void updateSyncsKillsCallsOnKillOncePerNewKill() {
        controller.startNewGame();
        when(input.processPause()).thenReturn(false);
        when(game.getDeathCounter()).thenReturn(0);
        when(game.isWon()).thenReturn(false);

        when(game.getKillCounter()).thenReturn(3);
        controller.update(0.1);
        verify(scoreManager, times(3)).onKill();

        controller.update(0.1);
        verify(scoreManager, times(3)).onKill();
    }

    @Test
    void updateWhenWonSetsGameOverAndSavesLeaderboard() {
        controller.startNewGame();
        when(input.processPause()).thenReturn(false);
        when(game.getDeathCounter()).thenReturn(0);
        when(game.getKillCounter()).thenReturn(0);
        when(game.isWon()).thenReturn(true);

        controller.update(0.1);

        assertEquals(GameState.GAME_OVER, controller.getState());
        verify(leaderboard).save();
    }

    @Test
    void pressingPauseDuringUpdateTogglesRunningToPaused() {
        controller.startNewGame();
        reset(input);
        when(input.processPause()).thenReturn(true);
        controller.update(0.1);

        assertEquals(GameState.PAUSED, controller.getState());
        verify(input).clearAll();
        verify(game, never()).update(); // because state becomes PAUSED before game.update()
    }

    @Test
    void pressingPauseAgainDuringUpdateResumesFromPaused() {
        controller.startNewGame();

        //toggle to PAUSED
        when(input.processPause()).thenReturn(true);
        controller.update(0.1);
        assertEquals(GameState.PAUSED, controller.getState());

        //toggle to RUNNING
        when(input.processPause()).thenReturn(true);
        controller.update(0.1);
        assertEquals(GameState.RUNNING, controller.getState());
    }

    @Test
    void resumeOffsetsStartTimeByPausedDuration() throws Exception {
        controller.startNewGame();
        controller.pause();

        double before = Game.startTime;

        // Avoid sleeping by forcing pauseStartedMs to an older value via reflection
        Field f = GameController.class.getDeclaredField("pauseStartedMs");
        f.setAccessible(true);
        f.setLong(controller, System.currentTimeMillis() - 250);

        controller.resume();

        assertEquals(GameState.RUNNING, controller.getState());
        assertTrue(Game.startTime > before, "startTime should have been pushed forward by pause duration");
    }
}
