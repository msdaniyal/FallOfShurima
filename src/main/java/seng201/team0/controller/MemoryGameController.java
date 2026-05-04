package seng201.team0.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import javafx.stage.Stage;
import javafx.util.Duration;
import seng201.team0.models.Adventurer;
import seng201.team0.models.BossFight;
import seng201.team0.models.Guild;
import seng201.team0.models.MemoryGame;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import static javafx.application.Platform.exit;

public class MemoryGameController {

    @FXML private GridPane imageContainer;
    @FXML private Label statusLabel;
    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button endButton;

    private final List<ImageView> images = new ArrayList<>();
    private final List<Integer> playerInput = new ArrayList<>();
    private List<Integer> pattern = new ArrayList<>();

    private boolean inputEnabled = false;
    private Timeline countdownTimeline;
    private int timeRemaining = 5;

    private BossFight bossFight;
    private Guild guild;
    private Adventurer currentAttacker;

    /**
     * Injects fight data. Must be called before startMemoryAttack().
     * Builds the GridPane layout for the correct difficulty.
     */
    public void setFightData(BossFight bossFight, Guild guild, Adventurer currentAttacker) {
        this.bossFight = bossFight;
        this.guild = guild;
        this.currentAttacker = currentAttacker;
        buildImageViews(bossFight.getMemoryGame().getSequenceLength());
    }

    @FXML
    public void initialize() {
        // Layout built in setFightData() once difficulty is known.
    }

    /**
     * Builds the grid layout based on sequence length / difficulty:
     *   Easy   (4)  → 2×2
     *   Normal (7)  → 3×3 + 7th centred below
     *   Hard   (10) → 2×5
     */
    private void buildImageViews(int count) {
        images.clear();
        imageContainer.getChildren().clear();
        imageContainer.getColumnConstraints().clear();
        imageContainer.getRowConstraints().clear();

        int cols = columnsFor(count);
        for (int c = 0; c < cols; c++) {
            ColumnConstraints cc = new ColumnConstraints(120);
            imageContainer.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < count; i++) {
            ImageView iv = new ImageView(
                    new Image(getClass().getResource("/images/img" + i + ".png").toExternalForm())
            );
            iv.setVisible(false);
            iv.setFitWidth(100);
            iv.setFitHeight(100);
            iv.setPreserveRatio(true);

            final int index = i;
            iv.setOnMouseClicked(e -> handlePlayerClick(index));

            int col = i % cols;
            int row = i / cols;

            if (count == 7 && i == 6) {
                GridPane.setColumnSpan(iv, 3);
                GridPane.setHalignment(iv, HPos.CENTER);
                col = 0;
            }

            imageContainer.add(iv, col, row);
            images.add(iv);
        }
    }

    private int columnsFor(int count) {
        switch (count) {
            case 4:  return 2;
            case 7:  return 3;
            case 10: return 2;
            default: return 2;
        }
    }

    // ----------------------------- Game flow ----------------------------------

    /**
     * Called when the player clicks "Ready to Remember!".
     * Hides the button immediately so it cannot be clicked again,
     * then starts the sequence playback.
     */
    @FXML
    public void startMemoryAttack() {
        // Hide and disable the button so it can't be clicked a second time
        startButton.setDisable(true);

        playerInput.clear();
        inputEnabled = false;

        pattern = bossFight.getMemoryGame().generateSequence();

        statusLabel.setText("Remember the sequence!");
        playPattern(0);
    }

    private void playPattern(int index) {
        if (index >= pattern.size()) {
            showAllImages();
            statusLabel.setText("Click the pictures in order!");
            startInputPhase();
            return;
        }

        hideAllImages();
        images.get(pattern.get(index)).setVisible(true);

        PauseTransition showPause = new PauseTransition(Duration.seconds(0.8));
        showPause.setOnFinished(event -> {
            hideAllImages();
            PauseTransition gapPause = new PauseTransition(Duration.seconds(0.3));
            gapPause.setOnFinished(e -> playPattern(index + 1));
            gapPause.play();
        });
        showPause.play();
    }

