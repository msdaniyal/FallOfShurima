package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Guild;

/**
 * Controller for the difficulty selection screen.
 * Receives the Guild from SetupController, lets the player pick a difficulty,
 * then finalises starting gold and navigates into the game.
 * @author Mohammed, Xinyi
 */
public class DifficultyController {

    @FXML private VBox easyCard;
    @FXML private VBox normalCard;
    @FXML private VBox hardCard;
    @FXML private Label validationLabel;

    private Guild guild;
    private Difficulty selectedDifficulty = null;

    /**
     * Receives the Guild from SetupController.
     * @param guild The guild created during setup
     */
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    // ----------------------------- Card selection ----------------------------

    @FXML
    public void onEasySelected() {
        selectedDifficulty = Difficulty.EASY;
        highlightCard(easyCard);
        validationLabel.setText("");
    }

    @FXML
    public void onNormalSelected() {
        selectedDifficulty = Difficulty.NORMAL;
        highlightCard(normalCard);
        validationLabel.setText("");
    }

    @FXML
    public void onHardSelected() {
        selectedDifficulty = Difficulty.HARD;
        highlightCard(hardCard);
        validationLabel.setText("");
    }

    /**
     * Highlights the selected card and resets the others.
     */
    private void highlightCard(VBox selected) {
        for (VBox card : new VBox[]{easyCard, normalCard, hardCard}) {
            if (card == selected) {
                DropShadow glow = new DropShadow();
                glow.setColor(Color.GOLD);
                glow.setRadius(20);
                glow.setSpread(0.4);
                card.setEffect(glow);
                card.setOpacity(1.0);
            } else {
                card.setEffect(null);
                card.setOpacity(0.5);
            }
        }
    }

    // ----------------------------- Confirm -----------------------------------

    /**
     * Called when the player confirms their difficulty choice.
     * Finalises guild starting gold and navigates into the main game.
     */
    @FXML
    public void onConfirm() {
        if (selectedDifficulty == null) {
            validationLabel.setText("Choose a difficulty.");
            validationLabel.setTextFill(Color.RED);
            return;
        }

        // Finalise starting gold now that difficulty is known
        guild.addGold(selectedDifficulty.getStartingGold());

        navigateToGame();
    }

    // ----------------------------- Navigation --------------------------------

    private void navigateToGame() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/mainmenu.fxml")
            );
            Parent root = loader.load();

            // Pass guild and difficulty to the main game controller
            // MainMenuController mainMenu = loader.getController();
            // mainMenu.setGameData(guild, selectedDifficulty);

            Stage stage = (Stage) validationLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("The Fall of Shurima");
        } catch (Exception e) {
            e.printStackTrace();
            validationLabel.setText("Error loading game.");
            validationLabel.setTextFill(Color.RED);
        }
    }
}