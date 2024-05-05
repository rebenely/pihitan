package xyz.ravencrows.pihitan;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.navigator.NavigatorSection;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

/**
 * Overlay program
 */
public class OverlayController {
  private final PihitanConfig config;
  private final ScreenNavigator navigator;

  public OverlayController(PihitanConfig config) {
    this.navigator = new ScreenNavigator(config.getTemplate(), config.getDspBounds());
    this.config = config;
  }

  public void start() {
    Stage stage = new Stage();
    stage.setX(0);
    stage.setY(0);

    HBox root = new HBox();
    Scene scene = new Scene(root);
    Label label = new Label("Is it working");

    label.setStyle("-fx-text-fill: #F1F6F9");
    label.setFont(new Font(24));

    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
    root.getChildren().add(label);

    scene.setFill(Color.TRANSPARENT);
    scene.setCursor(Cursor.CROSSHAIR);

    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setResizable(false);
    stage.setTitle("Pihitan overlay");
    stage.setScene(scene);
    stage.setAlwaysOnTop(true);
//    Rectangle2D bounds = config.getDspBounds();
//    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
//      if (KeyCode.A.equals(keyEvent.getCode())) {
//        Robot robot = new Robot();
//        robot.mouseMove(bounds.getMinX(), bounds.getMinY());
//      } else if (KeyCode.D.equals(keyEvent.getCode())) {
//        Robot robot = new Robot();
//        robot.mouseMove(bounds.getMaxX(), bounds.getMaxY());
//      }
//    });

    InputListener kbListener = new KeyboardInputListener();
    kbListener.start(scene, navigator, config.getInputSettings());

    stage.show();
//    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
//    stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
//    stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
  }
}
