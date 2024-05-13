package xyz.ravencrows.pihitan;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

/**
 * Overlay program
 */
public class OverlayController {
  private final PihitanConfig config;
  private final ScreenNavigator navigator;
  private static final Logger logger = LoggerFactory.getLogger(OverlayController.class);

  public OverlayController(PihitanConfig config) {
    this.navigator = new ScreenNavigator(config.getTemplate(), config.getDspBounds(), new Robot());
    this.config = config;
  }

  public void start() {
    logger.info("Initializing overlay app, config: {}", config);
    Stage stage = new Stage();
    stage.setX(0);
    stage.setY(0);

    FlowPane root = new FlowPane();
    root.setPadding(new Insets(10, 10, 10, 10));
    Scene scene = new Scene(root);
    Label label = new Label("Press esc to exit");

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

    InputListener listener = config.getInput();
    listener.listenToSceneAction(scene, actionCode -> label.setText(navigator.navigate(actionCode, scene)));

    stage.setOnCloseRequest(event -> listener.stopListener());

    // press esc to close
    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if(KeyCode.ESCAPE == keyEvent.getCode()) {
        logger.info("Escape pressed, exiting");
        listener.stopListener();
        stage.close();
      }
    });
    stage.show();
    logger.info("Start overlay app");
  }
}
