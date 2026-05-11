package seng201.team0.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the start screen.
 * Animations:
 *   1. Title fades in on load
 *   2. START button fades in after title, then pulses a gold glow indefinitely
 *   3. Clicking START triggers a black fade-out → loads setup.fxml → fades back in
 * @author Mohammed, Xinyi
 */
public class StartController {

    @FXML private Label titleLabel;
    @FXML private Button startButton;
    @FXML private Rectangle transitionOverlay;
    @FXML private javafx.scene.layout.AnchorPane rootPane;
    @FXML private javafx.scene.image.ImageView backgroundImage;
    @FXML private javafx.scene.layout.Pane contentPane;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, backgroundImage, contentPane);
        applyTitleGlow();
        playTitleFadeIn();
    }

    private void setupResponsiveScaling() {
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                rootPane.scaleXProperty().bind(
                        newScene.widthProperty().divide(900)
                );
                rootPane.scaleYProperty().bind(
                        newScene.heightProperty().divide(600)
                );
            }
        });
    }

    // ── Title ─────────────────────────────────────────────────────────────────

    private void applyTitleGlow() {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.color(0.8, 0.65, 0.1, 0.8));
        glow.setRadius(20);
        glow.setSpread(0.2);
        titleLabel.setEffect(glow);
    }

    private void playTitleFadeIn() {
        FadeTransition titleFade = new FadeTransition(Duration.seconds(2), titleLabel);
        titleFade.setFromValue(0.0);
        titleFade.setToValue(1.0);
        titleFade.setDelay(Duration.millis(300));
        titleFade.setOnFinished(e -> playButtonFadeIn());
        titleFade.play();
    }

    // ── Button ────────────────────────────────────────────────────────────────

    private void playButtonFadeIn() {
        FadeTransition btnFade = new FadeTransition(Duration.seconds(1), startButton);
        btnFade.setFromValue(0.0);
        btnFade.setToValue(1.0);
        btnFade.setOnFinished(e -> startButtonPulse());
        btnFade.play();
    }

    private void startButtonPulse() {
        DropShadow pulse = new DropShadow();
        pulse.setColor(Color.GOLD);
        pulse.setRadius(2);
        startButton.setEffect(pulse);

        Timeline pulseAnim = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(pulse.radiusProperty(), 2)),
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(pulse.radiusProperty(), 28))
        );
        pulseAnim.setAutoReverse(true);
        pulseAnim.setCycleCount(Animation.INDEFINITE);
        pulseAnim.play();
    }

    // ── Transition on START click ─────────────────────────────────────────────

    /**
     * On START click:
     *   1. Fade the black overlay IN (screen goes dark over 500ms)
     *   2. Load setup.fxml
     *   3. Swap the scene
     *   4. Fade the black overlay OUT on the new scene (500ms fade in)
     */
    @FXML
    public void onStart() {
        // Disable button so it can't be clicked twice mid-transition
        startButton.setDisable(true);

        FadeTransition fadeToBlack = new FadeTransition(Duration.millis(500), transitionOverlay);
        fadeToBlack.setFromValue(0.0);
        fadeToBlack.setToValue(1.0);
        fadeToBlack.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/Lore1.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) startButton.getScene().getWindow();
                double currentWidth = stage.getScene().getWidth();
                double currentHeight = stage.getScene().getHeight();
                boolean wasFullScreen = stage.isFullScreen();

                ScreenUtil.switchScene(stage, root);
                stage.setTitle("The Fall of Shurima");


                // Fade in from black on the new scene
                // We add a temporary black rectangle on top of the setup scene and fade it out
                javafx.scene.layout.AnchorPane setupRoot =
                        (javafx.scene.layout.AnchorPane) root;

                Rectangle fadeInRect = new Rectangle(900, 600, Color.BLACK);
                fadeInRect.setMouseTransparent(true);
                setupRoot.getChildren().add(fadeInRect);

                FadeTransition fadeFromBlack = new FadeTransition(
                        Duration.millis(500), fadeInRect);
                fadeFromBlack.setFromValue(1.0);
                fadeFromBlack.setToValue(0.0);
                fadeFromBlack.setOnFinished(ev ->
                        setupRoot.getChildren().remove(fadeInRect));
                fadeFromBlack.play();

            } catch (Exception ex) {
                ex.printStackTrace();
                startButton.setDisable(false);
            }
        });
        fadeToBlack.play();
    }
}