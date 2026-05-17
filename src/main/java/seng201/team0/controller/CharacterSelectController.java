package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Faction;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the character selection screen.
 * Uses a carousel layout so the player views one warrior at a time.
 * The player can choose up to 5 warriors to join the guild.
 *
 * Displayed character information:
 *  - Name
 *  - HP
 *  - Attack
 *  - Defence
 *  - Description
 *
 * @author Mohammed, Xinyi
 */
public class CharacterSelectController {

    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML private Label titleLabel;
    @FXML private Label instructionLabel;
    @FXML private Label selectedCountLabel;
    @FXML private Label warningLabel;
    @FXML private Label goldLabel;

    @FXML private AnchorPane rootPane;
    @FXML private ImageView backgroundImage;
    @FXML private Pane contentPane;

    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Button selectButton;

    @FXML private ImageView characterImage;
    @FXML private javafx.scene.layout.StackPane characterImageFrame;
    @FXML private Label characterNameLabel;
    @FXML private Label characterStatsLabel;
    @FXML private Label characterDescriptionLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private Guild guild;
    private Difficulty difficulty;
    private Game game;
    private int availableGold;
    private int currentCharacterIndex = 0;

    // ── Character lists ───────────────────────────────────────────────────────
    private final List<Adventurer> allCharacters = new ArrayList<>();
    private final List<Adventurer> selectedCharacters = new ArrayList<>();

    // ── Display ───────────────────────────────────────────────────────────────
    private static final int MAX_SELECTED = 5;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        selectedCountLabel.setText("Selected: 0 / " + MAX_SELECTED);
        warningLabel.setText("");

