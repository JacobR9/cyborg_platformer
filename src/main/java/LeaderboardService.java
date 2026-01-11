import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.util.*;


public class LeaderboardService {
    private final File file;
    private final List<ScoreEntry> entries = new ArrayList<>();

    public LeaderboardService(String filePath) {
        this.file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public void addEntry(String name, int score) {
        entries.add(new ScoreEntry(name, score));
    }

    public List<ScoreEntry> getEntries() {
        List<ScoreEntry> copy = new ArrayList<>(entries);
        copy.sort((a, b) -> Integer.compare(b.score(), a.score()));
        return copy;
    }

    public boolean save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(entries));
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void load() {
        if (!file.exists()) return;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            entries.clear();
            List<ScoreEntry> loaded = (List<ScoreEntry>) in.readObject();
            entries.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to load leaderboard from " + file.getAbsolutePath(), e);
        }
    }

    public void clear() {
        entries.clear();
        save();
    }
}

