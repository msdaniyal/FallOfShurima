package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;

/**
 * Map hub main menu.
 *
 * The main menu is now an interactive world map:
 * - Institute of War opens a hub popup for Shop and Barracks/Edit Guild.
 * - Icathia starts the first quest.
 * - Other map labels are currently locked/future areas.
 *
 * This keeps the same class name used by ShopController, CharacterSelectController,
 * MainWindow and other existing screens when they return to /fxml/mainmenu.fxml.
 */
public class MainMenuController {

    // Root / responsive screen fields
    @FXML private AnchorPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Pane contentPane;

    // HUD fields
    @FXML private Label guildNameLabel;
    @FXML private Label goldLabel;
    @FXML private Label partyCountLabel;
    @FXML private Label messageLabel;
    @FXML private Label currentQuestLabel;

    // Potion display
    @FXML private Label smallPotionInventoryLabel;
    @FXML private Label partyPotionInventoryLabel;
    @FXML private Label fullRestoreInventoryLabel;

    // Institute popup
    @FXML private Pane darkOverlay;
    @FXML private VBox institutePopup;

    private Game game;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        hideInstitutePopup();
    }

    public void setGameData(Game game) {
        this.game = game;
        updateDisplay();
    }

    private void updateDisplay() {
        if (game == null || game.getGuild() == null) {
            return;
        }

        Guild guild = game.getGuild();

        guildNameLabel.setText(guild.getName());
        goldLabel.setText(String.valueOf(guild.getGold()));
        partyCountLabel.setText(guild.getMainParty().size() + " / 5");

        smallPotionInventoryLabel.setText("Silver x" + guild.getSmallPotionCount());
        partyPotionInventoryLabel.setText("Gold x" + guild.getPartyPotionCount());
        fullRestoreInventoryLabel.setText("Purple x" + guild.getFullRestoreCount());

        if (game.getCurrentQuest() != null) {
            currentQuestLabel.setText("Current Quest: " + game.getCurrentQuest().getName());
        } else {
            currentQuestLabel.setText("Current Quest: Unknown");
        }

        messageLabel.setText("Click a map location.");
    }

    // -------------------------------------------------------------------------
    // Map location handlers
    // -------------------------------------------------------------------------

    @FXML
    public void onInstituteOfWar() {
        messageLabel.setText("Institute of War: prepare your guild.");
        showInstitutePopup();
    }

    /**
     * Compatibility method: if an older FXML button still calls onStartQuest,
     * it will start Icathia.
     */
    @FXML
    public void onStartQuest() {
        onIcathia();
    }

    @FXML
    public void onIcathia() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quest1.fxml"));
            Parent root = loader.load();

            Quest1Controller controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Icathia");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading Icathia.");
        }
    }

    @FXML
    public void onLockedLocation() {
        messageLabel.setText("This region is locked for now.");
    }

    @FXML
    public void onQuestAreaHover() {
        messageLabel.setText("Quest area — click to travel.");
    }

    @FXML
    public void onHubAreaHover() {
        messageLabel.setText("Institute of War — shop, barracks and guild preparation.");
    }

    // -------------------------------------------------------------------------
    // Institute popup handlers
    // -------------------------------------------------------------------------

    private void showInstitutePopup() {
        darkOverlay.setVisible(true);
        institutePopup.setVisible(true);
        darkOverlay.toFront();
        institutePopup.toFront();
    }

    private void hideInstitutePopup() {
        if (darkOverlay != null) {
            darkOverlay.setVisible(false);
        }
        if (institutePopup != null) {
            institutePopup.setVisible(false);
        }
    }

    @FXML
    public void onCloseInstitutePopup() {
        hideInstitutePopup();
        messageLabel.setText("Click a map location.");
    }

    @FXML
    public void onOpenShop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/shop.fxml"));
            Parent root = loader.load();

            ShopController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Shop");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading shop.");
        }
    }

    /**
     * Compatibility method: older code/FXML may still call onShop.
     */
    @FXML
    public void onShop() {
        onOpenShop();
    }

    @FXML
    public void onOpenBarracks() {
        onMyParty();
    }

    @FXML
    public void onMyParty() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/characterselect.fxml"));
            Parent root = loader.load();

            CharacterSelectController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Barracks");

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading barracks.");
        }
    }
}
