package seng201.team0.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import seng201.team0.models.Adventurer;
import seng201.team0.models.BossFight;
import seng201.team0.models.Guild;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class BossFightController {

    @FXML private ImageView image0;
    @FXML private ImageView image1;
    @FXML private ImageView image2;
    @FXML private ImageView image3;
    @FXML private Label statusLabel;
    @FXML private Label timerLabel;

    private final List<Integer> pattern = new ArrayList<>();
    private final List<Integer> playerInput = new ArrayList<>();
    private final List<ImageView> images = new ArrayList<>();
    private final Random random = new Random();

    private boolean inputEnabled = false;
    private Timeline countdownTimeline;
    private int timeRemaining = 5;

    // connect to model
    private BossFight bossFight;
    private Guild guild;
    private Adventurer currentAttacker;

    public void setFightData(BossFight bossFight, Guild guild, Adventurer currentAttacker) {
        this.bossFight = bossFight;
        this.guild = guild;
        this.currentAttacker = currentAttacker;
    }

    @FXML
    public void initialize() {
        images.add(image0);
        images.add(image1);
        images.add(image2);
        images.add(image3);

        image0.setImage(new Image(getClass().getResource("/images/img0.png").toExternalForm()));
        image1.setImage(new Image(getClass().getResource("/images/img1.png").toExternalForm()));
        image2.setImage(new Image(getClass().getResource("/images/img2.png").toExternalForm()));
        image3.setImage(new Image(getClass().getResource("/images/img3.png").toExternalForm()));

        hideAllImages();
    }

    @FXML
    public void startMemoryAttack() {
        pattern.clear();
        playerInput.clear();
        inputEnabled = false;

        for (int i = 0; i < 3; i++) {
            pattern.add(random.nextInt(images.size()));
        }

        statusLabel.setText("Remember the pictures!");
        playPattern(0);
    }

    private void playPattern(int index) {
        if (index >= pattern.size()) {
            showAllImages();
            statusLabel.setText("Click the sequence of the pictures!");
            startInputPhase();
            return;
        }

        hideAllImages();

        int imageIndex = pattern.get(index);
        images.get(imageIndex).setVisible(true);

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
        timeRemaining = 5;
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
        for (ImageView image : images) {
            image.setEffect(null);
            image.setScaleX(1.0);
            image.setScaleY(1.0);
        }
    }

    private void hideAllImages() {
        for (ImageView image : images) {
            image.setVisible(false);
        }
    }

    private void showAllImages() {
        for (ImageView image : images) {
            image.setVisible(true);
        }
    }

    @FXML
    private void onImage0Clicked() {
        handlePlayerClick(0);
    }

    @FXML
    private void onImage1Clicked() {
        handlePlayerClick(1);
    }

    @FXML
    private void onImage2Clicked() {
        handlePlayerClick(2);
    }

    @FXML
    private void onImage3Clicked() {
        handlePlayerClick(3);
    }

    private void handlePlayerClick(int clickedIndex) {
        if (!inputEnabled) {
            return;
        }

        highlightSelectedImage(clickedIndex);

        playerInput.add(clickedIndex);
        int currentPos = playerInput.size() - 1;

        if (clickedIndex != pattern.get(currentPos)) {
            failAttack();
            return;
        }

        if (playerInput.size() == pattern.size()) {
            successAttack();
        }
    }

    private void successAttack() {
        clearImageEffects();
        inputEnabled = false;
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
        timerLabel.setText("");

        if (bossFight == null || guild == null || currentAttacker == null) {
            statusLabel.setText("Attack success! (Fight data not connected yet)");
            return;
        }

        int damage = bossFight.playerAttack(currentAttacker, true);
        statusLabel.setText("Successful attack! Dealt " + damage + " damage.");

        if (!bossFight.isFightOver(guild)) {
            bossFight.bossTurn(guild);
        }

        bossFight.finishFightIfOver(guild);
        showFightResultIfOver();
    }

    private void failAttack() {
        clearImageEffects();
        inputEnabled = false;
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
        timerLabel.setText("");

        if (bossFight == null || guild == null || currentAttacker == null) {
            statusLabel.setText("Attack failed!");
            return;
        }

        int damage = bossFight.playerAttack(currentAttacker, false);
        statusLabel.setText("Attack failed! Dealt " + damage + " damage.");

        if (!bossFight.isFightOver(guild)) {
            bossFight.bossTurn(guild);
        }

        bossFight.finishFightIfOver(guild);
        showFightResultIfOver();
    }

    private void showFightResultIfOver() {
        if (bossFight != null && guild != null && bossFight.isFightOver(guild)) {
            if (bossFight.isPlayerWon()) {
                statusLabel.setText(statusLabel.getText() + "\nYou won!");
            } else {
                statusLabel.setText(statusLabel.getText() + "\nYou lost!");
            }
        }
    }
}