import java.io.Serializable;

public record ScoreEntry(String name, int score) implements Serializable {}
