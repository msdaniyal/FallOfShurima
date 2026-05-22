package seng201.team76.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team76.models.Adventurer;
import seng201.team76.models.Difficulty;
import seng201.team76.models.Game;
import seng201.team76.models.Guild;
import seng201.team76.models.Item;
import seng201.team76.models.ItemType;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the shop page screen.
 *
 * The shop sells three healing items:
 * 1. Silver Potion - heals one selected adventurer
 * 2. Gold Potion   - heals all main party members
 * 3. Purple Potion - fully heals all main party members
 *
 * Item prices change depending on difficulty.
 *
 * @author Mohammed, Xinyi
 */
public class ShopController implements GameDataReceiver {

    /**
     * Creates the shop controller.
     */
    public ShopController() {
    }

    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML private Label goldLabel;
    @FXML private Label messageLabel;

    @FXML private AnchorPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Pane contentPane;

    @FXML private VBox smallPotionStats;
    @FXML private VBox partyPotionStats;
    @FXML private VBox fullRestoreStats;

    @FXML private Label smallPotionStatText;
    @FXML private Label partyPotionStatText;
    @FXML private Label fullRestoreStatText;

    @FXML private Label smallPotionPriceLabel;
    @FXML private Label partyPotionPriceLabel;
    @FXML private Label fullRestorePriceLabel;

    @FXML private Label smallPotionQuantityLabel;
    @FXML private Label partyPotionQuantityLabel;
    @FXML private Label fullRestoreQuantityLabel;

    @FXML private Label ownedSmallPotionLabel;
    @FXML private Label ownedPartyPotionLabel;
    @FXML private Label ownedFullRestoreLabel;

    @FXML private ImageView ownedSmallPotionImage;
    @FXML private ImageView ownedPartyPotionImage;
    @FXML private ImageView ownedFullRestoreImage;

    @FXML private Rectangle targetPopupOverlay;
    @FXML private VBox targetPopup;
    @FXML private Label targetPopupTitle;
    @FXML private VBox targetListBox;

    // ── Game state ────────────────────────────────────────────────────────────
    private Game game;
    private Guild guild;

    // ── Shop items ────────────────────────────────────────────────────────────
    private final List<Item> shopItems = new ArrayList<>();

    private Item smallPotion;
    private Item partyPotion;
    private Item fullRestore;

    // ── Quantities ────────────────────────────────────────────────────────────
    private int smallPotionQuantity = 1;
    private int partyPotionQuantity = 1;
    private int fullRestoreQuantity = 1;

    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 9;

    /**
     * Runs automatically when shop.fxml is loaded.
     *
     * Do not create items here because game/difficulty have not been passed in yet.
     */
    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        messageLabel.setText("");

