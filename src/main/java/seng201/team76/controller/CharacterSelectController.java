package seng201.team76.controller;

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
import seng201.team76.models.Adventurer;
import seng201.team76.models.Difficulty;
import seng201.team76.models.Faction;
import seng201.team76.models.Game;
import seng201.team76.models.Guild;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the character selection screen.
 * Uses a carousel layout so the player views one warrior at a time.
 * The guild always includes the chosen main character, then the player chooses 1 to 4 companions.
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
public class CharacterSelectController implements GameDataReceiver {

    /**
     * Creates the character selection controller.
     */
    public CharacterSelectController() {
    }

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
    private static final int MIN_COMPANIONS = 1;
    private static final int MAX_COMPANIONS = 4;
    private static final int MAX_SELECTED = 1 + MAX_COMPANIONS;

    /**
     * Sets up the character selection screen after the FXML loads.
     */
    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        selectedCountLabel.setText("Companions: 0 / " + MAX_COMPANIONS);
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
     *
     * @param guild The player's guild
     * @param difficulty The selected difficulty
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
     *
     * @param game The current game
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

        allCharacters.add(guild.getMainCharacter());

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

        allCharacters.removeIf(adventurer -> !guild.isMainCharacter(adventurer)
                && guild.isPermanentlyUnavailable(adventurer));
    }

    private void displayCharacters() {
        selectedCharacters.clear();

        for (Adventurer adventurer : allCharacters) {
            if (isAlreadyInParty(adventurer)) {
                selectedCharacters.add(getRealPartyMember(adventurer));
            }
        }

        Adventurer mainCharacter = guild.getMainCharacter();
        if (findSelectedByName(mainCharacter.getName()) == null) {
            selectedCharacters.add(0, mainCharacter);
        }

        if (instructionLabel != null) {
            instructionLabel.setText("Your guild starts with " + mainCharacter.getName() + ". Choose 1 to 4 companions.");
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
            String imagePath = "/images/" + displayAdventurer.getName().toLowerCase() + ".png";
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

    /**
     * Moves the carousel to the previous character.
     */
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

    /**
     * Moves the carousel to the next character.
     */
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

    /**
     * Selects or removes the currently displayed character.
     */
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

        if (guild.isMainCharacter(adventurer)) {
            warningLabel.setTextFill(Color.GOLD);
            warningLabel.setText(adventurer.getName() + " is your main character and must stay in the guild.");
            return;
        }

        if (guild.isPermanentlyUnavailable(adventurer)) {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText(adventurer.getName() + " is gone and cannot be recruited again.");
            return;
        }

        Adventurer selected = findSelectedByName(adventurer.getName());

        if (selected != null) {
            selectedCharacters.remove(selected);
        } else {
            if (selectedCharacters.size() >= MAX_SELECTED) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("You can choose up to 4 companions with your main character.");
                return;
            }

            Adventurer actualSelection = getRealPartyMember(adventurer);
            List<Adventurer> previewSelection = new ArrayList<>(selectedCharacters);
            previewSelection.add(actualSelection);

            if (guild.previewGoldAfterPartySelection(previewSelection) < 0) {
                warningLabel.setTextFill(Color.RED);
                warningLabel.setText("Not enough gold to hire " + adventurer.getName() + ".");
                return;
            }

            selectedCharacters.add(actualSelection);
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

        if (guild != null && guild.isMainCharacter(adventurer)) {
            selectButton.setDisable(true);
            selectButton.setText("Main Character");
            selectButton.setStyle(getMainCharacterButtonStyle());
            return;
        }

        if (guild != null && guild.isPermanentlyUnavailable(adventurer)) {
            selectButton.setDisable(true);
            selectButton.setText("Gone");
            selectButton.setStyle(getMainCharacterButtonStyle());
            return;
        }

        selectButton.setDisable(false);

        if (findSelectedByName(adventurer.getName()) != null) {
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

    private String getMainCharacterButtonStyle() {
        return "-fx-font-size: 15px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-color: #4d3a14;" +
                "-fx-text-fill: #ffd86a;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: gold;" +
                "-fx-border-radius: 8;";
    }

    private void updateGoldLabel() {
        if (goldLabel != null && guild != null) {
            int previewGold = guild.previewGoldAfterPartySelection(selectedCharacters);
            goldLabel.setText("Gold: " + previewGold);
        }
    }

    private void updateSelectedCount() {
        selectedCountLabel.setText("Companions: " + getCompanionCount() + " / " + MAX_COMPANIONS);
    }

    private int getCompanionCount() {
        int count = 0;
        for (Adventurer selected : selectedCharacters) {
            if (!guild.isMainCharacter(selected)) {
                count++;
            }
        }
        return count;
    }

    private boolean hasValidPartySize() {
        int companionCount = getCompanionCount();
        return companionCount >= MIN_COMPANIONS && companionCount <= MAX_COMPANIONS
                && findSelectedByName(guild.getMainCharacter().getName()) != null;
    }

    private Adventurer findSelectedByName(String name) {
        for (Adventurer selected : selectedCharacters) {
            if (selected.getName().equals(name)) {
                return selected;
            }
        }
        return null;
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

        if (guild.isMainCharacter(adventurer)) {
            return guild.getMainCharacter();
        }

        for (Adventurer partyMember : guild.getMainParty()) {
            if (partyMember.getName().equals(adventurer.getName())) {
                return partyMember;
            }
        }

        return adventurer;
    }

    /**
     * Confirms the selected party and opens the next screen.
     */
    @FXML
    public void onConfirmSelection() {
        if (!hasValidPartySize()) {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Choose " + guild.getMainCharacter().getName() + " plus at least 1 companion. Maximum is 4 companions.");
            return;
        }

        boolean updated = guild.replaceMainPartyWithSelection(new ArrayList<>(selectedCharacters));

        if (!updated) {
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Payment failed or party changes are locked.");
            updateGoldLabel();
            return;
        }

        boolean firstGameCreation = false;

        if (game == null) {
            game = new Game(guild, difficulty);
            firstGameCreation = true;
        }

        if (firstGameCreation) {
            navigateToImperialCommand();
        } else {
            navigateToMainMenu();
        }
    }

    /**
     * Returns to setup or the main menu depending on where this screen was opened from.
     */
    @FXML
    public void onBack() {
        if (game != null) {
            navigateToMainMenu();
        } else {
            navigateToSetup();
        }
    }

    private void navigateToImperialCommand() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/imperialcommand.fxml"));
            Parent root = loader.load();

            ImperialCommandController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Imperial Command");

        } catch (Exception e) {
            e.printStackTrace();
            warningLabel.setTextFill(Color.RED);
            warningLabel.setText("Error loading imperial command scene.");
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
