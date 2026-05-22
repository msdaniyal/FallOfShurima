package seng201.team76.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng201.team76.models.Adventurer;
import seng201.team76.models.BossFight;

import java.util.Random;
import java.util.function.Consumer;

/**
 * Dice roll mini-controller used when the player chooses DEFEND.
 *
 * The controller only rolls and displays dice.
 * BossFight.java applies the actual combat result.
 */
public class DiceRollController {

    @FXML private Label titleLabel;
    @FXML private Label playerNameLabel;
    @FXML private Label bossNameLabel;
    @FXML private Label playerDiceLabel;
    @FXML private Label bossDiceLabel;
    @FXML private Label resultLabel;
    @FXML private Button rollButton;
    @FXML private Button continueButton;

    private BossFight bossFight;
    private Adventurer defender;
    private Consumer<DiceRollResult> resultHandler;

    private int playerRoll;
    private int bossRoll;
    private final Random random = new Random();

    public static class DiceRollResult {
        private final int playerRoll;
        private final int bossRoll;

        public DiceRollResult(int playerRoll, int bossRoll) {
            this.playerRoll = playerRoll;
            this.bossRoll = bossRoll;
        }

        public int getPlayerRoll() {
            return playerRoll;
        }

        public int getBossRoll() {
            return bossRoll;
        }
    }

    @FXML
    public void initialize() {
        continueButton.setVisible(false);
        continueButton.setDisable(true);
        playerDiceLabel.setText("-");
        bossDiceLabel.setText("-");
        resultLabel.setText("Click roll to defend.");
    }

    public void setDiceData(BossFight bossFight, Adventurer defender, Consumer<DiceRollResult> resultHandler) {
        this.bossFight = bossFight;
        this.defender = defender;
        this.resultHandler = resultHandler;

        if (defender != null) {
            playerNameLabel.setText(defender.getName());
        }

        if (bossFight != null && bossFight.getBoss() != null) {
            bossNameLabel.setText(bossFight.getBoss().getName());
        }
    }

    @FXML
    public void onRollDice() {
        rollButton.setDisable(true);
        resultLabel.setText("Rolling...");

        Timeline rolling = new Timeline(new KeyFrame(Duration.millis(90), event -> {
            playerDiceLabel.setText(String.valueOf(random.nextInt(6) + 1));
            bossDiceLabel.setText(String.valueOf(random.nextInt(6) + 1));
        }));

        rolling.setCycleCount(12);
        rolling.setOnFinished(event -> {
            playerRoll = random.nextInt(6) + 1;
            bossRoll = random.nextInt(6) + 1;

            playerDiceLabel.setText(String.valueOf(playerRoll));
            bossDiceLabel.setText(String.valueOf(bossRoll));

            if (playerRoll >= bossRoll) {
                resultLabel.setText("You blocked the attack.");
            } else {
                resultLabel.setText("The boss rolled higher.");
            }

            continueButton.setVisible(true);
            continueButton.setDisable(false);
        });

        rolling.play();
    }

    @FXML
    public void onContinue() {
        if (resultHandler != null) {
            resultHandler.accept(new DiceRollResult(playerRoll, bossRoll));
        }

        Stage stage = (Stage) continueButton.getScene().getWindow();
        stage.close();
    }
}
