package seng201.team0.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import seng201.team0.controller.MemoryGameController;
import seng201.team0.controller.MainMenuController;
import seng201.team0.models.Adventurer;
import seng201.team0.models.Difficulty;
import seng201.team0.models.Faction;
import seng201.team0.models.Game;
import seng201.team0.models.Guild;

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

//        boolean DEBUG_MAIN_MENU = true;
//
//        if (DEBUG_MAIN_MENU) {
//            startDebugMainMenu(primaryStage);
//            return;
//        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/start.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setTitle("The Fall of Shurima");
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show();

        primaryStage.maximizedProperty().addListener((obs, wasMaximized, isNowMaximized) -> {
            if (isNowMaximized) {
                primaryStage.setFullScreen(true);
            }
        });

//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/setup.fxml"));
//            Parent root = loader.load();
//
//            primaryStage.setScene(new Scene(root, 900, 600));
//            primaryStage.setTitle("The Fall of Shurima");
//            primaryStage.show();

//        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MemoryGame.fxml"));
//        Parent root = loader.load();
//
//        MemoryGameController controller = loader.getController();
//
//        // FIX 1: Difficulty is now required by BossFight
//        seng201.team0.models.Difficulty difficulty = seng201.team0.models.Difficulty.NORMAL;
//
//        seng201.team0.models.Guild guild =
//                new seng201.team0.models.Guild("Test Guild", difficulty.getStartingGold(), seng201.team0.models.Faction.AATROX);
//
//        seng201.team0.models.Adventurer mainAdventurer =
//                new seng201.team0.models.Adventurer(
//                        "MC", 30, 12, 6, 10,
//                        seng201.team0.models.Faction.AATROX,
//                        seng201.team0.models.Faction.AATROX,
//                        "Main character"
//                );
//
//        // FIX 2: Boss constructor now requires BossAbility and abilityFrequency
//        seng201.team0.models.Boss boss =
//                new seng201.team0.models.Boss(
//                        "Jax", 50, 20, 5,
//                        100, 10, -15,
//                        "The Grandmaster fights alone.",
//                        seng201.team0.models.BossAbility.IMMUNE_TURN,
//                        2
//                );
//
//        // FIX 3: BossFight constructor takes (boss, sequence, difficulty) — not an adventurer
//        seng201.team0.models.BossFight bossFight =
//                new seng201.team0.models.BossFight(boss, 1, difficulty);
//
//        guild.addToMainParty(mainAdventurer);
//
//        // FIX 4: "BossFight" (capital B) is the class name — "bossFight" (lowercase) is the instance
//        controller.setFightData(bossFight, guild, mainAdventurer);
//
//        Scene scene = new Scene(root, 900, 600);
//        primaryStage.setScene(scene);
//        primaryStage.setTitle("Boss Fight");
//        primaryStage.show();
    }

    private void startDebugMainMenu(Stage primaryStage) throws IOException {
        Difficulty difficulty = Difficulty.NORMAL;

        Guild guild = new Guild(
                "Debug Guild",
                difficulty.getStartingGold(),
                Faction.AATROX
        );

        guild.addToMainParty(new Adventurer(
                "Baalkux",
                110,
                18,
                8,
                20,
                Faction.AATROX,
                guild.getPlayerFaction(),
                "A brutal Darkin warrior with strong attack power."
        ));

        guild.addToMainParty(new Adventurer(
                "Horazi",
                90,
                20,
                5,
                25,
                Faction.XOLAANI,
                guild.getPlayerFaction(),
                "A celestial marksman with high damage but lower defense."
        ));

        guild.addToMainParty(new Adventurer(
                "Zaahen",
                105,
                17,
                9,
                20,
                Faction.NEUTRAL,
                guild.getPlayerFaction(),
                "A balanced warrior who is not tied strongly to either side."
        ));

        Game game = new Game(guild, difficulty);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainmenu.fxml"));
        Parent root = loader.load();

        MainMenuController controller = loader.getController();
        controller.setGameData(game);

        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.setTitle("The Fall of Shurima — Debug Main Menu");
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }

    /**
     * Launches the FXML application, this must be called from another class (in this case App.java)
     * otherwise JavaFX errors out and does not run.
     * @param args command line arguments
     */
    public static void launchWrapper(String[] args) {
        launch(args);
    }
}