        // Keep the carousel image area consistent for every character.
        if (characterImage != null) {
            characterImage.setFitWidth(240);
            characterImage.setFitHeight(230);
            characterImage.setPreserveRatio(true);
            characterImage.setSmooth(true);
            characterImage.setCache(true);
        }
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
                "A celestial marksman with high damage but lower defence."
        ));

        allCharacters.add(new Adventurer(
                "Ibaaros", 130, 14, 12, 20,
                Faction.AATROX, playerFaction,
                "A tough frontline fighter with high health and defence."
        ));

        allCharacters.add(new Adventurer(
                "Joraal", 120, 15, 11, 20,
                Faction.AATROX, playerFaction,
                "A loyal shield-bearer who protects the party."
        ));

        allCharacters.add(new Adventurer(
                "Naafiri", 85, 22, 4, 25,
                Faction.XOLAANI, playerFaction,
                "A fast assassin with very high attack but low defence."
        ));

        allCharacters.add(new Adventurer(
                "Rhaast", 115, 19, 7, 25,
                Faction.AATROX, playerFaction,
                "An aggressive fighter who thrives in dangerous battles."
        ));

        allCharacters.add(new Adventurer(
                "Taarosh", 150, 12, 14, 20,
                Faction.AATROX, playerFaction,
                "A heavy tank with high health and strong defence."
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
        selectedCharacters.clear();

        for (Adventurer adventurer : allCharacters) {
            if (isAlreadyInParty(adventurer)) {
                selectedCharacters.add(adventurer);
            }
        }

        currentCharacterIndex = 0;
        updateSelectedCount();
        updateGoldLabel();
        updateCarouselDisplay();
    }

    private void updateCarouselDisplay() {
        if (allCharacters.isEmpty()) {
            characterNameLabel.setText("");
            characterStatsLabel.setText("");
            characterDescriptionLabel.setText("");
            characterImage.setImage(null);
            selectButton.setDisable(true);
            return;
        }

        Adventurer adventurer = allCharacters.get(currentCharacterIndex);
        Adventurer displayAdventurer = getRealPartyMember(adventurer);

        characterNameLabel.setText(displayAdventurer.getName());

        characterStatsLabel.setText(
                "HP: " + displayAdventurer.getMaxHealth() +
                        "    ATK: " + displayAdventurer.getAttack() +
                        "    DEF: " + displayAdventurer.getDefense()
        );

        characterDescriptionLabel.setText(displayAdventurer.getDescription());

        try {
            String imagePath = "/images/" + displayAdventurer.getName() + ".png";
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            characterImage.setImage(image);
            normalizeCharacterImage(displayAdventurer.getName());
        } catch (Exception e) {
            characterImage.setImage(null);
        }

        applyImageGlow();
        updateSelectButton();
    }

    private void normalizeCharacterImage(String characterName) {
        // A fixed fit box prevents the UI from resizing when images have different dimensions.
        characterImage.setFitWidth(240);
        characterImage.setFitHeight(230);
        characterImage.setPreserveRatio(true);
        characterImage.setSmooth(true);

        // Reset any previous character-specific transform before applying the new one.
        characterImage.setScaleX(1.0);
        characterImage.setScaleY(1.0);
        characterImage.setTranslateX(0);
        characterImage.setTranslateY(0);

        applyCharacterImageTransform(characterName);
    }

    /**
     * Some character PNGs have more transparent padding than others.
     * This method lets us visually balance each character without editing image files.
     */
    private void applyCharacterImageTransform(String characterName) {
        characterImage.setScaleX(1.0);
        characterImage.setScaleY(1.0);
        characterImage.setTranslateX(0);
        characterImage.setTranslateY(0);
    }

    private void applyImageGlow() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.color(0.95, 0.72, 0.18, 0.65));
        glow.setRadius(30);
        glow.setSpread(0.25);
        characterImage.setEffect(glow);
    }

    @FXML
    public void onPreviousCharacter() {
        if (allCharacters.isEmpty()) {
            return;
        }

        currentCharacterIndex--;

        if (currentCharacterIndex < 0) {
            currentCharacterIndex = allCharacters.size() - 1;
        }

        warningLabel.setText("");
        updateCarouselDisplay();
    }

    @FXML
    public void onNextCharacter() {
        if (allCharacters.isEmpty()) {
            return;
        }

        currentCharacterIndex++;

        if (currentCharacterIndex >= allCharacters.size()) {
            currentCharacterIndex = 0;
        }

        warningLabel.setText("");
        updateCarouselDisplay();
    }

    @FXML
    public void onSelectCurrentCharacter() {
        if (allCharacters.isEmpty()) {
            return;
        }

        Adventurer adventurer = allCharacters.get(currentCharacterIndex);
        toggleCharacterSelection(adventurer);
    }

    private void toggleCharacterSelection(Adventurer adventurer) {
        warningLabel.setText("");

        if (selectedCharacters.contains(adventurer)) {
            selectedCharacters.remove(adventurer);
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

            availableGold -= adventurer.getPay();
            selectedCharacters.add(adventurer);
        }

        updateSelectedCount();
        updateGoldLabel();
        updateSelectButton();
    }

    private void updateSelectButton() {
        if (allCharacters.isEmpty()) {
            return;
        }

        Adventurer adventurer = allCharacters.get(currentCharacterIndex);

        if (selectedCharacters.contains(adventurer)) {
            selectButton.setText("Remove");
            selectButton.setStyle(getRemoveButtonStyle());
        } else {
            selectButton.setText("Select");
            selectButton.setStyle(getSelectButtonStyle());
        }
    }

    private String getSelectButtonStyle() {
        return "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #d6b63f;" +
                "-fx-text-fill: black;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #fff1a8;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;";
    }

    private String getRemoveButtonStyle() {
        return "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #7a1f1f;" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #ff9999;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;";
    }

    private void updateGoldLabel() {
        if (goldLabel != null) {
            goldLabel.setText("Gold: " + availableGold);
        }
    }

    private void updateSelectedCount() {
        selectedCountLabel.setText("Selected: " + selectedCharacters.size() + " / " + MAX_SELECTED);
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

            Stage stage = (Stage) rootPane.getScene().getWindow();
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

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Setup");

        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Error returning to setup screen.");
        }
    }
}
