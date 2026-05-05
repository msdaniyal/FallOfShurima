package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Faction;
import seng201.team0.models.Guild;

/**
 * Controller for the setup screen.
 * Handles guild name input, God Warrior (faction) selection with hover stats,
 * and the difficulty popup that appears after the player confirms their name/faction.
 * Difficulty selection then navigates into the main game.
 * @author Mohammed, Xinyi
 */
public class SetupController {

    // ── Setup screen fields ───────────────────────────────────────────────────
    @FXML private TextField guildNameField;
    @FXML private Label validationLabel;
    @FXML private ImageView aatroxImage;
    @FXML private ImageView xolaaniImage;

    // ── Hover stat panels ─────────────────────────────────────────────────────
    @FXML private VBox aatroxStats;
    @FXML private VBox xolaaniStats;
    @FXML private Label aatroxStatText;
    @FXML private Label xolaaniStatText;

    // ── Difficulty popup ──────────────────────────────────────────────────────
    @FXML private Rectangle popupOverlay;
    @FXML private VBox difficultyPopup;
    @FXML private Button easyBtn;
    @FXML private Button normalBtn;
    @FXML private Button hardBtn;

    // ── State ─────────────────────────────────────────────────────────────────
    private Faction selectedFaction = null;
    private Guild pendingGuild = null;  // created on confirm, held until difficulty chosen

    // ── Aatrox and Xolaani lore/stats shown on hover ──────────────────────────
    private static final String AATROX_INFO =
            "Faction: AATROX\n\n" +
                    "The World Ender. A darkin warrior\n";

    private static final String XOLAANI_INFO =
            "Faction: XOLAANI\n\n" +
                    "The Blood Weaver. A darkin priestess\n";

    @FXML
    public void initialize() {
        aatroxStatText.setText(AATROX_INFO);
        xolaaniStatText.setText(XOLAANI_INFO);

        // Load warrior images
        aatroxImage.setImage(new Image(
                getClass().getResource("/images/aatrox.png").toExternalForm()));
        xolaaniImage.setImage(new Image(
                getClass().getResource("/images/xolaani.png").toExternalForm()));
    }

    // ── Hover handlers ────────────────────────────────────────────────────────

    @FXML
    public void onAatroxHover() {
        aatroxStats.setVisible(true);
    }

    @FXML
    public void onXolaaniHover() {
        xolaaniStats.setVisible(true);
    }

    @FXML
    public void onHoverExit() {
        aatroxStats.setVisible(false);
        xolaaniStats.setVisible(false);
    }

    // ── Warrior selection ─────────────────────────────────────────────────────

    @FXML
    public void onAatroxSelected() {
        selectedFaction = Faction.AATROX;
        highlightSelected(aatroxImage, xolaaniImage);
        validationLabel.setText("");
    }

    @FXML
    public void onXolaaniSelected() {
        selectedFaction = Faction.XOLAANI;
        highlightSelected(xolaaniImage, aatroxImage);
        validationLabel.setText("");
    }

    private void highlightSelected(ImageView selected, ImageView other) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(30);
        glow.setSpread(0.5);
        selected.setEffect(glow);
        selected.setOpacity(1.0);

        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);
        other.setEffect(dim);
        other.setOpacity(0.6);
    }

    // ── Confirm (opens difficulty popup) ─────────────────────────────────────

    /**
     * Validates name and faction, then shows the difficulty popup.
     * The Guild is created here and held in pendingGuild until difficulty is chosen.
     */
    @FXML
    public void onConfirm() {
        String name = guildNameField.getText().trim();

        if (name.length() < 9 || name.length() > 12) {
            validationLabel.setText("Guild name must be 9–12 characters.");
            validationLabel.setTextFill(Color.RED);
            return;
        }

        if (selectedFaction == null) {
            validationLabel.setText("Choose your God Warrior.");
            validationLabel.setTextFill(Color.RED);
            return;
        }

        // Store guild without gold — gold is set once difficulty is picked
        pendingGuild = new Guild(name, 0, selectedFaction);

        // Show popup
        popupOverlay.setVisible(true);
        difficultyPopup.setVisible(true);
        validationLabel.setText("");
    }

    // ── Difficulty selection (inside popup) ───────────────────────────────────

    @FXML
    public void onEasySelected() {
        finaliseDifficulty(Difficulty.EASY);
    }

    @FXML
    public void onNormalSelected() {
        finaliseDifficulty(Difficulty.NORMAL);
    }

    @FXML
    public void onHardSelected() {
        finaliseDifficulty(Difficulty.HARD);
    }

    /**
     * Finalises gold on the pending guild and navigates to the main game.
     * @param difficulty The difficulty the player chose
     */
    private void finaliseDifficulty(Difficulty difficulty) {
        pendingGuild.addGold(difficulty.getStartingGold());

        // Hide popup before navigating
        popupOverlay.setVisible(false);
        difficultyPopup.setVisible(false);

        navigateToGame(difficulty);
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    private void navigateToGame(Difficulty difficulty) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            // Uncomment once MainMenuController exists and accepts game data:
            // MainMenuController mainMenu = loader.getController();
            // mainMenu.setGameData(pendingGuild, difficulty);

            Stage stage = (Stage) guildNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("The Fall of Shurima");
        } catch (Exception e) {
            e.printStackTrace();
            validationLabel.setText("Error loading game screen.");
            validationLabel.setTextFill(Color.RED);
        }
    }
}