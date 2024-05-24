package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manual screen points identifier for fallback
 */
public class ManualScreenIdentifier implements ScreenPointsIdentifier {
  private static final Logger logger = LoggerFactory.getLogger(ManualScreenIdentifier.class);
  private Pair<Double, Double> upperLeft;
  private Pair<Double, Double> lowerRight;
  private int step = 0;
  private Rectangle2D rect;

  @Override
  public Rectangle2D determineScreenSize(String windowName) {
    Stage stage = new Stage();
    HBox root = new HBox();
    Scene scene = new Scene(root);
    Label label = new Label("Unable to detect app, please manually determine dimensions\nClick on the upper left corner of your target window");
    label.setTextAlignment(TextAlignment.CENTER);

    label.setPadding(new Insets(10));
    label.setStyle("-fx-text-fill: #F1F6F9; -fx-background-color: rgba(0, 0, 0, 0.8)");
    label.setFont(new Font(24));

    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3)");
    root.getChildren().add(label);

    scene.setFill(Color.TRANSPARENT);
    scene.setCursor(Cursor.CROSSHAIR);

    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setResizable(true);
    stage.setTitle("Specify window size");
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.setAlwaysOnTop(true);

    this.step = 0;
    scene.setOnMouseClicked(mouseEvent -> {
      if (this.step == 0) {
        upperLeft = new Pair<>(mouseEvent.getX(), mouseEvent.getY());
        label.setText("Click on the lower right corner of your target window");
      } else if (this.step == 1) {
        lowerRight = new Pair<>(mouseEvent.getX(), mouseEvent.getY());

        final double width = lowerRight.getKey() - upperLeft.getKey();
        final double height = lowerRight.getValue() - upperLeft.getValue();
        final boolean invalidDimension = width <= 0 || height <= 0;

        // validate first
        label.setText(invalidDimension
                ? "Invalid points, please re-setup"
                : "Click anywhere to continue");
        if (!invalidDimension) {
          logger.info("Valid points, saving");
          rect = new Rectangle2D(upperLeft.getKey(), upperLeft.getValue(), width, height);
        } else {
          logger.error("Invalid points upperLeft: {}, lowerRight: {}", upperLeft, lowerRight);
        }
      } else {
        logger.info("Exiting window size screen");
        stage.close();
      }
      this.step++;
    });

    stage.showAndWait();
    return rect;
  }
}
