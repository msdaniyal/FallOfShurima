package seng201.team0.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Helper class for screen resizing.
 * The game is designed at 900 x 600.
 * The whole contentPane scales uniformly and stays centered.
 * @author Mohammed, Xinyi
 */
public class ScreenUtil {

    private static final double BASE_WIDTH = 900.0;
    private static final double BASE_HEIGHT = 600.0;

    public static void setupStretch(AnchorPane rootPane, ImageView backgroundImage, Pane contentPane) {
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                // Remove old bindings first, to avoid "bound value cannot be set" errors.
                rootPane.prefWidthProperty().unbind();
                rootPane.prefHeightProperty().unbind();

                contentPane.scaleXProperty().unbind();
                contentPane.scaleYProperty().unbind();
                contentPane.translateXProperty().unbind();
                contentPane.translateYProperty().unbind();

                if (backgroundImage != null) {
                    backgroundImage.fitWidthProperty().unbind();
                    backgroundImage.fitHeightProperty().unbind();
                }

                // Root follows the scene size.
                rootPane.prefWidthProperty().bind(newScene.widthProperty());
                rootPane.prefHeightProperty().bind(newScene.heightProperty());

                // Content is always designed as 900 x 600.
                contentPane.setPrefWidth(BASE_WIDTH);
                contentPane.setPrefHeight(BASE_HEIGHT);
                contentPane.setMinWidth(BASE_WIDTH);
                contentPane.setMinHeight(BASE_HEIGHT);
                contentPane.setMaxWidth(BASE_WIDTH);
                contentPane.setMaxHeight(BASE_HEIGHT);

                // Keep the contentPane's real layout position fixed.
                // Do NOT bind layoutX/layoutY.
                contentPane.setLayoutX(0);
                contentPane.setLayoutY(0);

                // If the background image is inside contentPane,
                // keep it at the base design size.
                if (backgroundImage != null) {
                    backgroundImage.setFitWidth(BASE_WIDTH);
                    backgroundImage.setFitHeight(BASE_HEIGHT);
                    backgroundImage.setPreserveRatio(false);
                }

                // Uniform scale: keeps the game scene ratio.
                NumberBinding scale = Bindings.min(
                        rootPane.widthProperty().divide(BASE_WIDTH),
                        rootPane.heightProperty().divide(BASE_HEIGHT)
                );

                contentPane.scaleXProperty().bind(scale);
                contentPane.scaleYProperty().bind(scale);

                // Center using translateX / translateY instead of layoutX / layoutY.
                // This avoids JavaFX layout binding errors.
                contentPane.translateXProperty().bind(
                        rootPane.widthProperty().subtract(BASE_WIDTH).divide(2)
                );

                contentPane.translateYProperty().bind(
                        rootPane.heightProperty().subtract(BASE_HEIGHT).divide(2)
                );
            }
        });
    }

    public static void switchScene(Stage stage, Parent root) {
        boolean wasFullScreen = stage.isFullScreen();

        if (stage.getScene() == null) {
            Scene scene = new Scene(root, 900, 600);
            scene.setFill(Color.BLACK);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }

        stage.setFullScreen(wasFullScreen);
    }
}