        hideAllPotionStats();
        updateQuantityLabels();
    }

    /**
     * Receives the current Game object from MainMenuController.
     *
     * @param game The current game object
     */
    public void setGameData(Game game) {
        this.game = game;
        this.guild = game.getGuild();

        createShopItems();
        displayItemInfo();
        updateGoldLabel();
        updateOwnedPotionDisplay();
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

        smallPotionCost = 30;
        partyPotionCost = 50;
        fullRestoreCost = 80;

        smallPotion = new Item(
                "Silver Potion",
                ItemType.SINGLE,
                30,
                smallPotionCost,
                "Restore 30 HP to one selected adventurer."
        );

        partyPotion = new Item(
                "Gold Potion",
                ItemType.PARTY,
                20,
                partyPotionCost,
                "Restore 20 HP to every adventurer in your main party."
        );

        fullRestore = new Item(
                "Purple Potion",
                ItemType.FULL,
                0,
                fullRestoreCost,
                "Fully restore every adventurer in your main party."
        );

        shopItems.clear();
        shopItems.add(smallPotion);
        shopItems.add(partyPotion);
        shopItems.add(fullRestore);
    }

    /**
     * Displays item prices.
     */
    private void displayItemInfo() {
        smallPotionPriceLabel.setText("Price: " + smallPotion.getCost() + " gold");
        partyPotionPriceLabel.setText("Price: " + partyPotion.getCost() + " gold");
        fullRestorePriceLabel.setText("Price: " + fullRestore.getCost() + " gold");

        smallPotionStatText.setText(
                "Type: Single Target\n" +
                        "Effect: Restore " + smallPotion.getHealAmount() + " HP\n" +
                        "Cost: " + smallPotion.getCost() + " gold\n\n" +
                        smallPotion.getDescription()
        );

        partyPotionStatText.setText(
                "Type: Party Heal\n" +
                        "Effect: Restore " + partyPotion.getHealAmount() + " HP to all\n" +
                        "Cost: " + partyPotion.getCost() + " gold\n\n" +
                        partyPotion.getDescription()
        );

        fullRestoreStatText.setText(
                "Type: Full Restore\n" +
                        "Effect: Fully heal all party members\n" +
                        "Cost: " + fullRestore.getCost() + " gold\n\n" +
                        fullRestore.getDescription()
        );
    }

    /**
     * Updates the displayed guild gold.
     */
    private void updateGoldLabel() {
        goldLabel.setText(String.valueOf(guild.getGold()));
    }

    /**
     * Updates quantity labels.
     */
    private void updateQuantityLabels() {
        if (smallPotionQuantityLabel != null) {
            smallPotionQuantityLabel.setText(String.valueOf(smallPotionQuantity));
        }

        if (partyPotionQuantityLabel != null) {
            partyPotionQuantityLabel.setText(String.valueOf(partyPotionQuantity));
        }

        if (fullRestoreQuantityLabel != null) {
            fullRestoreQuantityLabel.setText(String.valueOf(fullRestoreQuantity));
        }
    }

    // ── Hover descriptions ────────────────────────────────────────────────────

    /**
     * Shows the Silver Potion hover panel.
     */
    @FXML
    public void onSmallPotionHover() {
        hideAllPotionStats();
        smallPotionStats.setVisible(true);
        smallPotionStats.toFront();
    }

    /**
     * Shows the Gold Potion hover panel.
     */
    @FXML
    public void onPartyPotionHover() {
        hideAllPotionStats();
        partyPotionStats.setVisible(true);
        partyPotionStats.toFront();
    }

    /**
     * Shows the Purple Potion hover panel.
     */
    @FXML
    public void onFullRestoreHover() {
        hideAllPotionStats();
        fullRestoreStats.setVisible(true);
        fullRestoreStats.toFront();
    }

    /**
     * Hides potion hover panels.
     */
    @FXML
    public void onPotionHoverExit() {
        hideAllPotionStats();
    }

    private void hideAllPotionStats() {
        if (smallPotionStats != null) {
            smallPotionStats.setVisible(false);
        }
        if (partyPotionStats != null) {
            partyPotionStats.setVisible(false);
        }
        if (fullRestoreStats != null) {
            fullRestoreStats.setVisible(false);
        }
    }


    // ── Quantity buttons ──────────────────────────────────────────────────────

    /**
     * Increases the Silver Potion quantity.
     */
    @FXML
    public void onIncreaseSmallPotion() {
        smallPotionQuantity = increaseQuantity(smallPotionQuantity);
        updateQuantityLabels();
    }

    /**
     * Decreases the Silver Potion quantity.
     */
    @FXML
    public void onDecreaseSmallPotion() {
        smallPotionQuantity = decreaseQuantity(smallPotionQuantity);
        updateQuantityLabels();
    }

    /**
     * Increases the Gold Potion quantity.
     */
    @FXML
    public void onIncreasePartyPotion() {
        partyPotionQuantity = increaseQuantity(partyPotionQuantity);
        updateQuantityLabels();
    }

    /**
     * Decreases the Gold Potion quantity.
     */
    @FXML
    public void onDecreasePartyPotion() {
        partyPotionQuantity = decreaseQuantity(partyPotionQuantity);
        updateQuantityLabels();
    }

    /**
     * Increases the Purple Potion quantity.
     */
    @FXML
    public void onIncreaseFullRestore() {
        fullRestoreQuantity = increaseQuantity(fullRestoreQuantity);
        updateQuantityLabels();
    }

    /**
     * Decreases the Purple Potion quantity.
     */
    @FXML
    public void onDecreaseFullRestore() {
        fullRestoreQuantity = decreaseQuantity(fullRestoreQuantity);
        updateQuantityLabels();
    }

    private int increaseQuantity(int quantity) {
        if (quantity < MAX_QUANTITY) {
            return quantity + 1;
        }

        return quantity;
    }

    private int decreaseQuantity(int quantity) {
        if (quantity > MIN_QUANTITY) {
            return quantity - 1;
        }

        return quantity;
    }

    // ── Buy buttons ───────────────────────────────────────────────────────────

    /**
     * Buy Silver Potion.
     * Requires the player to choose one target adventurer.
     */
    @FXML
    public void onBuySmallPotion() {
        int totalCost = smallPotion.getCost() * smallPotionQuantity;

        if (guild.getGold() < totalCost) {
            showError("Not enough gold for " + smallPotionQuantity + " " + smallPotion.getName() + ".");
            return;
        }

        guild.spendGold(totalCost);
        guild.addSmallPotions(smallPotionQuantity);

        showSuccess("Bought " + smallPotionQuantity + " Silver Potion.");
        updateGoldLabel();
        updateOwnedPotionDisplay();
    }

    /**
     * Buy Gold Potion.
     * Heals every adventurer in the main party.
     */
    @FXML
    public void onBuyPartyPotion() {
        int totalCost = partyPotion.getCost() * partyPotionQuantity;

        if (guild.getGold() < totalCost) {
            showError("Not enough gold for " + partyPotionQuantity + " " + partyPotion.getName() + ".");
            return;
        }

        guild.spendGold(totalCost);
        guild.addPartyPotions(partyPotionQuantity);

        showSuccess("Bought " + partyPotionQuantity + " Gold Potion.");
        updateGoldLabel();
        updateOwnedPotionDisplay();
    }

    /**
     * Buy Purple Potion.
     * Fully heals every adventurer in the main party.
     */
    @FXML
    public void onBuyFullRestore() {
        int totalCost = fullRestore.getCost() * fullRestoreQuantity;

        if (guild.getGold() < totalCost) {
            showError("Not enough gold for " + fullRestoreQuantity + " " + fullRestore.getName() + ".");
            return;
        }

        guild.spendGold(totalCost);
        guild.addFullRestores(fullRestoreQuantity);

        showSuccess("Bought " + fullRestoreQuantity + " Purple Potion.");
        updateGoldLabel();
        updateOwnedPotionDisplay();
    }

    private void updateOwnedPotionDisplay() {
        if (guild == null) {
            return;
        }

        ownedSmallPotionLabel.setText("x" + guild.getSmallPotionCount());
        ownedPartyPotionLabel.setText("x" + guild.getPartyPotionCount());
        ownedFullRestoreLabel.setText("x" + guild.getFullRestoreCount());
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
