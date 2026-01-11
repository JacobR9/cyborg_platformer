import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

/**
 * View displayed when the game ends.
 *
 * <p>Shows final score, run statistics, and leaderboard controls,
 * and allows the player to restart or return to the main menu.</p>
 */
public class EndView extends StackPane {

    private final Label resultTitle = new Label("You Win!");
    private final Label scoreLabel = new Label("Score: 0");
    private final TextField nameField = new TextField();
    private final Label statusLabel = new Label("");

    private final Button saveBtn = new Button("Save score");

    //stats
    private final Label timeLabel = new Label("Time: 0.0s");
    private final Label killsLabel = new Label("Kills: 0");
    private final Label deathsLabel = new Label("Deaths: 0");

    private final Button restartBtn = new Button("Play again");
    private final Button menuBtn = new Button("Main menu");

    private final LeaderboardView leaderboardView;

    private Runnable onRestart = () -> {
    };
    private Runnable onBackToMenu = () -> {
    };

    public EndView(Font font, GameSettings settings) {
        setVisible(false);
        setPickOnBounds(true);
        //set end screen fonts to match
        resultTitle.setFont(Font.font(font.getFamily(), font.getSize() * 0.65));
        resultTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: green;");

        scoreLabel.setFont(Font.font(font.getFamily(), font.getSize() * 0.65));
        scoreLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        statusLabel.setFont(Font.font(font.getFamily(), font.getSize() * 0.65));
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        Label namePrompt = new Label("Name:");
        namePrompt.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        namePrompt.setFont(Font.font(font.getFamily(), font.getSize() * 0.5));
        nameField.setPromptText("Anonymous");
        nameField.setFont(Font.font(font.getFamily(), font.getSize() * 0.3));
        nameField.setMaxWidth(220);

        //initialise leaderboard for end screen
        leaderboardView = new LeaderboardView(font);

        ScrollPane lbScroll = new ScrollPane(leaderboardView);
        lbScroll.setFitToWidth(true);
        lbScroll.setMaxHeight(220);
        lbScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //game stats for that run
        timeLabel.setFont(Font.font(font.getFamily(), font.getSize() * 0.4));
        timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        killsLabel.setFont(Font.font(font.getFamily(), font.getSize() * 0.4));
        killsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        deathsLabel.setFont(Font.font(font.getFamily(), font.getSize() * 0.4));
        deathsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");

        HBox statsBox = new HBox(16, timeLabel, killsLabel, deathsLabel);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setStyle("-fx-padding: 10; -fx-text-fill: white;");

        // end screen buttons displayed horizontally
        HBox buttonBar = new HBox(12, saveBtn, restartBtn, menuBtn);
        buttonBar.setAlignment(Pos.CENTER);
        saveBtn.setMinWidth(220);
        restartBtn.setMinWidth(220);
        menuBtn.setMinWidth(220);

        // same font scaling logic
        double scale = settings.getTextScale(); // pass settings into EndView if not already
        saveBtn.setFont(Font.font(font.getFamily(), 18 * scale));
        restartBtn.setFont(Font.font(font.getFamily(), 18 * scale));
        menuBtn.setFont(Font.font(font.getFamily(), 18 * scale));

        VBox box = new VBox(12, resultTitle, scoreLabel, statsBox, statusLabel, namePrompt, nameField, lbScroll, buttonBar);
        box.setAlignment(Pos.CENTER);
        box.setMaxWidth(600);

        getChildren().add(box);

        restartBtn.setOnAction(e -> onRestart.run());
        menuBtn.setOnAction(e -> onBackToMenu.run());
    }

    public Button getSaveButton() { return saveBtn; }

    public LeaderboardView getLeaderboardView() { return leaderboardView; }

    public void setScore(int score) { scoreLabel.setText("Score: " + score); }

    public void setStatus(String text) { statusLabel.setText(text == null ? "" : text); }

    public String getEnteredName() {
        String n = nameField.getText();
        if (n == null || n.isBlank()) return "Anonymous";
        n = n.trim();
        return n.length() > 12 ? n.substring(0, 12) : n;
    }

    public void show() {
        setVisible(true);
    }

    public void hide() {
        setVisible(false);
    }

    public void setOnRestart(Runnable r) {
        this.onRestart = (r == null) ? () -> {} : r;
    }

    public void setOnBackToMenu(Runnable r) {
        this.onBackToMenu = (r == null) ? () -> {} : r;
    }

    //resets end screen fields and highlight
    public void resetUi() {
        nameField.clear();
        setStatus("");
        saveBtn.setDisable(false);
    }

    public void runStats(double elapsedSeconds, int kills, int deaths) {
        timeLabel.setText(String.format("Time: %.1fs", elapsedSeconds));
        killsLabel.setText("Kills: " + kills);
        deathsLabel.setText("Deaths: " + deaths);
    }


}