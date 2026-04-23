package seng201.team0.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Class starts the javaFX application window
 * @author seng201 teaching team
 */
public class MainWindow extends Application {

    /**
     * Opens the gui with the fxml content specified in resources/fxml/main.fxml
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     * @throws IOException if there is an issue loading fxml file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root = loader.load();

        seng201.team0.controller.BossFightController controller = loader.getController();

        seng201.team0.models.Guild guild =
                new seng201.team0.models.Guild("Test Guild", 100, seng201.team0.models.Faction.AATROX);

        seng201.team0.models.Adventurer mainAdventurer =
                new seng201.team0.models.Adventurer(
                        "MC", 30, 12, 6, 10,
                        seng201.team0.models.Faction.AATROX,
                        seng201.team0.models.Faction.AATROX,
                        "Main character"
                );

        seng201.team0.models.Boss boss =
                new seng201.team0.models.Boss(
                        "Jax", 50, 20, 5,
                        100, 10, -15, "Boss"
                );

        seng201.team0.models.BossFight bossFight =
                new seng201.team0.models.BossFight(boss, 1, mainAdventurer);

        guild.addToMainParty(mainAdventurer);

        controller.setFightData(bossFight, guild, mainAdventurer);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Boss Fight");
        primaryStage.show();
    }

    /**
     * Launches the FXML application, this must be called from another class (in this cass App.java) otherwise JavaFX
     * errors out and does not run
     * @param args command line arguments
     */
    public static void launchWrapper(String [] args) {
        launch(args);
    }



}
