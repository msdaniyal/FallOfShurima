package seng201.team76.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for the lore screen shown between start and setup.
 * Sequence:
 * 1. Screen fades in from black
 * 2. Golden sunburst explosion animates on the canvas
 * 3. Burst fades out, lore text fades in and types out letter by letter
 * 4. "Click to continue" prompt appears
 * 5. Player clicks -> fade to black -> setup.fxml loads
 * @author Mohammed, Xinyi
 */
public class LoreController1 {

    @FXML private AnchorPane rootPane;
    @FXML private Pane contentPane;

    @FXML private Canvas burstCanvas;
    @FXML private Label loreLabel;
    @FXML private Label continueLabel;
    @FXML private Rectangle clickCatcher;
    @FXML private Rectangle transitionOverlay;

    private static final String LORE_TEXT =
            "\"How the empire that once ruled Runeterra fell...\"";

    private boolean readyToContinue = false;

    @FXML
    public void initialize() {
        ScreenUtil.setupStretch(rootPane, null, contentPane);
        fadeInFromBlack();
    }

    private void fadeInFromBlack() {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(600), transitionOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.setOnFinished(e -> playBurst());
        fadeIn.play();
    }

    private void playBurst() {
        GraphicsContext gc = burstCanvas.getGraphicsContext2D();
        double cx = 450;
        double cy = 300;

        final long[] startTime = {-1};
        final double burstDuration = 1200;

        AnimationTimer burst = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (startTime[0] < 0) {
                    startTime[0] = now;
                }

                double elapsed = (now - startTime[0]) / 1_000_000.0;
                double progress = Math.min(elapsed / burstDuration, 1.0);

                gc.clearRect(0, 0, 900, 600);

                int numRays = 24;

                for (int i = 0; i < numRays; i++) {
                    double angle = Math.toRadians(i * (360.0 / numRays));
                    double rayLen = progress * 600;
                    double rayAlpha = (1.0 - progress) * 0.6;

                    gc.setStroke(Color.color(1.0, 0.85, 0.2, rayAlpha));
                    gc.setLineWidth(2 + (1.0 - progress) * 6);
                    gc.strokeLine(
                            cx,
                            cy,
                            cx + Math.cos(angle) * rayLen,
                            cy + Math.sin(angle) * rayLen
                    );
                }

                double radius = progress * 500;
                double alpha = (1.0 - progress) * 0.9;

                RadialGradient glow = new RadialGradient(
                        0,
                        0,
                        cx,
                        cy,
                        radius,
                        false,
                        CycleMethod.NO_CYCLE,
                        new Stop(0.0, Color.color(1.0, 0.95, 0.4, alpha)),
                        new Stop(0.3, Color.color(1.0, 0.75, 0.1, alpha * 0.6)),
                        new Stop(1.0, Color.color(1.0, 0.6, 0.0, 0.0))
                );

                gc.setFill(glow);
                gc.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

                if (progress >= 1.0) {
                    stop();

                    FadeTransition canvasFade = new FadeTransition(Duration.millis(400), burstCanvas);
                    canvasFade.setFromValue(1.0);
                    canvasFade.setToValue(0.0);
                    canvasFade.setOnFinished(ev -> startTypewriter());
                    canvasFade.play();
                }
            }
        };

        burst.start();
    }

    private void startTypewriter() {
        loreLabel.setText("");

        FadeTransition labelFade = new FadeTransition(Duration.millis(400), loreLabel);
        labelFade.setFromValue(0.0);
        labelFade.setToValue(1.0);
        labelFade.setOnFinished(e -> typeNextChar(0));
        labelFade.play();
    }

    private void typeNextChar(int index) {
        if (index >= LORE_TEXT.length()) {
            showContinuePrompt();
            return;
        }

        loreLabel.setText(LORE_TEXT.substring(0, index + 1));

        PauseTransition pause = new PauseTransition(Duration.millis(50));
        pause.setOnFinished(e -> typeNextChar(index + 1));
        pause.play();
    }

    private void showContinuePrompt() {
        FadeTransition promptFade = new FadeTransition(Duration.millis(600), continueLabel);
        promptFade.setFromValue(0.0);
        promptFade.setToValue(1.0);
        promptFade.setOnFinished(e -> {
            readyToContinue = true;
            clickCatcher.setMouseTransparent(false);
        });
        promptFade.play();
    }

    @FXML
    public void onContinue() {
        if (!readyToContinue) {
            return;
        }

        readyToContinue = false;
        clickCatcher.setMouseTransparent(true);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(600), transitionOverlay);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        fadeOut.setOnFinished(e -> loadSetup());
        fadeOut.play();
    }

    private void loadSetup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/setup.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) loreLabel.getScene().getWindow();
            ScreenUtil.switchScene(stage, root);
            stage.setTitle("The Fall of Shurima — Setup");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}