package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team0.models.Faction;
import seng201.team0.models.Guild;

/**
 * Controller for the setup screen (page 2).
 * Handles guild name input and God Warrior (faction) selection.
 * On confirm, creates a Guild and navigates to the difficulty screen.
 * @author Mohammed, Xinyi
 */
public class SetupController {

    @FXML private TextField guildNameField;
    @FXML private Label validationLabel;
    @FXML private ImageView aatroxImage;
    @FXML private ImageView xolaaniImage;

    /** The faction the player has currently selected. Null until one is chosen. */
    private Faction selectedFaction = null;

    // ----------------------------- Selection logic ----------------------------

    /**
     * Called when the player clicks the Aatrox warrior image.
     * Highlights Aatrox and dims Xolaani to show selection.
     */
    @FXML
    public void onAatroxSelected() {
        selectedFaction = Faction.AATROX;
        highlightSelected(aatroxImage, xolaaniImage);
        validationLabel.setText("");
    }

    /**
     * Called when the player clicks the Xolaani warrior image.
     * Highlights Xolaani and dims Aatrox to show selection.
     */
    @FXML
    public void onXolaaniSelected() {
        selectedFaction = Faction.XOLAANI;
        highlightSelected(xolaaniImage, aatroxImage);
        validationLabel.setText("");
    }

    /**
     * Applies a gold glow to the selected image and dims the other.
     * @param selected The image to highlight
     * @param other    The image to dim
     */
    private void highlightSelected(ImageView selected, ImageView other) {
        // Gold glow on selected
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(30);
        glow.setSpread(0.5);
        selected.setEffect(glow);
        selected.setOpacity(1.0);

        // Dim the other
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);
        other.setEffect(dim);
        other.setOpacity(0.6);
    }

    // ----------------------------- Confirm / validate -------------------------

    /**
     * Called when the player clicks Confirm.
     * Validates guild name (9-12 characters) and faction selection,
     * then creates a Guild and navigates to the difficulty screen.
     */
    @FXML
    public void onConfirm() {
        String name = guildNameField.getText().trim();

        // Validate name length
        if (name.length() < 9 || name.length() > 12) {
            validationLabel.setText("Guild name must be 9–12 characters.");
            validationLabel.setTextFill(Color.RED);
            return;
        }

        // Validate faction selected
        if (selectedFaction == null) {
            validationLabel.setText("Choose your God Warrior.");
            validationLabel.setTextFill(Color.RED);
            return;
        }

        // Create guild with placeholder starting gold — real value set after difficulty is chosen
        // We pass the guild and faction to the difficulty controller so it can finalise gold
        Guild guild = new Guild(name, 0, selectedFaction);

        navigateToDifficulty(guild);
    }

    // ----------------------------- Navigation --------------------------------

    /**
     * Navigates to the difficulty selection screen, passing the created guild.
     * @param guild The guild created from player input
     */
    private void navigateToDifficulty(Guild guild) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/difficulty.fxml")
            );
            Parent root = loader.load();

            // Pass the guild to the difficulty controller
            DifficultyController difficultyController = loader.getController();
            difficultyController.setGuild(guild);

            Stage stage = (Stage) guildNameField.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Choose Difficulty");
        } catch (Exception e) {
            e.printStackTrace();
            validationLabel.setText("Error loading difficulty screen.");
            validationLabel.setTextFill(Color.RED);
        }
    }
}