package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Faction;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the main menu.
 * This screen is used as the character selection screen.
 * The player can choose up to 5 warriors to join the guild.
 * @author Mohammed, Xinyi
 */
public class CharacterSelectController {

    // ── Setup screen fields ───────────────────────────────────────────────────
    @FXML private GridPane characterGrid;
    @FXML private Label titleLabel;
    @FXML private Label instructionLabel;
    @FXML private Label selectedCountLabel;
    @FXML private Label warningLabel;
    @FXML private Label goldLabel;
    @FXML private AnchorPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Pane contentPane;

    // ── State ─────────────────────────────────────────────────────────────────
    private Guild guild;
    private Difficulty difficulty;
    private Game game;
    private int availableGold;


    // ── Show all characters ───────────────────────────────────────────────────
    private final List<Adventurer> allCharacters = new ArrayList<>();
    private final List<Adventurer> selectedCharacters = new ArrayList<>();
    private final List<VBox> characterCards = new ArrayList<>();


    // ── Display ───────────────────────────────────────────────────────────────
    private static final int MAX_SELECTED = 5;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        selectedCountLabel.setText("Selected: 0 / " + MAX_SELECTED);
        warningLabel.setText("");
    }

    /**
     * Used when entering character selection for the first time from SetupController.
     */
    public void setGameData(Guild guild, Difficulty difficulty) {
        this.guild = guild;
        this.difficulty = difficulty;
        this.game = null;
        this.availableGold = guild.getGold();

        createCharacterList();
        displayCharacters();
    }

    /**
     * Used when entering character selection from Main Menu -> My Party.
     */
    public void setGameData(Game game) {
        this.game = game;
        this.guild = game.getGuild();
        this.difficulty = game.getDifficulty();
        this.availableGold = guild.getGold();

        createCharacterList();
        displayCharacters();
    }

    private void createCharacterList() {
        allCharacters.clear();

        Faction playerFaction = guild.getPlayerFaction();

        allCharacters.add(new Adventurer(
                "Baalkux", 110, 18, 8, 20,
                Faction.AATROX, playerFaction,
                "A brutal Darkin warrior with strong attack power."
        ));

        allCharacters.add(new Adventurer(
                "Horazi", 90, 20, 5, 25,
                Faction.XOLAANI, playerFaction,
                "A celestial marksman with high damage but lower defense."
        ));

        allCharacters.add(new Adventurer(
                "Ibaaros", 130, 14, 12, 20,
                Faction.AATROX, playerFaction,
                "A tough frontline fighter with high health and defense."
        ));

        allCharacters.add(new Adventurer(
                "Joraal", 120, 15, 11, 20,
                Faction.AATROX, playerFaction,
                "A loyal shield-bearer who protects the party."
        ));

        allCharacters.add(new Adventurer(
                "Naafiri", 85, 22, 4, 25,
                Faction.XOLAANI, playerFaction,
                "A fast assassin with very high attack but low defense."
        ));

        allCharacters.add(new Adventurer(
                "Rhaast", 115, 19, 7, 25,
                Faction.AATROX, playerFaction,
                "An aggressive fighter who thrives in dangerous battles."
        ));

        allCharacters.add(new Adventurer(
                "Taarosh", 150, 12, 14, 20,
                Faction.AATROX, playerFaction,
                "A heavy tank with high health and strong defense."
        ));

        allCharacters.add(new Adventurer(
                "Varus", 95, 21, 6, 25,
                Faction.XOLAANI, playerFaction,
                "A ranged attacker with strong burst damage."
        ));

        allCharacters.add(new Adventurer(
                "Zaahen", 105, 17, 9, 20,
                Faction.NEUTRAL, playerFaction,
                "A balanced warrior who is not tied strongly to either side."
        ));
    }

    private void displayCharacters() {
        characterGrid.getChildren().clear();
        characterCards.clear();
        selectedCharacters.clear();

        for (int i = 0; i < allCharacters.size(); i++) {
            Adventurer adventurer = allCharacters.get(i);
            VBox card = createCharacterCard(adventurer);

            int column = i % 3;
            int row = i / 3;

            characterGrid.add(card, column, row);
            characterCards.add(card);

            if (isAlreadyInParty(adventurer)) {
                selectedCharacters.add(adventurer);
                highlightCard(card);
            }
        }

        updateSelectedCount();
        updateGoldLabel();
    }

    private void updateGoldLabel() {
        if (goldLabel != null) {
            goldLabel.setText("Gold: " + availableGold);
        }
    }

    private boolean isAlreadyInParty(Adventurer adventurer) {
        if (guild == null) {
            return false;
        }

        for (Adventurer partyMember : guild.getMainParty()) {
            if (partyMember.getName().equals(adventurer.getName())) {
                return true;
            }
        }

        return false;
    }

    private VBox createCharacterCard(Adventurer adventurer) {
        VBox card = new VBox(5);
        card.setPrefWidth(200);
        card.setMinWidth(200);
        card.setMaxWidth(200);
        card.setPrefHeight(105);
        card.setMinHeight(105);
        card.setMaxHeight(105);
        card.setStyle(getNormalCardStyle());

        javafx.scene.layout.HBox content = new javafx.scene.layout.HBox(6);
        content.setStyle("-fx-alignment: center;");

        VBox leftBox = new VBox(4);
        leftBox.setStyle("-fx-alignment: center;");
        leftBox.setPrefWidth(60);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(75);
        imageView.setFitHeight(75);
        imageView.setPreserveRatio(true);

        String imagePath = "/images/" + adventurer.getName() + ".png";
        Image image = new Image(getClass().getResource(imagePath).toExternalForm());
        imageView.setImage(image);

        Label nameLabel = new Label(adventurer.getName());
        nameLabel.setTextFill(Color.GOLD);
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

        leftBox.getChildren().addAll(imageView, nameLabel);

        VBox rightBox = new VBox(4);
        rightBox.setStyle("-fx-alignment: center-left;");

        Adventurer displayAdventurer = getRealPartyMember(adventurer);

        Label statsLabel = new Label(
                "HP: " + displayAdventurer.getCurrentHealth() + " / " + displayAdventurer.getMaxHealth() + "\n" +
                        "ATK: " + displayAdventurer.getAttack() + "\n" +
                        "DEF: " + displayAdventurer.getDefense() + "\n" +
                        "Pay: " + displayAdventurer.getPay() + "\n" +
                        "Loyalty: " + displayAdventurer.getLoyalty()
        );
        statsLabel.setTextFill(Color.WHITE);
        statsLabel.setStyle("-fx-font-size: 11px;");

        rightBox.getChildren().add(statsLabel);

        content.getChildren().addAll(leftBox, rightBox);
        card.getChildren().add(content);

        card.setOnMouseClicked(event -> toggleCharacterSelection(adventurer, card));

        return card;
    }

    private Adventurer getRealPartyMember(Adventurer adventurer) {
        if (guild == null) {
            return adventurer;
        }

        for (Adventurer partyMember : guild.getMainParty()) {
            if (partyMember.getName().equals(adventurer.getName())) {
                return partyMember;
            }
        }

        return adventurer;
    }

    private void toggleCharacterSelection(Adventurer adventurer, VBox card) {
        warningLabel.setText("");

        if (selectedCharacters.contains(adventurer)) {
            selectedCharacters.remove(adventurer);
            unhighlightCard(card);

            // Refund when any selected character is removed
            availableGold += adventurer.getPay();

        } else {
            if (selectedCharacters.size() >= MAX_SELECTED) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("You can choose up to 5 warriors only.");
                return;
            }

            if (availableGold < adventurer.getPay()) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("Not enough gold to hire " + adventurer.getName() + ".");
                return;
            }

            // Pay when any character is selected
            availableGold -= adventurer.getPay();

            selectedCharacters.add(adventurer);
            highlightCard(card);
        }

        updateSelectedCount();
        updateGoldLabel();
    }

    private void highlightCard(VBox card) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(25);
        glow.setSpread(0.5);

        card.setEffect(glow);
        card.setStyle(getSelectedCardStyle());
    }

    private void unhighlightCard(VBox card) {
        card.setEffect(null);
        card.setStyle(getNormalCardStyle());
    }

    private String getNormalCardStyle() {
        return "-fx-background-color: rgba(30, 25, 20, 0.85);" +
                "-fx-border-color: #6b5a2b;" +
                "-fx-border-width: 2;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 7;" +
                "-fx-alignment: center;";
    }

    private String getSelectedCardStyle() {
        return "-fx-background-color: rgba(60, 45, 20, 0.95);" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 7;" +
                "-fx-alignment: center;";
    }

    private void updateSelectedCount() {
        selectedCountLabel.setText("Selected: " + selectedCharacters.size() + " / " + MAX_SELECTED);
    }

    @FXML
    public void onConfirmSelection() {
        if (selectedCharacters.isEmpty()) {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Choose at least 1 warrior.");
            return;
        }

        int goldDifference = availableGold - guild.getGold();

        if (goldDifference > 0) {
            guild.addGold(goldDifference);
        } else if (goldDifference < 0) {
            boolean paid = guild.spendGold(-goldDifference);

            if (!paid) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("Payment failed.");
                return;
            }
        }

        guild.getMainParty().clear();

        for (Adventurer adventurer : selectedCharacters) {
            guild.addToMainParty(adventurer);
        }

        if (game == null) {
            game = new Game(guild, difficulty);
        }

        navigateToMainMenu();
    }

    @FXML
    public void onBack() {
        if (game != null) {
            navigateToMainMenu();
        } else {
            navigateToSetup();
        }
    }

    private void navigateToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setGameData(game);

            Stage stage = (Stage) characterGrid.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Error loading main menu.");
        }
    }

    private void navigateToSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/setup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) characterGrid.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Setup");

        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Error returning to setup screen.");
        }
    }
}