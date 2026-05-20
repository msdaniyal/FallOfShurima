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
import seng201.team0.models.Quest;
import seng201.team0.models.QuestStoryChoice;
import seng201.team0.models.QuestStoryEvent;
import seng201.team0.models.StoryDrivenQuest;

import java.util.List;

/**
 * Shared controller for two-choice quest event screens.
 * Quest2-Quest5 extend this and provide their quest index.
 */
public abstract class QuestChoiceController implements GameDataReceiver {

    @FXML protected AnchorPane rootPane;
    @FXML protected Pane contentPane;
    @FXML protected ImageView backgroundImage;

    @FXML protected Label eventTitleLabel;
    @FXML protected Label speakerLabel;
    @FXML protected Label dialogueLabel;
    @FXML protected Label nextArrowLabel;
    @FXML protected Label choicePreviewLabel;

    @FXML protected Button continueButton;
    @FXML protected Button choiceAButton;
    @FXML protected Button choiceBButton;

    protected Game game;
    protected Quest quest;
    protected StoryDrivenQuest storyQuest;
    protected List<QuestStoryEvent> storyEvents;

    protected int eventIndex = 0;
    protected Timeline typewriterTimeline;
    protected String currentFullText = "";
    protected int currentCharIndex = 0;
    protected boolean typing = false;
    protected boolean waitingForChoice = false;
    protected boolean showingResult = false;
    protected boolean finalScene = false;

    private static final double TYPE_SPEED_MILLIS = 22;

    protected abstract int getQuestIndex();
    protected abstract String getQuestScreenTitle();

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        hideChoiceButtons();

        if (nextArrowLabel != null) {
            nextArrowLabel.setVisible(false);
        }
        if (continueButton != null) {
            continueButton.setVisible(false);
        }
        if (choicePreviewLabel != null) {
            choicePreviewLabel.setVisible(false);
        }

        contentPane.setOnMouseClicked(event -> advanceDialogue());
    }

    @Override
    public void setGameData(Game game) {
        this.game = game;
        game.selectQuest(getQuestIndex());

        this.quest = game.getQuests().get(getQuestIndex());
        if (!(quest instanceof StoryDrivenQuest)) {
            throw new IllegalStateException(quest.getName() + " must implement StoryDrivenQuest.");
        }

        this.storyQuest = (StoryDrivenQuest) quest;
        this.storyEvents = storyQuest.getStoryEvents();
        this.eventIndex = 0;
        this.showingResult = false;
        this.finalScene = false;

        if (storyQuest.isStoryCompleted()) {
            Platform.runLater(this::goToBattlefield);
        } else {
            showCurrentEvent();
        }
    }

    protected void showCurrentEvent() {
        if (eventIndex >= storyEvents.size()) {
            showFinalScene();
            return;
        }

        QuestStoryEvent event = storyEvents.get(eventIndex);

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

    protected void showFinalScene() {
        waitingForChoice = false;
        showingResult = false;
        finalScene = true;

        eventTitleLabel.setText("Battle Begins");
        speakerLabel.setText("Narrator");
        setBackground("/images/quest" + (getQuestIndex() + 1) + "_battle_intro.png");

        hideChoiceButtons();
        choicePreviewLabel.setVisible(false);
        continueButton.setText("Begin Battle");
        continueButton.setVisible(false);
        nextArrowLabel.setVisible(false);

        storyQuest.markStoryCompleted();
        typeText(storyQuest.getBattleIntroText());
    }

    protected void setBackground(String imagePath) {
        try {
            backgroundImage.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
        } catch (Exception e) {
            backgroundImage.setImage(null);
        }
    }

    protected void typeText(String text) {
        stopTypewriter();
        currentFullText = text == null ? "" : text;
        currentCharIndex = 0;
        typing = true;
        dialogueLabel.setText("");
        nextArrowLabel.setVisible(false);

        typewriterTimeline = new Timeline(new KeyFrame(Duration.millis(TYPE_SPEED_MILLIS), event -> {
            if (currentCharIndex < currentFullText.length()) {
                dialogueLabel.setText(currentFullText.substring(0, currentCharIndex + 1));
                currentCharIndex++;
            } else {
                stopTypewriter();
                typing = false;
                onTypingFinished();
            }
        }));
        typewriterTimeline.setCycleCount(Timeline.INDEFINITE);
        typewriterTimeline.play();
    }

    protected void stopTypewriter() {
        if (typewriterTimeline != null) {
            typewriterTimeline.stop();
        }
    }

    protected void finishTypingImmediately() {
        stopTypewriter();
        typing = false;
        dialogueLabel.setText(currentFullText);
        onTypingFinished();
    }

    protected void onTypingFinished() {
        if (waitingForChoice && !showingResult) {
            showChoiceButtons();
        } else {
            nextArrowLabel.setVisible(true);
            continueButton.setVisible(true);
        }
    }

    protected void advanceDialogue() {
        if (typing) {
            finishTypingImmediately();
            return;
        }

        if (waitingForChoice && !showingResult) {
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
        choose(0);
    }

    @FXML
    public void onChoiceB() {
        choose(1);
    }

    protected void choose(int choiceIndex) {
        if (!waitingForChoice || eventIndex >= storyEvents.size()) {
            return;
        }

        String result = storyQuest.applyStoryChoice(game.getGuild(), eventIndex, choiceIndex);

        waitingForChoice = false;
        showingResult = true;
        hideChoiceButtons();
        choicePreviewLabel.setVisible(false);
        speakerLabel.setText("Result");
        typeText(result);
    }

    protected void showChoiceButtons() {
        QuestStoryEvent event = storyEvents.get(eventIndex);
        List<QuestStoryChoice> choices = event.getChoices();

        setupChoiceButton(choiceAButton, choices.get(0));
        setupChoiceButton(choiceBButton, choices.get(1));

        choicePreviewLabel.setText("Hover over a command to read the full order.");
        choicePreviewLabel.setVisible(true);

        choiceAButton.setVisible(true);
        choiceBButton.setVisible(true);
        choiceAButton.setDisable(false);
        choiceBButton.setDisable(false);
    }

    protected void setupChoiceButton(Button button, QuestStoryChoice choice) {
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

    protected void hideChoiceButtons() {
        if (choiceAButton != null) {
            choiceAButton.setVisible(false);
            choiceAButton.setDisable(true);
        }
        if (choiceBButton != null) {
            choiceBButton.setVisible(false);
            choiceBButton.setDisable(true);
        }
    }

    protected void goToBattlefield() {
        stopTypewriter();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/battlefield.fxml"));
            Parent root = loader.load();

            GameDataReceiver controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — " + getQuestScreenTitle());

        } catch (Exception e) {
            e.printStackTrace();
            speakerLabel.setText("System");
            nextArrowLabel.setVisible(false);
            continueButton.setVisible(false);
            typeText("Battlefield screen could not be loaded.");
        }
    }
}
