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
import seng201.team0.models.Quest1;

import java.util.List;

/**
 * Quest 1 story controller.
 *
 * This screen uses the same Pokemon-style chat format as ImperialCommandController:
 * - Top background image
 * - Bottom dialogue panel
 * - Typewriter text
 * - Click to complete current line / continue
 * - Short choice buttons
 * - Hover over a choice button to preview the full command text
 *
 * Game logic for event effects is stored in Quest1.java.
 */
public class Quest1Controller implements GameDataReceiver {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;

    @FXML private Label eventTitleLabel;
    @FXML private Label speakerLabel;
    @FXML private Label dialogueLabel;
    @FXML private Label nextArrowLabel;
    @FXML private Label choicePreviewLabel;

    @FXML private Button continueButton;
    @FXML private Button choiceAButton;
    @FXML private Button choiceBButton;
    @FXML private Button choiceCButton;

    private Game game;
    private Quest1 quest1;
    private List<Quest1.StoryEvent> storyEvents;

    private int eventIndex = 0;

    private Timeline typewriterTimeline;
    private String currentFullText = "";
    private int currentCharIndex = 0;
    private boolean typing = false;

    private boolean waitingForChoice = false;
    private boolean showingResult = false;
    private boolean finalScene = false;

    private static final double TYPE_SPEED_MILLIS = 24;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        hideChoiceButtons();
        nextArrowLabel.setVisible(false);
        continueButton.setVisible(false);
        choicePreviewLabel.setVisible(false);

