package seng201.team0.controller;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Boss;
import seng201.team0.models.BossFight;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;
import seng201.team0.models.Quest;
import seng201.team0.models.Quest1;
import javafx.application.Platform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Quest 1 battle screen.
 * Shows party and boss directly on the battle background.
 * Uses a memory-card attack system with automatic countdown.
 *
 * @author Mohammed, Xinyi
 */
public class Quest1Controller {

    // ----------------------------- FXML fields -----------------------------

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;

    @FXML private Pane partyPane;
    @FXML private Pane bossPane;
    @FXML private HBox memoryArea;

    @FXML private Label statusLabel;
    @FXML private Label timerLabel;

    @FXML private Button attackButton;
    @FXML private Button pauseButton;

    @FXML private Rectangle pauseOverlay;
    @FXML private VBox pausePopup;

    // ----------------------------- Game state -----------------------------

    private Game game;
    private Guild guild;
    private Quest quest1;
    private BossFight currentFight;
    private Adventurer currentAttacker;

    private int fightIndex = 0;
    private int attackerIndex = 0;
    private int timeRemaining;
    private boolean battleResultShown = false;

    // ----------------------------- Potion state -----------------------------

    @FXML private HBox potionBar;

    @FXML private VBox smallPotionBox;
    @FXML private VBox partyPotionBox;
    @FXML private VBox fullRestoreBox;

    @FXML private ImageView smallPotionImage;
    @FXML private ImageView partyPotionImage;
    @FXML private ImageView fullRestoreImage;

    @FXML private Label smallPotionCountLabel;
    @FXML private Label partyPotionCountLabel;
    @FXML private Label fullRestoreCountLabel;

    private boolean potionUsedThisTurn = false;

    private enum SelectedPotion {
        NONE,
        SMALL,
        PARTY,
        FULL
    }

    private SelectedPotion selectedPotion = SelectedPotion.NONE;

    // ----------------------------- Memory game state -----------------------------

    private final List<Integer> correctPattern = new ArrayList<>();
    private final List<Integer> playerInput = new ArrayList<>();

    private static final int TOTAL_OPTIONS = 6;
    private static final int PATTERN_LENGTH = 3;

    // ----------------------------- UI state -----------------------------

    private final Map<Adventurer, StackPane> partySpriteMap = new HashMap<>();
    private StackPane bossSprite;

    private Timeline countdownTimeline;
    private PauseTransition activePauseTransition;

    private boolean inputEnabled = false;
    private boolean paused = false;

    private boolean inputEnabledBeforePause = false;
    private boolean attackButtonEnabledBeforePause = false;

    // ----------------------------- Retry / return state -----------------------------

    private final List<Adventurer> questStartParty = new ArrayList<>();
    private final Map<String, Integer> questStartHealth = new HashMap<>();

    // ----------------------------- Initialize -----------------------------

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        loadPotionImages();

