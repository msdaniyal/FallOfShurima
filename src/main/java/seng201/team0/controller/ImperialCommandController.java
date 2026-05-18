package seng201.team0.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import seng201.team0.models.Faction;
import seng201.team0.models.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Imperial command story scene.
 *
 * Pokemon-style dialogue:
 * - Text types out letter by letter.
 * - Clicking while typing instantly completes the current line.
 * - Clicking after the line is complete advances to the next line.
 * - A small arrow appears when the player can continue.
 *
 * Flow:
 * 1. Azir reports the Icathian uprising to the Emperor.
 * 2. Azir warns that Zilean and Jax are involved.
 * 3. The Emperor orders Azir to summon the chosen champion.
 * 4. The Emperor commands Aatrox or Xolaani, depending on setup choice.
 * 5. Continue to the Map Hub / Main Menu.
 */
public class ImperialCommandController {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;

    @FXML private Label speakerLabel;
    @FXML private Label dialogueLabel;
    @FXML private Label nextArrowLabel;
    @FXML private Button continueButton;

    private Game game;
    private final List<DialogueLine> lines = new ArrayList<>();
    private int lineIndex = 0;

    private Timeline typewriterTimeline;
    private String currentFullText = "";
    private int currentCharIndex = 0;
    private boolean typing = false;

    private static final double TYPE_SPEED_MILLIS = 24;

    private static class DialogueLine {
        private final String speaker;
        private final String text;
        private final String imagePath;

        private DialogueLine(String speaker, String text, String imagePath) {
            this.speaker = speaker;
            this.text = text;
            this.imagePath = imagePath;
        }
    }

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);

        if (nextArrowLabel != null) {
            nextArrowLabel.setVisible(false);
        }

        /*
         * Click anywhere on the scene to continue.
         * If text is still typing, the click completes the line first.
         */
        contentPane.setOnMouseClicked(event -> advanceDialogue());
    }

    /**
     * Receives the current game from CharacterSelectController.
     *
     * @param game current game state
     */
    public void setGameData(Game game) {
        this.game = game;
        buildDialogue();
        showCurrentLine();
    }

    private void buildDialogue() {
        lines.clear();

        Faction faction = game.getGuild().getPlayerFaction();
        boolean choseAatrox = faction == Faction.AATROX;

        lines.add(new DialogueLine(
                "Azir",
                "Father, word has reached the capital. Icathia has risen against Shurima.",
                "/images/imperial_court.png"
        ));

        lines.add(new DialogueLine(
                "Azir",
                "Their nobles refuse tribute, their armies gather at the eastern gates, and the rebels whisper two names: Zilean and Jax.",
                "/images/azir_bowing.png"
        ));

        lines.add(new DialogueLine(
                "The Emperor",
                "Then this rebellion must be broken before it teaches the other provinces to dream of freedom.",
                "/images/azir_bowing.png"
        ));

        lines.add(new DialogueLine(
                "The Emperor",
                "Azir, call my champion to court. Shurima will answer Icathia with steel, sun, and obedience.",
                "/images/azir_bowing.png"
        ));

        if (choseAatrox) {
            lines.add(new DialogueLine(
                    "The Emperor",
                    "Aatrox, blade of the empire, you were made for war. March to Icathia and break their rebellion before it becomes a plague.",
                    "/images/imperial_command_aatrox.png"
            ));

            lines.add(new DialogueLine(
                    "Aatrox",
                    "By your command. Icathia will kneel, or it will burn beneath the sun.",
                    "/images/imperial_command_aatrox.png"
            ));
        } else {
            lines.add(new DialogueLine(
                    "The Emperor",
                    "Xolaani, blood-weaver of Shurima, bind their resistance before it infects the rest of the empire.",
                    "/images/imperial_command_xolaani.png"
            ));

            lines.add(new DialogueLine(
                    "Xolaani",
                    "By your command. Their defiance will be silenced, and their will shall serve Shurima.",
                    "/images/imperial_command_xolaani.png"
            ));
        }

        lines.add(new DialogueLine(
                "Narrator",
                "And so the command was given. The march to Icathia began beneath the gaze of the Sun Disc.",
                choseAatrox ? "/images/imperial_command_aatrox.png" : "/images/imperial_command_xolaani.png"
        ));
    }

    private void showCurrentLine() {
        stopTypewriter();

        if (lineIndex >= lines.size()) {
            goToMapHub();
            return;
        }

        DialogueLine line = lines.get(lineIndex);

        speakerLabel.setText(line.speaker);
        currentFullText = line.text;
        dialogueLabel.setText("");
        currentCharIndex = 0;

        if (nextArrowLabel != null) {
            nextArrowLabel.setVisible(false);
        }

        if (continueButton != null) {
            continueButton.setText(lineIndex == lines.size() - 1 ? "Continue" : "Next");
        }

        try {
            backgroundImage.setImage(new Image(
                    getClass().getResource(line.imagePath).toExternalForm()
            ));
        } catch (Exception e) {
            backgroundImage.setImage(null);
            currentFullText = line.text + "\n\n[Missing image: " + line.imagePath + "]";
        }

        startTypewriter();
    }

    private void startTypewriter() {
        typing = true;

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

        if (nextArrowLabel != null) {
            nextArrowLabel.setVisible(true);
        }
    }

    private void advanceDialogue() {
        if (typing) {
            finishTyping();
            return;
        }

        lineIndex++;
        showCurrentLine();
    }

    @FXML
    public void onContinue() {
        advanceDialogue();
    }

    private void goToMapHub() {
        stopTypewriter();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            MainMenuController controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Map Hub");

        } catch (Exception e) {
            e.printStackTrace();
            dialogueLabel.setText("Error loading the map hub.");
        }
    }
}
