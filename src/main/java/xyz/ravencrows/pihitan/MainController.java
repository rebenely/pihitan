package xyz.ravencrows.pihitan;

import com.badlogic.gdx.controllers.Controller;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.input.SDLGamepadInputListener;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.GsonUtil;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main controller for the app
 * Also includes the stage for determining screen sizes and the navigation program
 */
public class MainController {
  private static final Logger logger = LoggerFactory.getLogger(MainController.class);

  private Pair<Double, Double> upperLeft;
  private Pair<Double, Double> lowerRight;

  // draggable screen
  private double xOffset;
  private double yOffset;

  private int step;

  private final PihitanConfig config = PihitanConfig.getInstance();

  @FXML
  protected Button templateSelectBtn;
  @FXML
  protected ChoiceBox<String> inputTypeSelect;
  @FXML
  public Button winSizeBtn;

  private Map<String, InputListener> initializedInputs;

  @FXML
  public void initialize() {
    logger.info("Initializing MainController");
    // get plugged in controllers
    List<String> inputOptions = new ArrayList<>();
    inputOptions.add(KeyboardInputListener.NAME);
    for(Controller controller : config.getManager().getControllers()){
      inputOptions.add(controller.getName());
    }

    inputTypeSelect.getItems().removeAll(inputTypeSelect.getItems());
    inputTypeSelect.getItems().addAll(inputOptions);

    // set keyboard as default
    final InputListener kbListener = new KeyboardInputListener();
    config.setInput(kbListener);
    inputTypeSelect.getSelectionModel().select(KeyboardInputListener.NAME);

    initializedInputs = new HashMap<>();
    initializedInputs.put(KeyboardInputListener.NAME, kbListener);

    logger.info("MainController initialized");
  }

  /**
   * Start the navigation program
   */
  @FXML
  protected void startOverlay() {
    if(!validate()) {
      logger.error("Invalid config");
      return; // do not start
    }

    logger.info("Config valid, starting overlay");

    initInputSelect();

    final OverlayController controller = new OverlayController(config);
    controller.start();

    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setAlwaysOnTop(false);
    currentStage.hide();
  }

  /**
   * Validate before starting
   */
  private boolean validate() {
    boolean noErrors = true;
    if(config.getDspBounds() == null) {
      winSizeBtn.getStyleClass().add("step-error");
      noErrors = false;
      logger.error("No dsp bounds");
    } else {
      winSizeBtn.getStyleClass().remove("step-error");
    }

    if(config.getTemplate() == null) {
      templateSelectBtn.getStyleClass().add("step-error");
      noErrors = false;
      logger.error("No template selected");
    } else {
      templateSelectBtn.getStyleClass().remove("step-error");
    }

    return noErrors;
  }

  /**
   * Show the input config screen
   */
  @FXML
  protected void configureInputType() throws IOException {
    logger.info("Input configure selected");
    initInputSelect();

    // Get the current stage
    FXMLLoader loader = new FXMLLoader(getClass().getResource("input-config.fxml"));
    Scene scene = ScreenUtil.setupScreen(loader);

    // Pass parent scene so we can go back
    ConfigController configController = loader.getController();
    configController.initController(inputTypeSelect.getScene());

    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setScene(scene);
  }

  /**
   * setup selected config first so upon load of InputConfigController, config is properly updated
   * Config is set to Keyboard by default in initialize method
   */
  private void initInputSelect() {
    final String selected = inputTypeSelect.getValue();
    if(selected.equals(KeyboardInputListener.NAME)) {
      // KB is always initialized
      logger.info("Keyboard input selected");
      config.setInput(initializedInputs.get(selected));
      return;
    }

    InputListener inputListener = initializedInputs.get(selected);
    if(inputListener != null) {
      logger.info("Reuse initialized input listener");
      config.setInput(inputListener);
      return;
    }

    // if not yet defined, create input listener
    SDL2ControllerManager manager = config.getManager();
    for(Controller controller : manager.getControllers()){
      final String name = controller.getName();
      if(!selected.equals(name)) {
        continue;
      }
      logger.info("Created input listener {}", name);
      InputListener newInput = new SDLGamepadInputListener((SDL2Controller) controller, manager);
      initializedInputs.put(name, newInput);
      config.setInput(newInput);
    }
  }

  @FXML
  protected void exit(ActionEvent event) {
    logger.info("Exit MainController scene");
    if(config.getInput() != null) {
      config.getInput().stopListener();
    }
    ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
  }

  /**
   * Show the overlay for the user screen size input
   */
  @FXML
  protected void determineWindowSize() {
    logger.info("Determine app dimensions");
    Stage stage = new Stage();
    HBox root = new HBox();
    Scene scene = new Scene(root);
    Label label = new Label("Click on the upper left corner of your archetype window");

    label.setStyle("-fx-text-fill: #F1F6F9");
    label.setFont(new Font(24));

    root.setAlignment(Pos.CENTER);
    root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9)");
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
        label.setText("Click on the lower right corner of your vst window");
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
          config.setDspBounds(new Rectangle2D(upperLeft.getKey(), upperLeft.getValue(), width, height));
        } else {
          logger.error("Invalid points upperLeft: {}, lowerRight: {}", upperLeft, lowerRight);
        }
      } else {
        logger.info("Exiting window size screen");
        stage.close();
      }
      this.step++;
    });

    stage.show();
  }

  @FXML
  protected void selectTemplate() throws IOException {
    final FileChooser fileChooser = new FileChooser();
    final Stage stage = (Stage) templateSelectBtn.getScene().getWindow();
    fileChooser.setInitialDirectory(new File("."));
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Pihitan Templates", "*.json")
    );

    final File selectedFile = fileChooser.showOpenDialog(stage);
    if(selectedFile == null) {
      logger.warn("No file selected");
      return;
    }

    Gson gson = GsonUtil.getInstance();
    try(final BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
      Template template = gson.fromJson(br, Template.class);
      templateSelectBtn.setText(template.getName());

      logger.info("Loaded {}", template.getName());
      config.setTemplate(template);
    }

  }

  @FXML
  protected void windowDragged(MouseEvent event) {
    Stage stage = (Stage) templateSelectBtn.getScene().getWindow();
    stage.setX(event.getScreenX() - xOffset);
    stage.setY(event.getScreenY() - yOffset);
  }

  @FXML
  protected void windowPressed(MouseEvent event) {
    xOffset = event.getSceneX();
    yOffset = event.getSceneY();
  }
}