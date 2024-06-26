package xyz.ravencrows.pihitan;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.navigator.ScreenIdentifierService;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

/**
 * Overlay program
 */
public class OverlayController {
  public static final int CIRCLE_RADIUS = 50;
  private final PihitanConfig config;
  private final ScreenNavigator navigator;
  private static final Logger logger = LoggerFactory.getLogger(OverlayController.class);

  // Currently only used in showing wireframe
  final KeyCombination SHOW_DEBUG_KEY = new KeyCodeCombination(KeyCode.DIGIT2, KeyCombination.SHIFT_DOWN);

  // reconfigure screen bounds
  final KeyCombination RECONFIGURE_SCREEN = new KeyCodeCombination(KeyCode.DIGIT1, KeyCombination.SHIFT_DOWN);

  private boolean showDebug;

  private boolean escPressed;

  public OverlayController(PihitanConfig config) {
    this.navigator = new ScreenNavigator(config.getTemplate(), config.getDspBounds(), new Robot());
    this.config = config;
  }

  public void start() {
    logger.info("Initializing overlay app, config: {}", config);
    Stage stage = new Stage();
    stage.setX(0);
    stage.setY(0);

    escPressed = false;
    final Template template = config.getTemplate();
    Pane root = new Pane();
    root.setPadding(new Insets(10));
    Scene scene = new Scene(root);
    Label label = new Label("Using " + template.getId() + " by " + template.getAuthor() + "\nPress esc twice to exit");

    Rectangle2D points = config.getDspBounds();
    Rectangle wireframe = new Rectangle(points.getMinX(), points.getMinY(), points.getWidth(), points.getHeight());
    wireframe.setFill(null);
    wireframe.setStroke(getTemplateColor(config.getTemplate()));
    root.getChildren().add(wireframe);
    showDebug = config.isDebugMode();
    wireframe.setVisible(showDebug);

    label.setStyle("""
            -fx-text-fill: #F1F6F9;
            -fx-background-color: rgba(0, 0, 0, 0.7);
            -fx-padding: 20px;
            """);
    label.setFont(new Font(24));
    label.relocate(0,0);

    root.setStyle("-fx-background-color: rgba(0, 0, 0, 0)");
    root.getChildren().add(label);

    scene.setFill(Color.TRANSPARENT);
    scene.setCursor(Cursor.CROSSHAIR);

    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setResizable(false);
    stage.setTitle("Pihitan overlay");
    stage.setScene(scene);
    stage.setAlwaysOnTop(true);
    stage.setMaximized(true);

    Circle mouseHighlight = new Circle();
    Pane highlight = new Pane();
    highlight.getChildren().add(mouseHighlight);

    // Black path
    mouseHighlight.setRadius(0); // hide by default
    mouseHighlight.setFill(null);
    mouseHighlight.setStroke(new Color(0, 0, 0, 0.3));

    // glow and bloom gets clipped
    mouseHighlight.setEffect(new DropShadow());

    // rotating arc
    Arc arc = createArc(highlight, config.getTemplate());

    // add highlight
    root.getChildren().add(highlight);

    InputListener listener = config.getInput();
    listener.listenToSceneAction(scene, actionCode -> {
      label.setText(navigator.navigate(actionCode, scene));

      // highlight mouse
      Point2D point = navigator.getRobot().getMousePosition();
      highlight.relocate(point.getX(), point.getY());
      mouseHighlight.setRadius(CIRCLE_RADIUS);
      arc.setRadiusX(CIRCLE_RADIUS);
      arc.setRadiusY(CIRCLE_RADIUS);
    });

    stage.setOnCloseRequest(event -> listener.stopListener());

    // press esc to close
    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      boolean escapeEvent = KeyCode.ESCAPE == keyEvent.getCode();
      if(escapeEvent && this.escPressed) {
        logger.info("Escape pressed");
          listener.stopListener();
          stage.close();
      } else if (escapeEvent) {
        label.setText("Press esc again to exit");
        this.escPressed = true;
        return;
      } else {
        this.escPressed = false;
      }

      if (SHOW_DEBUG_KEY.match(keyEvent)) {
        logger.info("Show debug");
        // Shift + Tab will show/hide the wireframe
        showDebug = !showDebug;
        wireframe.setVisible(showDebug);

        label.setText("Toggle wireframe");
      } else if (RECONFIGURE_SCREEN.match(keyEvent)) {
        logger.info("Reconfigure screen");
        Rectangle2D newPoints = ScreenIdentifierService.getInstance().getScreenSize(config.getTemplate().getWindowName());
        config.setDspBounds(newPoints);

        // recompute template
        navigator.recomputeAppBounds(config.getDspBounds());

        // reconfigure wireframe
        wireframe.setX(newPoints.getMinX());
        wireframe.setY(newPoints.getMinY());
        wireframe.setWidth(newPoints.getWidth());
        wireframe.setHeight(newPoints.getHeight());

        // user should move the cursor manually, so it goes to the correct position
        label.setText("Updated app size / position.");
      }
    });
    stage.show();
    logger.info("Start overlay app");
  }

  private static Arc createArc(Pane highlight, Template template) {
    Arc arc = new Arc();
    arc.setFill(null);
    arc.setStrokeWidth(5);
    arc.setEffect(new Glow(0.5));
    arc.setStartAngle(67.5f);
    arc.setLength(45f);
    arc.setBlendMode(BlendMode.EXCLUSION);
    highlight.getChildren().add(arc);

    // arc stroke, defaults to white
    Color highlightColor = getTemplateColor(template);
    arc.setStroke(highlightColor);

    // rotating arc fx
    Timeline animation = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(arc.startAngleProperty(), arc.getStartAngle(), Interpolator.EASE_IN)),
            new KeyFrame(Duration.seconds(1), new KeyValue(arc.startAngleProperty(), arc.getStartAngle() - 360, Interpolator.EASE_OUT))
    );
    animation.setCycleCount(Animation.INDEFINITE);
    animation.play();
    return arc;
  }

  private static Color getTemplateColor(Template template) {
    return template.getColor() != null ?
            template.getColor() :
            new Color(1, 1, 1, 0.5);
  }
}
