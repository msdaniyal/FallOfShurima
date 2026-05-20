package seng201.team0.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Game;

/**
 * Final result screen shown after the true ending, hard-path victory, or loss.
 */
public class EndController implements GameDataReceiver {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statsLabel;
    @FXML private VBox partyStatsBox;

    private Game game;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
    }

    @Override
    public void setGameData(Game game) {
        this.game = game;
        updateDisplay();
    }

    private void updateDisplay() {
        if (game == null) {
            return;
        }

        titleLabel.setText(game.getEndingTitle());
        descriptionLabel.setText(game.getEndingDescription());
        statsLabel.setText("Gold: " + game.getGuild().getGold()
                + "    Loyalty threshold: " + game.getLoyaltyThreshold()
                + "    Party left: " + game.getGuild().getMainParty().size());

        if (game.isPlayerWon()) {
            setBackground("/images/end_victory.png");
        } else {
            setBackground("/images/end_defeat.png");
        }

        partyStatsBox.getChildren().clear();
        if (game.getGuild().getMainParty().isEmpty()) {
            Label none = new Label("No adventurers survived.");
            none.getStyleClass().add("ending-party-label");
            partyStatsBox.getChildren().add(none);
            return;
        }

        for (Adventurer member : game.getGuild().getMainParty()) {
            Label label = new Label(member.getName()
                    + "   HP " + member.getCurrentHealth() + "/" + member.getMaxHealth()
                    + "   ATK " + member.getAttack()
                    + "   Loyalty " + member.getLoyalty()
                    + "   Madness " + member.getMadness());
            label.getStyleClass().add("ending-party-label");
            partyStatsBox.getChildren().add(label);
        }
    }

    private void setBackground(String path) {
        try {
            backgroundImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) {
            backgroundImage.setImage(null);
        }
    }

    @FXML
    public void onReturnToMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
            Parent root = loader.load();

            GameDataReceiver controller = loader.getController();
            controller.setGameData(game);

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Final Result");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
