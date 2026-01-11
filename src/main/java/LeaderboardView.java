import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.List;

/**
 * Renders the leaderboard UI.
 *
 * <p>Displays a list of high score entries and highlights the most
 * recently added score when provided.</p>
 */
public class LeaderboardView extends VBox {

    private final VBox rows = new VBox(6);
    private final Label title = new Label("Leaderboard");
    //font
    private Font font = Font.getDefault();
    private double scale = 1.0;
    private double maxSize = 18;
    private double minSize = 10;


    public LeaderboardView(Font font) {
        if (font != null) this.font = font;

        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);

        title.setFont(this.font);
        rows.setAlignment(Pos.TOP_LEFT);

        getChildren().addAll(title, rows);
    }

    public void setEntries(List<ScoreEntry> entries, int maxToShow, ScoreEntry highlight) {
        rows.getChildren().clear();

        int n = Math.min(maxToShow, entries.size());
        for (int i = 0; i < n; i++) {
            ScoreEntry e = entries.get(i);
            Label line = new Label(String.format(
                    "%2d) %-12s  %d", i + 1, e.name(), e.score()
            ));
            line.setFont(scaledFont());

            //makes recent player gold highlighted
            if (highlight != null
                    && e.name().equals(highlight.name())
                    && e.score() == highlight.score()) {line.setStyle("-fx-text-fill: gold; -fx-font-weight: bold; ");}

            rows.getChildren().add(line);
        }

        if (n == 0) {
            Label empty = new Label("No scores yet");
            empty.setFont(scaledFont());
            rows.getChildren().add(empty);
        }
    }

    public void setFont(Font font) {
        if (font == null) return;
        this.font = font;
        title.setFont(font);
        rows.getChildren().forEach(n -> {
            if (n instanceof Label l) l.setFont(font);
        });
    }

    public void setScale(double scale) {
        this.scale = scale;
        title.setFont(Font.font(font.getFamily(), font.getSize() * scale));
        rows.getChildren().forEach(n -> {
            if (n instanceof Label l) {
                l.setFont(Font.font(font.getFamily(), font.getSize() * scale));
            }
        });
    }

    private Font scaledFont() {
        double size = font.getSize() * scale;
        size = Math.max(minSize, Math.min(maxSize, size));
        return Font.font(font.getFamily(), size);
    }

}
