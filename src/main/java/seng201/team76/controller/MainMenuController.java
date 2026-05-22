package seng201.team76.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng201.team76.models.Game;
import seng201.team76.models.Guild;

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
public class MainMenuController implements GameDataReceiver {

    /**
     * Creates the main menu controller.
     */
    public MainMenuController() {
    }

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

    @FXML private Button quest1Button;
    @FXML private Button quest2Button;
    @FXML private Button quest3Button;
    @FXML private Button quest4Button;
    @FXML private Button quest5Button;
    @FXML private Button quest6Button;

    // Institute popup
    @FXML private Pane darkOverlay;
    @FXML private VBox institutePopup;

    private Game game;

    /**
     * Sets up the map screen after the FXML loads.
     */
    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        hideInstitutePopup();
    }

    /**
     * Receives the current game and refreshes the map display.
     *
     * @param game The current game
     */
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

        updateQuestButtons();

        messageLabel.setText(game.isGameOver() ? "The story has ended." : "Click a map location.");
    }

    private void updateQuestButtons() {
        Button[] buttons = { quest1Button, quest2Button, quest3Button, quest4Button, quest5Button, quest6Button };

        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] == null || i >= game.getQuests().size()) {
                continue;
            }

            boolean unlocked = game.getQuests().get(i).isUnlocked();
            buttons[i].setDisable(!unlocked);
            buttons[i].setOpacity(unlocked ? 1.0 : 0.45);

            if (game.getQuests().get(i).isCompleted()) {
                buttons[i].setText("✓ Quest " + (i + 1));
            }
        }

        if (quest6Button != null) {
            quest6Button.setVisible(game.getQuests().size() > 5 && game.getQuests().get(5).isUnlocked());
        }
    }

    // -------------------------------------------------------------------------
    // Map location handlers
    // -------------------------------------------------------------------------

    /**
     * Opens the Institute of War popup.
     */
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

    /**
     * Opens the first quest area.
     */
    @FXML
    public void onIcathia() {
        openQuest(1);
    }

    /** Opens Quest 1. */
    @FXML public void onQuest1() { openQuest(1); }
    /** Opens Quest 2. */
    @FXML public void onQuest2() { openQuest(2); }
    /** Opens Quest 3. */
    @FXML public void onQuest3() { openQuest(3); }
    /** Opens Quest 4. */
    @FXML public void onQuest4() { openQuest(4); }
    /** Opens Quest 5. */
    @FXML public void onQuest5() { openQuest(5); }
    /** Opens Quest 6. */
    @FXML public void onQuest6() { openQuest(6); }


    private void openQuest(int questNumber) {
        if (game == null) {
            messageLabel.setText("No active game found.");
            return;
        }

        int questIndex = questNumber - 1;
        if (questIndex < 0 || questIndex >= game.getQuests().size()) {
            messageLabel.setText("Quest " + questNumber + " does not exist.");
            return;
        }

        if (!game.getQuests().get(questIndex).isUnlocked()) {
            messageLabel.setText("Quest " + questNumber + " is locked.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/quest" + questNumber + ".fxml"));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof GameDataReceiver) {
                ((GameDataReceiver) controller).setGameData(game);
            } else {
                messageLabel.setText("Quest " + questNumber + " controller cannot receive game data.");
                return;
            }

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Quest " + questNumber);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error loading Quest " + questNumber + ".");
        }
    }

    /**
     * Shows a message for locked map locations.
     */
    @FXML
    public void onLockedLocation() {
        messageLabel.setText("This region is locked for now.");
    }

    /**
     * Shows hover text for quest areas.
     */
    @FXML
    public void onQuestAreaHover() {
        messageLabel.setText("Quest area — click to travel.");
    }

    /**
     * Shows hover text for the Institute of War.
     */
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

    /**
     * Closes the Institute of War popup.
     */
    @FXML
    public void onCloseInstitutePopup() {
        hideInstitutePopup();
        messageLabel.setText("Click a map location.");
    }

    /**
     * Opens the shop screen.
     */
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

    /**
     * Opens the barracks screen.
     */
    @FXML
    public void onOpenBarracks() {
        onMyParty();
    }

    /**
     * Opens the party editing screen if party editing is still allowed.
     */
    @FXML
    public void onMyParty() {
        if (game != null && game.getGuild() != null && game.getGuild().isPartyLocked()) {
            messageLabel.setText("Recruiting is closed after Quest 4. Your warband is locked for the final conflict.");
            hideInstitutePopup();
            return;
        }

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