    private void startInputPhase() {
        inputEnabled = true;
        playerInput.clear();
        startTimer();
    }

    private void startTimer() {
        timeRemaining = 50;
        timerLabel.setText("Time Remaining: " + timeRemaining);

        countdownTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    timeRemaining--;
                    timerLabel.setText("Time Remaining: " + timeRemaining);
                    if (timeRemaining <= 0) {
                        countdownTimeline.stop();
                        failAttack();
                    }
                })
        );
        countdownTimeline.setCycleCount(5);
        countdownTimeline.play();
    }

    // ----------------------------- Click handling -----------------------------

    private void handlePlayerClick(int clickedIndex) {
        if (!inputEnabled) return;

        highlightSelectedImage(clickedIndex);
        playerInput.add(clickedIndex);

        int currentPos = playerInput.size() - 1;

        if (clickedIndex != pattern.get(currentPos)) {
            failAttack();
            return;
        }

        if (playerInput.size() == pattern.size()) {
            if (bossFight.getMemoryGame().checkSequence(playerInput)) {
                successAttack();
            } else {
                failAttack();
            }
        }
    }

    // ----------------------------- Visual helpers -----------------------------

    private void highlightSelectedImage(int index) {
        clearImageEffects();
        ImageView selected = images.get(index);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(25);
        glow.setSpread(0.6);
        selected.setEffect(glow);
        selected.setScaleX(1.1);
        selected.setScaleY(1.1);
    }

    private void clearImageEffects() {
        for (ImageView iv : images) {
            iv.setEffect(null);
            iv.setScaleX(1.0);
            iv.setScaleY(1.0);
        }
    }

    private void hideAllImages() {
        for (ImageView iv : images) { iv.setVisible(false); }
    }

    private void showAllImages() {
        for (ImageView iv : images) { iv.setVisible(true); }
    }

    // ----------------------------- Attack resolution --------------------------

    private void successAttack() {
        clearImageEffects();
        inputEnabled = false;
        stopTimer();

        if (bossFight == null || guild == null || currentAttacker == null) {
            statusLabel.setText("Attack success! (Fight data not connected yet)");
            return;
        }

        int damage = bossFight.playerAttack(currentAttacker, true);
        statusLabel.setText("Successful attack! Dealt " + damage + " damage.");

        if (!bossFight.isFightOver(guild)) bossFight.bossTurn(guild);
        bossFight.finishFightIfOver(guild);
        showFightResultIfOver();
    }

    private void failAttack() {
        clearImageEffects();
        inputEnabled = false;
        stopTimer();

        if (bossFight == null || guild == null || currentAttacker == null) {
            statusLabel.setText("Attack failed!");
            return;
        }

        int damage = bossFight.playerAttack(currentAttacker, false);
        statusLabel.setText("Attack failed! Dealt " + damage + " damage.");
        startButton.setVisible(true);
        startButton.setDisable(false);
        startButton.setText("Try Again?");

        endButton.setVisible(true);
        endButton.setDisable(false);
        endButton.setText("Return");

        if (!bossFight.isFightOver(guild)) bossFight.bossTurn(guild);
        bossFight.finishFightIfOver(guild);
        showFightResultIfOver();
    }

    private void stopTimer() {
        if (countdownTimeline != null) countdownTimeline.stop();
        timerLabel.setText("");
    }

    private void showFightResultIfOver() {
        if (bossFight != null && guild != null && bossFight.isFightOver(guild)) {
            String result = bossFight.isPlayerWon() ? "\nYou won!" : "\nYou lost!";

            statusLabel.setText(statusLabel.getText() + result);
        }
    }

    // ----------------------------- End attack move --------------------------

    public void endGame() {
        // Close the memory game window
        Stage stage = (Stage) statusLabel.getScene().getWindow();
        stage.close();
    }
}