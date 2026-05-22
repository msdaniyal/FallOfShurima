package seng201.team76.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import seng201.team76.models.Game;
import seng201.team76.models.Quest;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * Controller for Quest map screen.
 * Shows icons of all quests.
 * Last quest appears only if the player wins quest 5 and lacks loyalty points.
 *
 * @author Mohammed, Xinyi
 */
public class MapController implements GameDataReceiver {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private Pane mapPane;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private Button backButton;
    @FXML private ImageView backgroundImage;

    private Game game;

    private static final String[] ROMAN_NUMBERS = {
            "I", "II", "III", "IV", "V"
    };

    private static final double[][] QUEST_POSITIONS = {
            {120, 300},
            {270, 210},
            {420, 335},
            {570, 220},
            {720, 315}
    };

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        messageLabel.setText("");
    }

    public void setGameData(Game game) {
        this.game = game;
        buildMap();
    }

    private void buildMap() {
        mapPane.getChildren().clear();

        drawCurvedPath();

        List<Quest> quests = game.getQuests();

        for (int i = 0; i < 5; i++) {
            Quest quest = quests.get(i);
            boolean unlocked = quest.isUnlocked();

            Button questButton = createQuestButton(ROMAN_NUMBERS[i], unlocked);

            final int questNumber = i + 1;
            questButton.setLayoutX(QUEST_POSITIONS[i][0]);
            questButton.setLayoutY(QUEST_POSITIONS[i][1]);

            questButton.setOnAction(event -> {
                if (unlocked) {
                    openQuest(questNumber);
                } else {
                    messageLabel.setText("Quest " + questNumber + " is locked.");
                }
            });

            mapPane.getChildren().add(questButton);
        }

        showHiddenQuestIfUnlocked();
    }

    private void drawCurvedPath() {
        for (int i = 0; i < QUEST_POSITIONS.length - 1; i++) {
            double startX = QUEST_POSITIONS[i][0] + 42;
            double startY = QUEST_POSITIONS[i][1] + 42;
            double endX = QUEST_POSITIONS[i + 1][0] + 42;
            double endY = QUEST_POSITIONS[i + 1][1] + 42;

            Line line = new Line(startX, startY, endX, endY);
            line.setStroke(Color.rgb(120, 95, 35, 0.7));
            line.setStrokeWidth(5);
            line.setMouseTransparent(true);

            mapPane.getChildren().add(line);
        }
    }

    private Button createQuestButton(String text, boolean unlocked) {
        Button button = new Button(text);
        button.setPrefWidth(84);
        button.setPrefHeight(84);

        if (unlocked) {
            button.setDisable(false);
            button.setStyle(getUnlockedQuestStyle());

            DropShadow glow = new DropShadow();
            glow.setColor(Color.GOLD);
            glow.setRadius(24);
            glow.setSpread(0.45);
            button.setEffect(glow);

            button.setOnMouseEntered(event -> {
                button.setScaleX(1.08);
                button.setScaleY(1.08);
            });

            button.setOnMouseExited(event -> {
                button.setScaleX(1.0);
                button.setScaleY(1.0);
            });

        } else {
            button.setDisable(true);
            button.setStyle(getLockedQuestStyle());
            button.setOpacity(0.65);
        }

        return button;
    }

    private String getUnlockedQuestStyle() {
        return "-fx-background-color: radial-gradient(center 50% 50%, radius 65%, #f6d65b, #8a5a12);" +
                "-fx-background-radius: 100;" +
                "-fx-border-color: gold;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 100;" +
                "-fx-text-fill: #201200;" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;";
    }

    private String getLockedQuestStyle() {
        return "-fx-background-color: radial-gradient(center 50% 50%, radius 65%, #555555, #1f1f1f);" +
                "-fx-background-radius: 100;" +
                "-fx-border-color: #777777;" +
                "-fx-border-width: 3;" +
                "-fx-border-radius: 100;" +
                "-fx-text-fill: #b0b0b0;" +
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;";
    }

    private void showHiddenQuestIfUnlocked() {
        List<Quest> quests = game.getQuests();

        if (quests.size() < 6) {
            return;
        }

        Quest hiddenQuest = quests.get(5);

        if (!hiddenQuest.isUnlocked()) {
            return;
        }

        Button lastBattleButton = new Button("Last Battle");
        lastBattleButton.setPrefWidth(220);
        lastBattleButton.setPrefHeight(58);
        lastBattleButton.setLayoutX(340);
        lastBattleButton.setLayoutY(475);
        lastBattleButton.setStyle(
                "-fx-background-color: rgba(40, 20, 55, 0.92);" +
                        "-fx-background-radius: 22;" +
                        "-fx-border-color: #d8a8ff;" +
                        "-fx-border-width: 3;" +
                        "-fx-border-radius: 22;" +
                        "-fx-text-fill: #f2d5ff;" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;"
        );

        DropShadow mysteryGlow = new DropShadow();
        mysteryGlow.setColor(Color.MEDIUMPURPLE);
        mysteryGlow.setRadius(26);
        mysteryGlow.setSpread(0.5);
        lastBattleButton.setEffect(mysteryGlow);

        lastBattleButton.setOnAction(event -> openQuest(6));

        mapPane.getChildren().add(lastBattleButton);
    }

    private void openQuest(int questNumber) {
        try {
            int questIndex = questNumber - 1;
            if (!game.selectQuest(questIndex)) {
                messageLabel.setText("Quest " + questNumber + " is locked.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/quest" + questNumber + ".fxml")
            );

            Parent root = loader.load();

            Object controller = loader.getController();
            if (!(controller instanceof GameDataReceiver)) {
                messageLabel.setText("Quest " + questNumber + " controller must implement GameDataReceiver.");
                return;
            }

            ((GameDataReceiver) controller).setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Quest " + questNumber);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Quest " + questNumber + " page is not ready yet.");
        }
    }

    @FXML
    public void onBack() {
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
            messageLabel.setText("Error returning to main menu.");
        }
    }
}