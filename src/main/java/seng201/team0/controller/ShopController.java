package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;
import seng201.team0.models.Item;
import seng201.team0.models.ItemType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the shop page screen.
 *
 * The shop sells three healing items:
 * 1. Small Potion  - heals one selected adventurer
 * 2. Party Potion  - heals all main party members
 * 3. Full Restore  - fully heals all main party members
 *
 * Item prices change depending on difficulty.
 *
 * @author Mohammed, Xinyi
 */
public class ShopController {

    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML private Label goldLabel;
    @FXML private Label messageLabel;
    @FXML private AnchorPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Pane contentPane;

    @FXML private Label smallPotionLabel;
    @FXML private Label partyPotionLabel;
    @FXML private Label fullRestoreLabel;

    @FXML private ChoiceBox<String> targetChoiceBox;

    // ── Game state ────────────────────────────────────────────────────────────
    private Game game;
    private Guild guild;

    // ── Shop items ────────────────────────────────────────────────────────────
    private final List<Item> shopItems = new ArrayList<>();

    private Item smallPotion;
    private Item partyPotion;
    private Item fullRestore;

    /**
     * Runs automatically when shop.fxml is loaded.
     *
     * Do not create items here because game/difficulty have not been passed in yet.
     */
    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        messageLabel.setText("");
    }

    /**
     * Receives the current Game object from MainMenuController.
     * This must be called after FXMLLoader.load().
     *
     * @param game The current game object
     */
    public void setGameData(Game game) {
        this.game = game;
        this.guild = game.getGuild();

        createShopItems();
        displayItemInfo();
        updateGoldLabel();
    }

    /**
     * Creates the three shop items.
     * Prices are different depending on difficulty.
     */
    private void createShopItems() {
        int smallPotionCost;
        int partyPotionCost;
        int fullRestoreCost;

        Difficulty difficulty = game.getDifficulty();

        switch (difficulty) {
            case EASY:
                smallPotionCost = 30;
                partyPotionCost = 70;
                fullRestoreCost = 130;
                break;

            case NORMAL:
                smallPotionCost = 40;
                partyPotionCost = 90;
                fullRestoreCost = 160;
                break;

            case HARD:
                smallPotionCost = 55;
                partyPotionCost = 120;
                fullRestoreCost = 210;
                break;

            default:
                smallPotionCost = 40;
                partyPotionCost = 90;
                fullRestoreCost = 160;
                break;
        }

        smallPotion = new Item(
                "Small Potion",
                ItemType.SINGLE,
                30,
                smallPotionCost,
                "Restore 30 HP to one adventurer."
        );

        partyPotion = new Item(
                "Party Potion",
                ItemType.PARTY,
                20,
                partyPotionCost,
                "Restore 20 HP to all main party members."
        );

        fullRestore = new Item(
                "Full Restore",
                ItemType.FULL,
                0,
                fullRestoreCost,
                "Fully restore all main party members."
        );

        shopItems.clear();
        shopItems.add(smallPotion);
        shopItems.add(partyPotion);
        shopItems.add(fullRestore);
    }

    /**
     * Displays item name, price, and description on the shop screen.
     */
    private void displayItemInfo() {
        smallPotionLabel.setText(
                smallPotion.getName() + " — " + smallPotion.getCost() + " gold\n" +
                        smallPotion.getDescription()
        );

        partyPotionLabel.setText(
                partyPotion.getName() + " — " + partyPotion.getCost() + " gold\n" +
                        partyPotion.getDescription()
        );

        fullRestoreLabel.setText(
                fullRestore.getName() + " — " + fullRestore.getCost() + " gold\n" +
                        fullRestore.getDescription()
        );
    }

    /**
     * Updates the displayed guild gold.
     */
    private void updateGoldLabel() {
        goldLabel.setText("Gold: " + guild.getGold());
    }

    /**
     * Buy Small Potion.
     * Requires the player to choose one target adventurer.
     */
    @FXML
    public void onBuySmallPotion() {
        if (guild.getMainParty().isEmpty()) {
            showError("Your party is empty.");
            return;
        }

        if (guild.getGold() < smallPotion.getCost()) {
            showError("Not enough gold for " + smallPotion.getName() + ".");
            return;
        }

        List<String> adventurerNames = new ArrayList<>();

        for (Adventurer adventurer : guild.getMainParty()) {
            adventurerNames.add(
                    adventurer.getName() +
                            "  HP: " + adventurer.getCurrentHealth() +
                            "/" + adventurer.getMaxHealth()
            );
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(
                adventurerNames.get(0),
                adventurerNames
        );

        dialog.setTitle("Choose Target");
        dialog.setHeaderText("Use Small Potion");
        dialog.setContentText("Choose an adventurer to heal:");

        dialog.showAndWait().ifPresent(selectedText -> {
            String selectedName = selectedText.split("  HP:")[0];
            Adventurer target = findAdventurerByName(selectedName);

            if (target == null) {
                showError("Could not find selected adventurer.");
                return;
            }

            buyItem(smallPotion, target);
        });
    }

    /**
     * Buy Party Potion.
     * Heals every adventurer in the main party.
     */
    @FXML
    public void onBuyPartyPotion() {
        buyItem(partyPotion, null);
    }

    /**
     * Buy Full Restore.
     * Fully heals every adventurer in the main party.
     */
    @FXML
    public void onBuyFullRestore() {
        buyItem(fullRestore, null);
    }

    /**
     * Handles payment and item use.
     *
     * @param item The item being bought
     * @param target The target adventurer, only needed for SINGLE item
     */
    private void buyItem(Item item, Adventurer target) {
        if (guild.getMainParty().isEmpty()) {
            showError("Your party is empty.");
            return;
        }

        if (guild.getGold() < item.getCost()) {
            showError("Not enough gold for " + item.getName() + ".");
            return;
        }

        boolean paid = guild.spendGold(item.getCost());

        if (!paid) {
            showError("Not enough gold.");
            return;
        }

        item.use(guild, target);

        updateGoldLabel();

        if (target != null) {
            showSuccess("Used " + item.getName() + " on " + target.getName() + ".");
        } else {
            showSuccess("Used " + item.getName() + " on your party.");
        }
    }

    /**
     * Finds one adventurer in the main party by name.
     *
     * @param name The adventurer name
     * @return Adventurer if found, otherwise null
     */
    private Adventurer findAdventurerByName(String name) {
        for (Adventurer adventurer : guild.getMainParty()) {
            if (adventurer.getName().equals(name)) {
                return adventurer;
            }
        }
        return null;
    }

    /**
     * Shows a success message.
     */
    private void showSuccess(String message) {
        messageLabel.setTextFill(Color.LIGHTGREEN);
        messageLabel.setText(message);
    }

    /**
     * Shows an error message.
     */
    private void showError(String message) {
        messageLabel.setTextFill(Color.RED);
        messageLabel.setText(message);
    }

    /**
     * Back button.
     * Returns to main menu and passes the same Game object back.
     */
    @FXML
    public void onBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) goldLabel.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error returning to main menu.");
        }
    }
}