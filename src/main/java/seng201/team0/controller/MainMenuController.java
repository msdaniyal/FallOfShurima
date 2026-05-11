package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;


/**
 * Controller for the real main menu screen.
 * Shows three options:
 * 1. Start Quest
 * 2. My Party
 * 3. Shop
 * @author Mohammed, Xinyi
 */
public class MainMenuController {

    @FXML private Label guildNameLabel;
    @FXML private Label goldLabel;
    @FXML private Label partyCountLabel;
    @FXML private Label messageLabel;

    private Game game;

    public void setGameData(Game game) {
        this.game = game;
        updateDisplay();
    }

    private void updateDisplay() {
        Guild guild = game.getGuild();

        guildNameLabel.setText("Guild: " + guild.getName());
        goldLabel.setText("Gold: " + guild.getGold());
        partyCountLabel.setText("Party Members: " + guild.getMainParty().size() + " / 5");
        messageLabel.setText("");
    }

    @FXML
    public void onStartQuest() {
        messageLabel.setText("Quest screen coming soon.");

        // Later:
        // navigateToQuestScreen();
    }

    @FXML
    public void onMyParty() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/characterselect.fxml"));
            Parent root = loader.load();

            CharacterSelectController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) guildNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("The Fall of Shurima — My Party");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading party screen.");
        }
    }

    @FXML
    public void onShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shop.fxml"));
            Parent root = loader.load();

            ShopController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) guildNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("The Fall of Shurima — Shop");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading shop.");
        }
    }
}