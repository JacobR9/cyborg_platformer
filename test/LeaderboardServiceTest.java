import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LeaderboardServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void saveReturnsTrueAndCreatesFile() {
        Path file = tempDir.resolve("leaderboard.dat");
        LeaderboardService lb = new LeaderboardService(file.toString());

        lb.addEntry("Player", 100);

        assertTrue(lb.save(), "save() should return true on success");
        assertTrue(file.toFile().exists(), "leaderboard file should exist after save()");
    }

    @Test
    void loadRestoresSavedEntries() {
        Path file = tempDir.resolve("leaderboard.dat");

        LeaderboardService lb1 = new LeaderboardService(file.toString());
        lb1.addEntry("A", 200);
        lb1.addEntry("B", 50);
        assertTrue(lb1.save());

        LeaderboardService lb2 = new LeaderboardService(file.toString());
        lb2.load();

        List<ScoreEntry> entries = lb2.getEntries();
        assertEquals(2, entries.size());
        assertEquals("A", entries.get(0).name());
        assertEquals(200, entries.get(0).score());
        assertEquals("B", entries.get(1).name());
        assertEquals(50, entries.get(1).score());
    }

    @Test
    void getEntriesReturnsSortedCopyDescending() {
        Path file = tempDir.resolve("leaderboard.dat");
        LeaderboardService lb = new LeaderboardService(file.toString());

        lb.addEntry("Low", 10);
        lb.addEntry("High", 999);
        lb.addEntry("Mid", 100);

        List<ScoreEntry> sorted = lb.getEntries();
        assertEquals(List.of("High", "Mid", "Low"),
                sorted.stream().map(ScoreEntry::name).toList());

        sorted.clear();
        assertEquals(3, lb.getEntries().size());
    }

    @Test
    void loadDoesNothingIfFileMissing() {
        Path file = tempDir.resolve("missing.dat");
        LeaderboardService lb = new LeaderboardService(file.toString());
        lb.load();
        assertTrue(lb.getEntries().isEmpty());
    }

    @Test
    void clearEmptiesEntriesAndPersists() {
        Path file = tempDir.resolve("leaderboard.dat");

        LeaderboardService lb = new LeaderboardService(file.toString());
        lb.addEntry("P1", 123);
        lb.addEntry("P2", 456);
        assertTrue(lb.save());

        lb.clear();
        assertTrue(lb.getEntries().isEmpty(), "clear() should empty in-memory entries");

        LeaderboardService lb2 = new LeaderboardService(file.toString());
        lb2.load();
        assertTrue(lb2.getEntries().isEmpty(), "clear() should persist empty list to disk");
    }
}
