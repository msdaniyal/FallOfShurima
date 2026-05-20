package seng201.team0.controller;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
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
import java.util.function.Consumer;
import javafx.scene.paint.Color;

/**
 * Popup controller for the memory attack.
 * The controller owns only the JavaFX display/timer.
 * MemoryGame owns the sequence and success/failure logic.
 */
public class MemoryGameController {

    @FXML private GridPane imageContainer;
    @FXML private Label statusLabel;
    @FXML private Label timerLabel;
    @FXML private Button startButton;
    @FXML private Button endButton;

    private final List<ImageView> images = new ArrayList<>();
    private List<Integer> pattern = new ArrayList<>();

    private boolean inputEnabled = false;
    private Timeline countdownTimeline;
    private int timeRemaining = 5;

    private BossFight bossFight;
    private Guild guild;
    private Adventurer currentAttacker;

    private Consumer<Boolean> resultHandler;
    private boolean resultDelivered = false;

    public void setFightData(BossFight bossFight, Guild guild, Adventurer currentAttacker) {
        this.bossFight = bossFight;
        this.guild = guild;
        this.currentAttacker = currentAttacker;
        buildImageViews(bossFight.getMemoryGame().getSequenceLength());
    }

    public void setResultHandler(Consumer<Boolean> resultHandler) {
        this.resultHandler = resultHandler;
    }

    @FXML
    public void initialize() {
        if (endButton != null) {
            endButton.setVisible(false);
            endButton.setDisable(true);
        }
        if (timerLabel != null) {
            timerLabel.setText("");
        }
    }

    private void buildImageViews(int count) {
        images.clear();
        imageContainer.getChildren().clear();
        imageContainer.getColumnConstraints().clear();
        imageContainer.getRowConstraints().clear();

        for (int c = 0; c < 3; c++) {
            imageContainer.getColumnConstraints().add(new ColumnConstraints(95));
        }

        for (int i = 0; i < count; i++) {
            ImageView imageView = createMemoryImage(i);
            final int index = i;
            imageView.setOnMouseClicked(event -> handlePlayerClick(index));

            imageContainer.add(imageView, i % 3, i / 3);
            images.add(imageView);
        }
    }

    private ImageView createMemoryImage(int index) {
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(getClass().getResource("/images/img" + index + ".png").toExternalForm()));
        } catch (Exception ignored) {
            imageView.setImage(null);
        }

        imageView.setVisible(false);
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    @FXML
    public void startMemoryAttack() {
        if (bossFight == null) {
            finishAttack(false);
            return;
        }

        startButton.setDisable(true);
        startButton.setVisible(false);
        resultDelivered = false;
        inputEnabled = false;

        pattern = bossFight.getMemoryGame().startRound();

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

        PauseTransition showPause = new PauseTransition(Duration.seconds(0.65));
        showPause.setOnFinished(event -> {
            hideAllImages();
            PauseTransition gapPause = new PauseTransition(Duration.seconds(0.20));
            gapPause.setOnFinished(e -> playPattern(index + 1));
            gapPause.play();
        });
        showPause.play();
    }

    private void startInputPhase() {
        inputEnabled = true;
        startTimer();
    }

    private void startTimer() {
        timeRemaining = Math.max(8, bossFight.getMemoryGame().getSequenceLength() * 4);
        timerLabel.setText("Time: " + timeRemaining);

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeRemaining--;
            timerLabel.setText("Time: " + timeRemaining);

            if (timeRemaining <= 0) {
                bossFight.getMemoryGame().failRound();
                finishAttack(false);
            }
        }));

        countdownTimeline.setCycleCount(Timeline.INDEFINITE);
        countdownTimeline.play();
    }

    private void handlePlayerClick(int clickedIndex) {
        if (!inputEnabled || bossFight == null) {
            return;
        }

        highlightSelectedImage(clickedIndex);

        MemoryGame.SelectionResult result = bossFight.getMemoryGame().selectImage(clickedIndex);
        if (result.isComplete()) {
            finishAttack(result.isSuccessful());
        }
    }

    private void finishAttack(boolean success) {
        if (resultDelivered) {
            return;
        }

        resultDelivered = true;
        clearImageEffects();
        inputEnabled = false;
        stopTimer();

        if (success) {
            statusLabel.setText("Successful attack!");
        } else {
            statusLabel.setText("Attack failed!");
        }

        if (resultHandler != null) {
            closeWindow();
            Platform.runLater(() -> resultHandler.accept(success));
            return;
        }

        if (endButton != null) {
            endButton.setVisible(true);
            endButton.setDisable(false);
            endButton.setText("Return");
        }
    }

    private void highlightSelectedImage(int index) {
        clearImageEffects();

        ImageView selected = images.get(index);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.GOLD);
        glow.setRadius(18);
        glow.setSpread(0.55);
        selected.setEffect(glow);
        selected.setScaleX(1.08);
        selected.setScaleY(1.08);
    }

    private void clearImageEffects() {
        for (ImageView imageView : images) {
            imageView.setEffect(null);
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        }
    }

    private void hideAllImages() {
        for (ImageView imageView : images) {
            imageView.setVisible(false);
        }
    }

    private void showAllImages() {
        for (ImageView imageView : images) {
            imageView.setVisible(true);
        }
    }

    private void stopTimer() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        if (timerLabel != null) {
            timerLabel.setText("");
        }
    }

    @FXML
    public void endGame() {
        finishAttack(false);
    }

    private void closeWindow() {
        if (statusLabel != null && statusLabel.getScene() != null) {
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
}
