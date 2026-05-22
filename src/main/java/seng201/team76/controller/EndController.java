package seng201.team76.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seng201.team76.models.Adventurer;
import seng201.team76.models.Game;
import seng201.team76.models.Guild;

/**
 * Final ending screen.
 * Starts as a cinematic ending page, then reveals survivor/lost-character results
 * only when the player presses View Result.
 */
public class EndController implements GameDataReceiver {

    /**
     * Creates the ending screen controller.
     */
    public EndController() {
    }

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;
    @FXML private ImageView backgroundImage;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label statsLabel;
    @FXML private ScrollPane resultScrollPane;
    @FXML private VBox partyStatsBox;
    @FXML private Button viewResultButton;

    private Game game;
    private boolean resultsVisible = false;

    /**
     * Sets up the ending screen after the FXML loads.
     */
    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        hideResults();
    }

    @Override
    /**
     * Receives the current game and updates the ending display.
     *
     * @param game The current game
     */
    public void setGameData(Game game) {
        this.game = game;
        updateDisplay();
    }

    private void updateDisplay() {
        if (game == null) {
            return;
        }

        Guild guild = game.getGuild();

        titleLabel.setText("The End");
        descriptionLabel.setText("All Darkin are sealed until they return.");
        statsLabel.setText("The fate of the guild has been recorded.");

        if (shouldUseVictoryImage()) {
            setBackground("/images/end_victory.png");
        } else {
            setBackground("/images/end_defeat.png");
        }

        partyStatsBox.getChildren().clear();
        addSurvivorSection(guild);
        addDepartedSection(guild);
        hideResults();
    }

    private boolean shouldUseVictoryImage() {
        // The Zoe ending is treated as the final sealing scene, even if Zoe defeats the party.
        // This matches the story beat: all Darkin are sealed until they return.
        return game.isTwilightEnding() || game.isPlayerWon();
    }

    /**
     * Shows or hides the detailed result section.
     */
    @FXML
    public void onViewResult() {
        resultsVisible = !resultsVisible;

        if (resultScrollPane != null) {
            resultScrollPane.setVisible(resultsVisible);
            resultScrollPane.setManaged(resultsVisible);
        }

        if (viewResultButton != null) {
            viewResultButton.setText(resultsVisible ? "Hide Result" : "View Result");
        }
    }

    private void hideResults() {
        resultsVisible = false;

        if (resultScrollPane != null) {
            resultScrollPane.setVisible(false);
            resultScrollPane.setManaged(false);
        }

        if (viewResultButton != null) {
            viewResultButton.setText("View Result");
        }
    }

    private void addSurvivorSection(Guild guild) {
        partyStatsBox.getChildren().add(createSectionHeader("Survivors"));

        boolean hasLivingSurvivor = false;
        for (Adventurer member : guild.getMainParty()) {
            if (!member.isDead()) {
                hasLivingSurvivor = true;
                partyStatsBox.getChildren().add(createStoryLabel(buildSurvivorSummary(member)));
            }
        }

        if (!hasLivingSurvivor) {
            partyStatsBox.getChildren().add(createStoryLabel("No adventurers survived the sealing."));
        }
    }

    private void addDepartedSection(Guild guild) {
        boolean hasDepartedRecords = !guild.getDepartedAdventurers().isEmpty();
        boolean hasDeadMembersStillInParty = countDeadMembersStillInParty(guild) > 0;

        if (!hasDepartedRecords && !hasDeadMembersStillInParty) {
            return;
        }

        partyStatsBox.getChildren().add(createSectionHeader("Lost or Departed"));

        for (Guild.DepartedAdventurerRecord record : guild.getDepartedAdventurers()) {
            partyStatsBox.getChildren().add(createStoryLabel(buildDepartedSummary(record)));
        }

        for (Adventurer member : guild.getMainParty()) {
            if (member.isDead() && !hasDepartedRecord(guild, member.getName())) {
                partyStatsBox.getChildren().add(createStoryLabel(buildFallenCurrentPartySummary(member)));
            }
        }
    }

    private int countDeadMembersStillInParty(Guild guild) {
        int count = 0;
        for (Adventurer member : guild.getMainParty()) {
            if (member.isDead() && !hasDepartedRecord(guild, member.getName())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasDepartedRecord(Guild guild, String name) {
        for (Guild.DepartedAdventurerRecord record : guild.getDepartedAdventurers()) {
            if (record.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private Label createSectionHeader(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("ending-section-label");
        return label;
    }

    private Label createStoryLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add("ending-party-label");
        return label;
    }

    private String buildSurvivorSummary(Adventurer member) {
        String loyaltyText;
        if (member.getLoyalty() >= 75) {
            loyaltyText = "still remains fiercely loyal to you";
        } else if (member.getLoyalty() >= 50) {
            loyaltyText = "still follows you, though doubt now sits behind their eyes";
        } else if (member.getLoyalty() >= 25) {
            loyaltyText = "remains with you, but the bond has been badly damaged";
        } else {
            loyaltyText = "barely remains by your side";
        }

        String madnessText = describeMadness(member.getMadness());
        return member.getName() + " " + loyaltyText + "; however, " + madnessText + ".";
    }

    private String buildDepartedSummary(Guild.DepartedAdventurerRecord record) {
        String madnessText = describeMadness(record.getMadness());

        if (Guild.FATE_FELL.equals(record.getFate())) {
            return record.getName() + " fell before the sealing. Their name is still carried by the guild; "
                    + madnessText + ".";
        }

        String loyaltyText;
        if (record.getLoyalty() <= 0) {
            loyaltyText = "their loyalty finally broke";
        } else if (record.getLoyalty() < 30) {
            loyaltyText = "their trust in the guild was almost gone";
        } else {
            loyaltyText = "they walked away before the final chapter";
        }

        return record.getName() + " left the party when " + loyaltyText + "; " + madnessText + ".";
    }

    private String buildFallenCurrentPartySummary(Adventurer member) {
        String madnessText = describeMadness(member.getMadness());
        return member.getName() + " fell during the final sealing. Their story ends with the guild; "
                + madnessText + ".";
    }

    private String describeMadness(int madness) {
        if (madness >= 75) {
            return "the rift had made them mad";
        } else if (madness >= 50) {
            return "the rift's whispers still haunted them";
        } else if (madness >= 25) {
            return "the rift had left marks on their mind";
        }
        return "their mind remained steady despite everything";
    }

    private void setBackground(String path) {
        try {
            backgroundImage.setImage(new Image(getClass().getResource(path).toExternalForm()));
        } catch (Exception e) {
            backgroundImage.setImage(null);
        }
    }

    /**
     * Starts a new game from the start screen.
     */
    @FXML
    public void onNewGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) rootPane.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the JavaFX application.
     */
    @FXML
    public void onExitGame() {
        Platform.exit();
    }
}