        if (attackButton != null) {
            attackButton.setVisible(false);
            attackButton.setDisable(true);
        }

        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
        }

        if (pausePopup != null) {
            pausePopup.setVisible(false);
        }
    }

    private void showQuestStartPopup() {
        StackPane overlay = new StackPane();
        overlay.setPrefSize(rootPane.getPrefWidth(), rootPane.getPrefHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.45);");
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);

        VBox box = new VBox(15);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(360);
        box.setPrefHeight(160);
        box.setMaxWidth(360);
        box.setMaxHeight(160);
        box.setStyle(
                "-fx-background-color: rgba(40, 30, 20, 0.95);" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 22;" +
                        "-fx-padding: 25;"
        );

        Label titleLabel = new Label("Quest 1: Icathia");
        titleLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;"
        );

        box.getChildren().add(titleLabel);
        overlay.getChildren().add(box);

        rootPane.getChildren().add(overlay);

        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> {
            rootPane.getChildren().remove(overlay);
            prepareNextAttacker();
        });
        delay.play();
    }

    private void showBattleResultPopup(boolean playerWon) {
        if (battleResultShown) {
            return;
        }

        battleResultShown = true;

        StackPane overlay = new StackPane();
        overlay.setPrefSize(rootPane.getPrefWidth(), rootPane.getPrefHeight());
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.55);");
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);

        VBox box = new VBox(18);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(380);
        box.setPrefHeight(playerWon ? 220 : 280);
        box.setMaxWidth(380);
        box.setMaxHeight(playerWon ? 220 : 280);
        box.setStyle(
                "-fx-background-color: rgba(35, 25, 20, 0.96);" +
                        "-fx-background-radius: 24;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 24;" +
                        "-fx-padding: 30;"
        );

        Label resultLabel = new Label(playerWon ? "You win!" : "You lose!");
        resultLabel.setStyle(
                "-fx-text-fill: gold;" +
                        "-fx-font-size: 38px;" +
                        "-fx-font-weight: bold;"
        );

        Button mainMenuButton = createPopupButton("Return to Main Menu");
        mainMenuButton.setOnAction(event -> goToMainMenu());

        box.getChildren().add(resultLabel);

        if (!playerWon) {
            Button retryButton = createPopupButton("Retry");
            retryButton.setOnAction(event -> {
                rootPane.getChildren().remove(overlay);
                onRetry();
            });
            box.getChildren().add(retryButton);
        }

        box.getChildren().add(mainMenuButton);

        overlay.getChildren().add(box);
        rootPane.getChildren().add(overlay);
    }

    private Button createPopupButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(220);
        button.setPrefHeight(45);
        button.setStyle(
                "-fx-background-color: rgba(120, 80, 20, 0.85);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;" +
                        "-fx-text-fill: gold;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        );

        button.setOnMouseEntered(event -> button.setStyle(
                "-fx-background-color: gold;" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;" +
                        "-fx-text-fill: black;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        ));

        button.setOnMouseExited(event -> button.setStyle(
                "-fx-background-color: rgba(120, 80, 20, 0.85);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: gold;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 16;" +
                        "-fx-text-fill: gold;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;"
        ));

        return button;
    }

    private void loadPotionImages() {
        try {
            smallPotionImage.setImage(new Image(
                    getClass().getResource("/images/silver_potion.png").toExternalForm()
            ));
            partyPotionImage.setImage(new Image(
                    getClass().getResource("/images/gold_potion.png").toExternalForm()
            ));
            fullRestoreImage.setImage(new Image(
                    getClass().getResource("/images/purple_potion.png").toExternalForm()
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        smallPotionBox.setOnMouseClicked(e -> onPotionClicked(SelectedPotion.SMALL));
        partyPotionBox.setOnMouseClicked(e -> onPotionClicked(SelectedPotion.PARTY));
        fullRestoreBox.setOnMouseClicked(e -> onPotionClicked(SelectedPotion.FULL));
    }

    public void setGameData(Game game) {
        this.game = game;
        this.guild = game.getGuild();

        this.quest1 = new Quest1(game.getDifficulty());
        this.fightIndex = 0;
        this.attackerIndex = 0;
        this.currentFight = quest1.getBossFights().get(fightIndex);

        saveQuestStartState();

        setupPartyDisplay();
        setupBossDisplay();

        Platform.runLater(this::showQuestStartPopup);
    }

    // ----------------------------- Save / restore state -----------------------------

    private void saveQuestStartState() {
        questStartParty.clear();
        questStartHealth.clear();

        for (Adventurer adventurer : guild.getMainParty()) {
            questStartParty.add(adventurer);
            questStartHealth.put(adventurer.getName(), adventurer.getCurrentHealth());
        }
    }

    private void restoreQuestStartState() {
        guild.getMainParty().clear();

        for (Adventurer adventurer : questStartParty) {
            Integer startHp = questStartHealth.get(adventurer.getName());

            if (startHp != null) {
                adventurer.setCurrentHealth(startHp);
            }

            guild.getMainParty().add(adventurer);
        }
    }

    private void resetQuest1State() {
        stopAllTimers();

        restoreQuestStartState();

        this.quest1 = new Quest1(game.getDifficulty());
        this.fightIndex = 0;
        this.attackerIndex = 0;
        this.currentFight = quest1.getBossFights().get(fightIndex);

        battleResultShown = false;

        correctPattern.clear();
        playerInput.clear();
        memoryArea.getChildren().clear();

        paused = false;
        inputEnabled = false;
        potionUsedThisTurn = false;
        selectedPotion = SelectedPotion.NONE;

        pauseOverlay.setVisible(false);
        pausePopup.setVisible(false);

        attackButton.setVisible(false);
        attackButton.setDisable(true);
        timerLabel.setText("");

        updatePotionDisplay(false);
        setupPartyDisplay();
        setupBossDisplay();
        prepareNextAttacker();
    }

    // ----------------------------- Party display -----------------------------

    private void setupPartyDisplay() {
        partyPane.getChildren().clear();
        partySpriteMap.clear();

        List<Adventurer> party = guild.getMainParty();

        for (int i = 0; i < party.size(); i++) {
            Adventurer adventurer = party.get(i);
            StackPane sprite = createPartySprite(adventurer);

            double[] position = getPartyPosition(i);

            sprite.setLayoutX(position[0]);
            sprite.setLayoutY(position[1]);

            partyPane.getChildren().add(sprite);
            partySpriteMap.put(adventurer, sprite);
        }
    }

    private double[] getPartyPosition(int index) {
        double leftX = 0;
        double middleX = 135;
        double rightX = 270;

        double topY = 0;
        double bottomY = 160;

        switch (index) {
            case 0:
                return new double[] { leftX, topY };
            case 1:
                return new double[] { leftX, bottomY };
            case 2:
                return new double[] { middleX, topY };
            case 3:
                return new double[] { middleX, bottomY };
            case 4:
                return new double[] { rightX, topY };
            default:
                return new double[] { rightX, bottomY };
        }
    }

    private StackPane createPartySprite(Adventurer adventurer) {
        StackPane wrapper = new StackPane();
        wrapper.setPrefWidth(115);
        wrapper.setPrefHeight(155);

        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: transparent;");

        StackPane hpBar = createHealthBar(adventurer, "#2cff5a");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);

        try {
            imageView.setImage(new Image(
                    getClass().getResource("/images/" + adventurer.getName() + ".png").toExternalForm()
            ));
        } catch (Exception e) {
            imageView.setImage(null);
        }

        Label nameLabel = new Label(adventurer.getName());
        nameLabel.setTextFill(Color.LIMEGREEN);
        nameLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 5, 0.8, 0, 0);"
        );

        box.getChildren().addAll(hpBar, imageView, nameLabel);
        wrapper.getChildren().add(box);

        return wrapper;
    }

    // ----------------------------- Boss display -----------------------------

    private void setupBossDisplay() {
        bossPane.getChildren().clear();

        Boss boss = currentFight.getBoss();

        bossSprite = createBossSprite(boss);
        bossSprite.setLayoutX(120);
        bossSprite.setLayoutY(30);

        bossPane.getChildren().add(bossSprite);
    }

    private StackPane createBossSprite(Boss boss) {
        StackPane wrapper = new StackPane();
        wrapper.setPrefWidth(170);
        wrapper.setPrefHeight(220);

        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: transparent;");

        StackPane hpBar = createHealthBar(boss, "#ff3333");
        hpBar.setPrefWidth(130);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(155);
        imageView.setFitHeight(155);
        imageView.setPreserveRatio(true);

        try {
            imageView.setImage(new Image(
                    getClass().getResource("/images/" + boss.getName() + ".png").toExternalForm()
            ));
        } catch (Exception e) {
            imageView.setImage(null);
        }

        Label nameLabel = new Label(boss.getName());
        nameLabel.setTextFill(Color.RED);
        nameLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 6, 0.8, 0, 0);"
        );

        box.getChildren().addAll(hpBar, imageView, nameLabel);
        wrapper.getChildren().add(box);

        return wrapper;
    }

    private double getHealthPercent(seng201.team0.models.Character character) {
        if (character.getMaxHealth() <= 0) {
            return 0;
        }

        return (double) character.getCurrentHealth() / character.getMaxHealth();
    }

    private StackPane createHealthBar(seng201.team0.models.Character character, String color) {
        StackPane healthStack = new StackPane();
        healthStack.setPrefWidth(100);
        healthStack.setPrefHeight(18);

        ProgressBar hpBar = new ProgressBar(getHealthPercent(character));
        hpBar.setPrefWidth(100);
        hpBar.setPrefHeight(18);
        hpBar.setStyle("-fx-accent: " + color + ";");

        Label hpText = new Label(character.getCurrentHealth() + " / " + character.getMaxHealth());
        hpText.setTextFill(Color.WHITE);
        hpText.setStyle(
                "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 4, 0.9, 0, 0);"
        );

        healthStack.getChildren().addAll(hpBar, hpText);
        return healthStack;
    }

    // ----------------------------- Turn flow -----------------------------

    private void prepareNextAttacker() {
        if (currentFight.isFightOver(guild)) {
            finishCurrentFight();
            return;
        }

        if (guild.getMainParty().isEmpty()) {
            statusLabel.setText("Your party has fallen.");
            attackButton.setVisible(false);
            attackButton.setDisable(true);
            updatePotionDisplay(false);
            return;
        }

        if (attackerIndex >= guild.getMainParty().size()) {
            attackerIndex = 0;
        }

        currentAttacker = guild.getMainParty().get(attackerIndex);

        potionUsedThisTurn = false;
        selectedPotion = SelectedPotion.NONE;
        updatePotionDisplay(false);

        setupPartyDisplay();
        setupBossDisplay();
        highlightCurrentAttacker();

        startReadyCountdown();
    }

    private void highlightCurrentAttacker() {
        for (StackPane sprite : partySpriteMap.values()) {
            sprite.setEffect(null);
            sprite.setScaleX(1.0);
            sprite.setScaleY(1.0);
        }

        StackPane currentSprite = partySpriteMap.get(currentAttacker);

        if (currentSprite != null) {
            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setRadius(35);
            glow.setSpread(0.6);

            currentSprite.setEffect(glow);
            currentSprite.setScaleX(1.08);
            currentSprite.setScaleY(1.08);
        }
    }

    // ----------------------------- Countdown -----------------------------

    private void startReadyCountdown() {
        inputEnabled = false;
        attackButton.setVisible(false);
        attackButton.setDisable(true);
        memoryArea.getChildren().clear();
        updatePotionDisplay(false);

        statusLabel.setText("Ready?");

        startCountdown(3, () -> {
            generatePattern();
            showPatternForTwoSeconds();
        });
    }

    private void startCountdown(int seconds, Runnable onFinished) {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        timeRemaining = seconds;
        timerLabel.setText(String.valueOf(timeRemaining));

        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeRemaining--;
                    timerLabel.setText(String.valueOf(timeRemaining));

                    if (timeRemaining <= 0) {
                        countdownTimeline.stop();
                        timerLabel.setText("");
                        onFinished.run();
                    }
                })
        );

        countdownTimeline.setCycleCount(seconds);
        countdownTimeline.play();
    }

    private void stopAllTimers() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        if (activePauseTransition != null) {
            activePauseTransition.stop();
        }
    }

    // ----------------------------- Memory attack -----------------------------

    private void generatePattern() {
        correctPattern.clear();
        playerInput.clear();

        List<Integer> allOptions = new ArrayList<>();

        for (int i = 0; i < TOTAL_OPTIONS; i++) {
            allOptions.add(i);
        }

        Collections.shuffle(allOptions);

        for (int i = 0; i < PATTERN_LENGTH; i++) {
            correctPattern.add(allOptions.get(i));
        }
    }

    private void showPatternForTwoSeconds() {
        memoryArea.getChildren().clear();
        statusLabel.setText("Remember!");

        attackButton.setVisible(false);
        attackButton.setDisable(true);
        updatePotionDisplay(false);

        for (int imageIndex : correctPattern) {
            StackPane card = createMemoryCard(imageIndex);
            memoryArea.getChildren().add(card);
        }

        activePauseTransition = new PauseTransition(Duration.seconds(2));
        activePauseTransition.setOnFinished(e -> showSixOptions());
        activePauseTransition.play();
    }

    private void showSixOptions() {
        memoryArea.getChildren().clear();
        playerInput.clear();

        statusLabel.setText("Choose cards in order");

        attackButton.setVisible(true);
        attackButton.setDisable(true);

        for (int i = 0; i < TOTAL_OPTIONS; i++) {
            StackPane card = createMemoryCard(i);
            final int clickedIndex = i;

            card.setOnMouseClicked(event -> handleImageClick(clickedIndex, card));

            memoryArea.getChildren().add(card);
        }

        inputEnabled = true;
        updatePotionDisplay(true);

        startCountdown(8, () -> {
            if (inputEnabled) {
                inputEnabled = false;
                attackButton.setDisable(true);
                attackButton.setVisible(false);

                updatePotionDisplay(false);
                selectedPotion = SelectedPotion.NONE;
                resolvePlayerAttack(false);
            }
        });
    }

    private StackPane createMemoryCard(int imageIndex) {
        StackPane card = new StackPane();
        card.setPrefWidth(95);
        card.setPrefHeight(95);

        card.setStyle(
                "-fx-background-color: rgba(0,0,0,0.45);" +
                        "-fx-border-color: #d6b63f;" +
                        "-fx-border-width: 2;" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 5;"
        );

        ImageView imageView = new ImageView();
        imageView.setFitWidth(82);
        imageView.setFitHeight(82);
        imageView.setPreserveRatio(true);

        try {
            imageView.setImage(new Image(
                    getClass().getResource("/images/img" + imageIndex + ".png").toExternalForm()
            ));
        } catch (Exception e) {
            imageView.setImage(null);
        }

        Label numberLabel = new Label("");
        numberLabel.setVisible(false);
        numberLabel.setTextFill(Color.BLACK);
        numberLabel.setStyle(
                "-fx-background-color: gold;" +
                        "-fx-font-size: 15px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 2 7 2 7;"
        );

        StackPane.setAlignment(numberLabel, Pos.TOP_RIGHT);

        card.getChildren().addAll(imageView, numberLabel);
        card.setUserData(numberLabel);
        card.getProperties().put("imageIndex", imageIndex);

        return card;
    }

    private void handleImageClick(int clickedIndex, StackPane card) {
        if (!inputEnabled || paused) {
            return;
        }

        if (playerInput.contains(clickedIndex)) {
            playerInput.remove(Integer.valueOf(clickedIndex));
            refreshMemoryCardSelectionStyles();
            return;
        }

        if (playerInput.size() >= PATTERN_LENGTH) {
            return;
        }

        playerInput.add(clickedIndex);
        refreshMemoryCardSelectionStyles();
    }

    private void refreshMemoryCardSelectionStyles() {
        for (javafx.scene.Node node : memoryArea.getChildren()) {
            if (!(node instanceof StackPane)) {
                continue;
            }

            StackPane card = (StackPane) node;
            Object imageIndexObject = card.getProperties().get("imageIndex");

            if (!(imageIndexObject instanceof Integer)) {
                continue;
            }

            int imageIndex = (Integer) imageIndexObject;
            Label numberLabel = (Label) card.getUserData();

            int selectedPosition = playerInput.indexOf(imageIndex);

            if (selectedPosition >= 0) {
                numberLabel.setText(String.valueOf(selectedPosition + 1));
                numberLabel.setVisible(true);

                DropShadow glow = new DropShadow();
                glow.setColor(Color.GOLD);
                glow.setRadius(25);
                glow.setSpread(0.5);

                card.setEffect(glow);
                card.setStyle(
                        "-fx-background-color: rgba(80,60,20,0.65);" +
                                "-fx-border-color: gold;" +
                                "-fx-border-width: 3;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 5;"
                );
            } else {
                numberLabel.setText("");
                numberLabel.setVisible(false);

                card.setEffect(null);
                card.setStyle(
                        "-fx-background-color: rgba(0,0,0,0.45);" +
                                "-fx-border-color: #d6b63f;" +
                                "-fx-border-width: 2;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 5;"
                );
            }
        }

        attackButton.setDisable(playerInput.size() != PATTERN_LENGTH);
    }

    @FXML
    public void onConfirmAttack() {
        if (!inputEnabled) {
            return;
        }

        if (playerInput.size() != PATTERN_LENGTH) {
            return;
        }

        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        timerLabel.setText("");
        inputEnabled = false;
        attackButton.setDisable(true);
        attackButton.setVisible(false);

        boolean success = playerInput.equals(correctPattern);

        resolvePlayerAttack(success);
        updatePotionDisplay(false);
        selectedPotion = SelectedPotion.NONE;
    }

    // ----------------------------- Potion logic -----------------------------

    private void updatePotionDisplay(boolean usable) {
        if (guild == null) {
            return;
        }

        smallPotionCountLabel.setText("x" + guild.getSmallPotionCount());
        partyPotionCountLabel.setText("x" + guild.getPartyPotionCount());
        fullRestoreCountLabel.setText("x" + guild.getFullRestoreCount());

        boolean canUseSmall = usable && !paused && !potionUsedThisTurn && guild.getSmallPotionCount() > 0;
        boolean canUseParty = usable && !paused && !potionUsedThisTurn && guild.getPartyPotionCount() > 0;
        boolean canUseFull = usable && !paused && !potionUsedThisTurn && guild.getFullRestoreCount() > 0;

        updatePotionBoxStyle(smallPotionBox, canUseSmall, selectedPotion == SelectedPotion.SMALL);
        updatePotionBoxStyle(partyPotionBox, canUseParty, selectedPotion == SelectedPotion.PARTY);
        updatePotionBoxStyle(fullRestoreBox, canUseFull, selectedPotion == SelectedPotion.FULL);

        setPotionImageGrey(smallPotionImage, !canUseSmall);
        setPotionImageGrey(partyPotionImage, !canUseParty);
        setPotionImageGrey(fullRestoreImage, !canUseFull);
    }

    private void updatePotionBoxStyle(VBox box, boolean usable, boolean selected) {
        if (selected) {
            box.setStyle(
                    "-fx-alignment: center;" +
                            "-fx-border-color: gold;" +
                            "-fx-border-width: 3;" +
                            "-fx-border-radius: 10;" +
                            "-fx-background-color: rgba(255,215,0,0.18);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 3;" +
                            "-fx-cursor: hand;"
            );
        } else if (usable) {
            box.setStyle(
                    "-fx-alignment: center;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 3;" +
                            "-fx-cursor: hand;"
            );
        } else {
            box.setStyle(
                    "-fx-alignment: center;" +
                            "-fx-border-color: transparent;" +
                            "-fx-padding: 3;"
            );
        }
    }

    private void setPotionImageGrey(ImageView imageView, boolean grey) {
        if (grey) {
            javafx.scene.effect.ColorAdjust adjust = new javafx.scene.effect.ColorAdjust();
            adjust.setSaturation(-1.0);
            adjust.setBrightness(-0.35);
            imageView.setEffect(adjust);
            imageView.setOpacity(0.55);
        } else {
            imageView.setEffect(null);
            imageView.setOpacity(1.0);
        }
    }

    private void onPotionClicked(SelectedPotion potion) {
        if (!inputEnabled || paused || potionUsedThisTurn) {
            return;
        }

        selectedPotion = potion;
        updatePotionDisplay(true);

        boolean used = false;

        switch (potion) {
            case SMALL:
                used = useSmallPotionOnCurrentAttacker();
                break;
            case PARTY:
                used = usePartyPotion();
                break;
            case FULL:
                used = useFullRestore();
                break;
            default:
                break;
        }

        if (used) {
            potionUsedThisTurn = true;
            selectedPotion = SelectedPotion.NONE;

            setupPartyDisplay();
            highlightCurrentAttacker();

            statusLabel.setText("Potion used.");
            updatePotionDisplay(true);
        } else {
            selectedPotion = SelectedPotion.NONE;
            statusLabel.setText("No potion left.");
            updatePotionDisplay(true);
        }
    }

    private boolean useSmallPotionOnCurrentAttacker() {
        if (guild.getSmallPotionCount() <= 0) {
            return false;
        }

        Adventurer lowestHealthAdventurer = getLowestHealthAdventurer();

        if (lowestHealthAdventurer == null) {
            statusLabel.setText("Everyone is already full HP.");
            return false;
        }

        int before = lowestHealthAdventurer.getCurrentHealth();
        int after = Math.min(lowestHealthAdventurer.getMaxHealth(), before + 30);
        int healed = after - before;

        if (healed <= 0) {
            statusLabel.setText("Everyone is already full HP.");
            return false;
        }

        guild.useSmallPotion();
        lowestHealthAdventurer.setCurrentHealth(after);
        showHealNumber(partySpriteMap.get(lowestHealthAdventurer), healed);

        return true;
    }

    private Adventurer getLowestHealthAdventurer() {
        Adventurer lowest = null;

        for (Adventurer adventurer : guild.getMainParty()) {
            if (adventurer.isDead()) {
                continue;
            }

            if (adventurer.getCurrentHealth() >= adventurer.getMaxHealth()) {
                continue;
            }

            if (lowest == null || adventurer.getCurrentHealth() < lowest.getCurrentHealth()) {
                lowest = adventurer;
            }
        }

        return lowest;
    }

    private boolean usePartyPotion() {
        if (guild.getPartyPotionCount() <= 0) {
            return false;
        }

        boolean anyoneHealed = false;
        Map<Adventurer, Integer> healedAmounts = new HashMap<>();

        for (Adventurer adventurer : guild.getMainParty()) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                int before = adventurer.getCurrentHealth();
                int after = Math.min(adventurer.getMaxHealth(), before + 20);
                int healed = after - before;

                if (healed > 0) {
                    adventurer.setCurrentHealth(after);
                    healedAmounts.put(adventurer, healed);
                    anyoneHealed = true;
                }
            }
        }

        if (!anyoneHealed) {
            statusLabel.setText("Everyone is already full HP.");
            return false;
        }

        guild.usePartyPotion();

        for (Map.Entry<Adventurer, Integer> entry : healedAmounts.entrySet()) {
            showHealNumber(partySpriteMap.get(entry.getKey()), entry.getValue());
        }

        return true;
    }

    private boolean useFullRestore() {
        if (guild.getFullRestoreCount() <= 0) {
            return false;
        }

        boolean anyoneHealed = false;
        Map<Adventurer, Integer> healedAmounts = new HashMap<>();

        for (Adventurer adventurer : guild.getMainParty()) {
            if (!adventurer.isDead() && adventurer.getCurrentHealth() < adventurer.getMaxHealth()) {
                int before = adventurer.getCurrentHealth();
                adventurer.resetHealth();
                int healed = adventurer.getCurrentHealth() - before;

                if (healed > 0) {
                    healedAmounts.put(adventurer, healed);
                    anyoneHealed = true;
                }
            }
        }

        if (!anyoneHealed) {
            statusLabel.setText("Everyone is already full HP.");
            return false;
        }

        guild.useFullRestore();

        for (Map.Entry<Adventurer, Integer> entry : healedAmounts.entrySet()) {
            showHealNumber(partySpriteMap.get(entry.getKey()), entry.getValue());
        }

        return true;
    }

    private void showHealNumber(StackPane sprite, int amount) {
        if (sprite == null || amount <= 0) {
            return;
        }

        Label healLabel = new Label("+" + amount);
        healLabel.setTextFill(Color.LIMEGREEN);
        healLabel.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 8, 0.8, 0, 0);"
        );

        StackPane.setAlignment(healLabel, Pos.TOP_CENTER);
        healLabel.setTranslateY(-10);

        sprite.getChildren().add(healLabel);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.9), healLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        TranslateTransition move = new TranslateTransition(Duration.seconds(0.9), healLabel);
        move.setFromY(-10);
        move.setToY(-45);

        ParallelTransition animation = new ParallelTransition(fade, move);
        animation.setOnFinished(e -> sprite.getChildren().remove(healLabel));
        animation.play();
    }

    // ----------------------------- Player attack result -----------------------------

    private void resolvePlayerAttack(boolean success) {
        memoryArea.getChildren().clear();
        updatePotionDisplay(false);

        int bossHpBefore = currentFight.getBoss().getCurrentHealth();
        int damage = currentFight.playerAttack(currentAttacker, success, false, guild);
        int bossHpAfter = currentFight.getBoss().getCurrentHealth();

        String resultText;

        if (!success) {
            resultText = "Attack Fail";
        } else if (damage == 0 || bossHpBefore == bossHpAfter) {
            resultText = "Attack Blocked";
        } else {
            resultText = "Attack Successful";
        }

        showBattleTextSequence(
                "Player Attack",
                resultText,
                () -> {
                    if (success && damage > 0) {
                        showBossDamageNumber(bossHpBefore - bossHpAfter);
                    }

                    currentFight.finishFightIfOver(guild);
                    setupBossDisplay();

                    activePauseTransition = new PauseTransition(Duration.seconds(0.7));
                    activePauseTransition.setOnFinished(e -> afterPlayerAttack());
                    activePauseTransition.play();
                }
        );
    }

    private void afterPlayerAttack() {
        if (currentFight.isFightOver(guild)) {
            finishCurrentFight();
            return;
        }

        showBigCenterText("Boss Attack");

        activePauseTransition = new PauseTransition(Duration.seconds(0.8));
        activePauseTransition.setOnFinished(e -> runBossAttack());
        activePauseTransition.play();
    }

    // ----------------------------- Boss attack result -----------------------------

    private void runBossAttack() {
        Map<Adventurer, Integer> beforeHp = new HashMap<>();

        for (Adventurer member : new ArrayList<>(guild.getMainParty())) {
            beforeHp.put(member, member.getCurrentHealth());
        }

        currentFight.bossTurn(guild);

        boolean bossHit = false;

        for (Adventurer member : beforeHp.keySet()) {
            int before = beforeHp.get(member);
            int after = member.getCurrentHealth();

            if (before > after) {
                bossHit = true;

                StackPane sprite = partySpriteMap.get(member);
                if (sprite != null) {
                    showDamageOnSprite(sprite, before - after);
                }
            }
        }

        String resultText = bossHit ? "Successful" : "Fail";

        showBigCenterText(resultText);

        activePauseTransition = new PauseTransition(Duration.seconds(0.9));
        activePauseTransition.setOnFinished(e -> {
            currentFight.finishFightIfOver(guild);

            setupPartyDisplay();
            setupBossDisplay();

            if (currentFight.isFightOver(guild)) {
                finishCurrentFight();
                return;
            }

            attackerIndex++;
            prepareNextAttacker();
        });
        activePauseTransition.play();
    }

    // ----------------------------- Battle text -----------------------------

    private void showBattleTextSequence(String firstText, String secondText, Runnable onFinished) {
        showBigCenterText(firstText);

        activePauseTransition = new PauseTransition(Duration.seconds(0.8));
        activePauseTransition.setOnFinished(e -> {
            showBigCenterText(secondText);

            activePauseTransition = new PauseTransition(Duration.seconds(0.9));
            activePauseTransition.setOnFinished(ev -> {
                memoryArea.getChildren().clear();
                onFinished.run();
            });
            activePauseTransition.play();
        });

        activePauseTransition.play();
    }

    private void showBigCenterText(String text) {
        statusLabel.setText("");
        memoryArea.getChildren().clear();
        updatePotionDisplay(false);

        Label label = new Label(text);
        label.setTextFill(Color.GOLD);
        label.setStyle(
                "-fx-font-size: 34px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 8, 0.8, 0, 0);"
        );

        memoryArea.getChildren().add(label);
    }

    // ----------------------------- Damage numbers -----------------------------

    private void showBossDamageNumber(int damage) {
        if (damage <= 0 || bossSprite == null) {
            return;
        }

        showDamageOnSprite(bossSprite, damage);
    }

    private void showDamageOnSprite(StackPane sprite, int damage) {
        Label damageLabel = new Label("-" + damage);
        damageLabel.setTextFill(Color.RED);
        damageLabel.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 8, 0.8, 0, 0);"
        );

        StackPane.setAlignment(damageLabel, Pos.TOP_CENTER);
        damageLabel.setTranslateY(-10);

        sprite.getChildren().add(damageLabel);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.9), damageLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);

        TranslateTransition move = new TranslateTransition(Duration.seconds(0.9), damageLabel);
        move.setFromY(-10);
        move.setToY(-45);

        ParallelTransition animation = new ParallelTransition(fade, move);
        animation.setOnFinished(e -> sprite.getChildren().remove(damageLabel));
        animation.play();
    }

    // ----------------------------- Fight completion -----------------------------

    private void finishCurrentFight() {
        stopAllTimers();
        updatePotionDisplay(false);

        setupPartyDisplay();
        setupBossDisplay();

        if (guild.isWiped()) {
            memoryArea.getChildren().clear();
            timerLabel.setText("");
            statusLabel.setText("");

            attackButton.setVisible(false);
            attackButton.setDisable(true);

            showBattleResultPopup(false);
            return;
        }

        fightIndex++;

        if (fightIndex >= quest1.getBossFights().size()) {
            finishQuest1();
            return;
        }

        currentFight = quest1.getBossFights().get(fightIndex);
        attackerIndex = 0;

        setupBossDisplay();

        activePauseTransition = new PauseTransition(Duration.seconds(1.5));
        activePauseTransition.setOnFinished(e -> prepareNextAttacker());
        activePauseTransition.play();

        guild.healMainPartyToFull();
    }

    private void finishQuest1() {
        stopAllTimers();
        updatePotionDisplay(false);

        quest1.runEvents(guild);
        quest1.updateCharacters(guild);
        game.advanceToNextQuest();

        guild.healMainPartyToFull();

        memoryArea.getChildren().clear();
        timerLabel.setText("");
        statusLabel.setText("");

        attackButton.setVisible(false);
        attackButton.setDisable(true);

        showBattleResultPopup(true);
    }

    // ----------------------------- Pause popup -----------------------------

    @FXML
    public void onPause() {
        paused = true;

        inputEnabledBeforePause = inputEnabled;
        attackButtonEnabledBeforePause = !attackButton.isDisable();

        inputEnabled = false;
        updatePotionDisplay(false);

        if (countdownTimeline != null) {
            countdownTimeline.pause();
        }

        if (activePauseTransition != null) {
            activePauseTransition.pause();
        }

        attackButton.setDisable(true);

        pauseOverlay.setVisible(true);
        pausePopup.setVisible(true);
        pauseOverlay.toFront();
        pausePopup.toFront();
    }

    @FXML
    public void onResume() {
        paused = false;

        pauseOverlay.setVisible(false);
        pausePopup.setVisible(false);

        if (countdownTimeline != null) {
            countdownTimeline.play();
        }

        if (activePauseTransition != null) {
            activePauseTransition.play();
        }

        inputEnabled = inputEnabledBeforePause;

        if (attackButtonEnabledBeforePause) {
            attackButton.setDisable(false);
        }

        updatePotionDisplay(inputEnabled);
    }

    @FXML
    public void onRetry() {
        resetQuest1State();
    }

    @FXML
    public void onPauseMainMenu() {
        stopAllTimers();
        guild.healMainPartyToFull();
        goToMainMenu();
    }

    // ----------------------------- Navigation -----------------------------

    private void goToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Error returning to main menu.");
        }
    }
}