import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * View displayed when the game is paused.
 *
 * <p>Provides controls to resume gameplay, restart the level,
 * or return to the main menu.</p>
 */
public class PauseMenuView extends VBox {

    private final Button resumeBtn = new Button("Resume");
    private final Button restartBtn = new Button("Restart");
    private final Button menuBtn = new Button("Main Menu");

    public PauseMenuView(Font gameFont, GameSettings settings) {
        super(16);
        setAlignment(Pos.CENTER);
        setPickOnBounds(true);
        setStyle("-fx-background-color: rgba(0,0,0,0.65);");

        Label title = new Label("PAUSED");
        double scale = settings.getTextScale();
        title.setFont(Font.font(gameFont.getFamily(), 48 * scale));
        resumeBtn.setFont(Font.font(gameFont.getFamily(), 18 * scale));
        restartBtn.setFont(Font.font(gameFont.getFamily(), 18 * scale));
        menuBtn.setFont(Font.font(gameFont.getFamily(), 18 * scale));

        resumeBtn.setMinWidth(180);
        restartBtn.setMinWidth(180);
        menuBtn.setMinWidth(180);

        getChildren().addAll(title, resumeBtn, restartBtn, menuBtn);
    }

    public Button resumeButton() { return resumeBtn; }
    public Button restartButton() { return restartBtn; }
    public Button menuButton() { return menuBtn; }
}
