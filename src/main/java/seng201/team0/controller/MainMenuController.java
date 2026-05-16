package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

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

    @FXML private javafx.scene.layout.AnchorPane rootPane;
    @FXML private javafx.scene.image.ImageView backgroundImage;
    @FXML private Pane contentPane;

    private Game game;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
    }

    public void setGameData(Game game) {
        this.game = game;
        updateDisplay();
    }

    private void updateDisplay() {
        Guild guild = game.getGuild();

        partyCountLabel.setText(guild.getMainParty().size() + " / 5");
        guildNameLabel.setText(guild.getName());
        goldLabel.setText(String.valueOf(guild.getGold()));
        messageLabel.setText("");
    }

    @FXML
    public void onStartQuest() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quest1.fxml"));
            Parent root = loader.load();

            Quest1Controller controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) guildNameLabel.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Quest 1");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading Quest 1.");
        }
    }

    @FXML
    public void onMyParty() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/characterselect.fxml"));
            Parent root = loader.load();

            CharacterSelectController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) guildNameLabel.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
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
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Shop");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading shop.");
        }
    }
}