package seng201.team0.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng201.team0.models.Game;
import seng201.team0.models.Quest6;

/**
 * Quest 6 has no branching events. It only appears if the player fails the
 * loyalty threshold after Quest 5, then sends the party straight to Zoe.
 */
public class Quest6Controller implements GameDataReceiver {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label eventTitleLabel;
    @FXML private Label speakerLabel;
    @FXML private Label dialogueLabel;
    @FXML private Label nextArrowLabel;
    @FXML private Button continueButton;

    private Game game;
    private Quest6 quest6;
    private Timeline typewriterTimeline;
    private String fullText = "";
    private int charIndex = 0;
    private boolean typing = false;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        if (nextArrowLabel != null) {
            nextArrowLabel.setVisible(false);
        }
        if (continueButton != null) {
            continueButton.setVisible(false);
        }
        contentPane.setOnMouseClicked(event -> advance());
    }

    @Override
    public void setGameData(Game game) {
        this.game = game;

        if (!game.getQuests().get(5).isUnlocked()) {
            Platform.runLater(this::returnToMenuBecauseLocked);
            return;
        }

        game.selectQuest(5);
        this.quest6 = (Quest6) game.getQuests().get(5);
        quest6.runEvents(game.getGuild());

        eventTitleLabel.setText("Twilight of Sleep");
        speakerLabel.setText("Narrator");
        setBackground("/images/Quest6/quest6_twilight.png");
        typeText(quest6.getFinalIntroText());
    }

    private void returnToMenuBecauseLocked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();
            GameDataReceiver controller = loader.getController();
            controller.setGameData(game);
            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBackground(String imagePath) {
        try {
            backgroundImage.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
        } catch (Exception e) {
            backgroundImage.setImage(null);
        }
    }

    private void typeText(String text) {
        stopTypewriter();
        fullText = text == null ? "" : text;
        charIndex = 0;
        typing = true;
        dialogueLabel.setText("");
        nextArrowLabel.setVisible(false);
        continueButton.setVisible(false);

        typewriterTimeline = new Timeline(new KeyFrame(Duration.millis(24), event -> {
            if (charIndex < fullText.length()) {
                dialogueLabel.setText(fullText.substring(0, charIndex + 1));
                charIndex++;
            } else {
                stopTypewriter();
                typing = false;
                nextArrowLabel.setVisible(true);
                continueButton.setVisible(true);
            }
        }));
        typewriterTimeline.setCycleCount(Timeline.INDEFINITE);
        typewriterTimeline.play();
    }

    private void stopTypewriter() {
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
        }
    }

    private void advance() {
        if (typing) {
            stopTypewriter();
            typing = false;
            dialogueLabel.setText(fullText);
            nextArrowLabel.setVisible(true);
            continueButton.setVisible(true);
            return;
        }

        goToBattlefield();
    }

    @FXML
    public void onContinue() {
        advance();
    }

    private void goToBattlefield() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/battlefield.fxml"));
            Parent root = loader.load();
            GameDataReceiver controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Zoe");
        } catch (Exception e) {
            e.printStackTrace();
            dialogueLabel.setText("Battlefield screen could not be loaded.");
        }
    }
}