        contentPane.setOnMouseClicked(event -> advanceDialogue());
    }

    public void setGameData(Game game) {
        this.game = game;
        game.selectQuest(0);

        if (game.getQuests().get(0) instanceof Quest1) {
            this.quest1 = (Quest1) game.getQuests().get(0);
        } else {
            this.quest1 = new Quest1(game.getDifficulty());
        }

        this.storyEvents = quest1.getStoryEvents();
        this.eventIndex = 0;
        this.showingResult = false;
        this.finalScene = false;

        if (quest1.isStoryCompleted()) {
            Platform.runLater(this::goToBattlefield);
        } else {
            showCurrentEvent();
        }
    }

    private void showCurrentEvent() {
        if (eventIndex >= storyEvents.size()) {
            showFinalScene();
            return;
        }

        Quest1.StoryEvent event = storyEvents.get(eventIndex);

        waitingForChoice = true;
        showingResult = false;
        finalScene = false;

        eventTitleLabel.setText(event.getTitle());
        speakerLabel.setText(event.getSpeaker());

        setBackground(event.getBackgroundImagePath());

        hideChoiceButtons();
        continueButton.setVisible(false);
        nextArrowLabel.setVisible(false);
        choicePreviewLabel.setVisible(false);

        typeText(event.getPrompt());
    }

    private void showFinalScene() {
        waitingForChoice = false;
        showingResult = false;
        finalScene = true;

        eventTitleLabel.setText("The Battle Begins");
        speakerLabel.setText("Narrator");

        setBackground("/images/quest1_gates.png");

        hideChoiceButtons();
        choicePreviewLabel.setVisible(false);
        continueButton.setText("Begin Battle");
        continueButton.setVisible(false);
        nextArrowLabel.setVisible(false);

        typeText(quest1.getBattleIntroText());
    }

    private void setBackground(String imagePath) {
        try {
            backgroundImage.setImage(new Image(
                    getClass().getResource(imagePath).toExternalForm()
            ));
        } catch (Exception e) {
            backgroundImage.setImage(null);
        }
    }

    private void typeText(String text) {
        stopTypewriter();

        currentFullText = text;
        currentCharIndex = 0;
        typing = true;

        dialogueLabel.setText("");
        nextArrowLabel.setVisible(false);

        typewriterTimeline = new Timeline(new KeyFrame(Duration.millis(TYPE_SPEED_MILLIS), event -> {
            if (currentCharIndex < currentFullText.length()) {
                currentCharIndex++;
                dialogueLabel.setText(currentFullText.substring(0, currentCharIndex));
            } else {
                finishTyping();
            }
        }));

        typewriterTimeline.setCycleCount(Timeline.INDEFINITE);
        typewriterTimeline.play();
    }

    private void stopTypewriter() {
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
            typewriterTimeline = null;
        }
    }

    private void finishTyping() {
        stopTypewriter();

        typing = false;
        dialogueLabel.setText(currentFullText);

        if (waitingForChoice) {
            showChoiceButtons();
            nextArrowLabel.setVisible(false);
            continueButton.setVisible(false);
        } else {
            nextArrowLabel.setVisible(true);
            continueButton.setVisible(true);
        }
    }

    private void advanceDialogue() {
        if (typing) {
            finishTyping();
            return;
        }

        if (waitingForChoice) {
            return;
        }

        if (finalScene) {
            goToBattlefield();
            return;
        }

        if (showingResult) {
            eventIndex++;
            showCurrentEvent();
        }
    }

    @FXML
    public void onContinue() {
        advanceDialogue();
    }

    @FXML
    public void onChoiceA() {
        handleChoice(0);
    }

    @FXML
    public void onChoiceB() {
        handleChoice(1);
    }

    @FXML
    public void onChoiceC() {
        handleChoice(2);
    }

    private void handleChoice(int choiceIndex) {
        if (!waitingForChoice || eventIndex >= storyEvents.size()) {
            return;
        }

        waitingForChoice = false;
        showingResult = true;
        finalScene = false;

        hideChoiceButtons();
        choicePreviewLabel.setVisible(false);

        speakerLabel.setText("Result");

        String resultText = quest1.applyStoryChoice(game.getGuild(), eventIndex, choiceIndex);
        typeText(resultText);
    }

    private void showChoiceButtons() {
        Quest1.StoryEvent event = storyEvents.get(eventIndex);
        List<Quest1.StoryChoice> choices = event.getChoices();

        setupChoiceButton(choiceAButton, choices.get(0));
        setupChoiceButton(choiceBButton, choices.get(1));
        setupChoiceButton(choiceCButton, choices.get(2));

        choicePreviewLabel.setText("Hover over a command to read the full order.");
        choicePreviewLabel.setVisible(true);

        choiceAButton.setVisible(true);
        choiceBButton.setVisible(true);
        choiceCButton.setVisible(true);

        choiceAButton.setDisable(false);
        choiceBButton.setDisable(false);
        choiceCButton.setDisable(false);
    }

    private void setupChoiceButton(Button button, Quest1.StoryChoice choice) {
        button.setText(choice.getShortChoiceText());

        button.setOnMouseEntered(event -> {
            choicePreviewLabel.setText(choice.getFullChoiceText());
            choicePreviewLabel.setVisible(true);
        });

        button.setOnMouseExited(event -> {
            if (waitingForChoice) {
                choicePreviewLabel.setText("Hover over a command to read the full order.");
                choicePreviewLabel.setVisible(true);
            }
        });
    }

    private void hideChoiceButtons() {
        choiceAButton.setVisible(false);
        choiceBButton.setVisible(false);
        choiceCButton.setVisible(false);

        choiceAButton.setDisable(true);
        choiceBButton.setDisable(true);
        choiceCButton.setDisable(true);
    }

    private void goToBattlefield() {
        stopTypewriter();
        quest1.markStoryCompleted();
        game.selectQuest(0);

        /*
         * We are keeping this flexible because the battle controller is the next step.
         * Recommended next file name:
         *     battlefield.fxml
         *
         * If it does not exist yet, this screen will show a clear message instead of crashing.
         */
        if (tryLoadBattlefield("/fxml/battlefield.fxml")) {
            return;
        }

        if (tryLoadBattlefield("/fxml/quest1battle.fxml")) {
            return;
        }

        speakerLabel.setText("System");
        nextArrowLabel.setVisible(false);
        continueButton.setVisible(false);
        typeText("Battlefield screen is not connected yet. Next step: create battlefield.fxml and BattlefieldController.");
    }

    private boolean tryLoadBattlefield(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof GameDataReceiver) {
                ((GameDataReceiver) controller).setGameData(game);
            }

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Icathia Battlefield");
            return true;

        } catch (Exception e) {
            return false;
        }
    }
}
