package xyz.ravencrows.pihitan;

import com.badlogic.gdx.controllers.Controller;
import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.input.SDLGamepadInputListener;
import xyz.ravencrows.pihitan.navigator.JnaScreenIdentifier;
import xyz.ravencrows.pihitan.navigator.ManualScreenIdentifier;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.PersistedConfig;
import xyz.ravencrows.pihitan.userconfig.PersistedInput;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.GsonUtil;
import xyz.ravencrows.pihitan.util.PersistUtil;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Main controller for the app
 * Also includes the stage for determining screen sizes and the navigation program
 */
public class MainController {
  private static final Logger logger = LoggerFactory.getLogger(MainController.class);
  public static final String TEMPLATES = "templates";

  // draggable screen
  private double xOffset;
  private double yOffset;

  private final PihitanConfig config = PihitanConfig.getInstance();

  @FXML
  protected ChoiceBox<String> templateSelect;
  @FXML
  protected ChoiceBox<String> inputTypeSelect;

  private Map<String, InputListener> initializedInputs;

  @FXML
  public void initialize() {
    logger.info("Initializing MainController");

    initializeAvailableTemplates();

    List<String> inputOptions = initializeAvailableInputTypes();

    // always initialize keyboard
    final InputListener kbListener = new KeyboardInputListener(KeyboardInputListener.defaults());
    config.setInput(KeyboardInputListener.NAME, kbListener);
    inputTypeSelect.getSelectionModel().select(KeyboardInputListener.NAME);

    // set Keyboard input as default, may change when loading persisted values
    initializedInputs = new HashMap<>();
    initializedInputs.put(KeyboardInputListener.NAME, kbListener);

    initializePersistedInputConfigs(inputOptions);

    logger.info("MainController initialized");
  }

  /**
   * Get templates in directory
   */
  private void initializeAvailableTemplates() {
    // create if it doesn't exist
    try (Stream<Path> stream = Files.list(Files.createDirectories(Paths.get(TEMPLATES)))) {
      List<String> templates = stream
              .filter(file -> !Files.isDirectory(file) && file.toString().endsWith(".json"))
              .map(Path::getFileName)
              .map(Path::toString)
              .toList();
      logger.info("Templates loaded {}", templates);
      if(templates.isEmpty()) {
        logger.error("No templates found!");
      }

      templateSelect.getItems().removeAll(templateSelect.getItems());
      templateSelect.getItems().addAll(templates);

      // set to persisted value or first in list
      String persistedTemplate = PersistUtil.getConfig().getTemplate();
      String defaultTemplate = !templates.isEmpty() ? templates.get(0) : "";
      if(persistedTemplate != null && templates.contains(persistedTemplate)) {
        defaultTemplate = persistedTemplate;
      }
      templateSelect.getSelectionModel().select(defaultTemplate);
    } catch (Exception e) {
      logger.error("Error encountered reading templates", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Initialized persisted configs from config.json
   */
  private void initializePersistedInputConfigs(List<String> inputOptions) {
    // initialize persisted configs
    final PersistedConfig persistedConfig = PersistUtil.getConfig();
    for (final PersistedInput input : persistedConfig.getInputs()) {
      final String name = input.getName();
      if(!inputOptions.contains(name)) {
        logger.warn("Cannot find {}", name);
        continue;
      }

      InputListener inputListener = initInputSelect(name);
      if(inputListener == null) {
        logger.warn("Was not able to create a listener for {}", name);
        continue;
      }

      inputListener.setKeys(input.getActions()); // set keys from persisted data
      if(name.equalsIgnoreCase(persistedConfig.getInput())) {
        logger.info("Selecting {} as default", name);
        config.setInput(name, inputListener);
        inputTypeSelect.getSelectionModel().select(name);
      }
    }
  }

  /**
   * Initializes available input types
   */
  private List<String> initializeAvailableInputTypes() {
    // get plugged in controllers
    List<String> inputOptions = new ArrayList<>();
    inputOptions.add(KeyboardInputListener.NAME);
    for(Controller controller : config.getManager().getControllers()){
      inputOptions.add(controller.getName());
    }

    inputTypeSelect.getItems().removeAll(inputTypeSelect.getItems());
    inputTypeSelect.getItems().addAll(inputOptions);
    return inputOptions;
  }

  /**
   * Start the navigation program
   */
  @FXML
  protected void startOverlay() {
    logger.info("Config valid, starting overlay");

    // get input
    final String selected = inputTypeSelect.getValue();
    final InputListener listener = initInputSelect(selected);
    config.setInput(selected, listener);

    // get template
    final String templateSelected = templateSelect.getValue();
    final Template selectedTemplate = readTemplate(templateSelected);
    config.setTemplate(selectedTemplate);

    // determine screen size
    final String windowName = selectedTemplate.getWindowName();
    final boolean hasWindowName = windowName != null && !windowName.isBlank();
    if (hasWindowName) {
      try {
        Rectangle2D rect2d = new JnaScreenIdentifier().determineScreenSize(selectedTemplate.getWindowName());
        config.setDspBounds(rect2d);
      } catch (Exception e) {
        logger.error("Error while finding window, please manually determine points", e);
        determineWindowSize();
      }
    } else {
      // manually determine window size, add prompt here
      determineWindowSize();
    }

    if(!validateBounds()) {
      logger.error("Invalid bounds");
      return; // do not start
    }

    // save selected input type and template
    logger.info("Saving current config as default");
    PersistUtil.setDefaults(templateSelected, selected);

    final OverlayController controller = new OverlayController(config);
    controller.start();

    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setAlwaysOnTop(false);
    currentStage.hide();
  }

  private Template readTemplate(String templateSelected) {
    Gson gson = GsonUtil.getInstance();
    try(final BufferedReader br = new BufferedReader(new FileReader(TEMPLATES + "/" + templateSelected))) {
      Template template = gson.fromJson(br, Template.class);
      logger.info("Loaded {}", template.getId());

      templateSelect.getStyleClass().remove("step-error");
      // add actual validation here
      return template;
    } catch (Exception e) {
      templateSelect.getStyleClass().add("step-error");
      logger.error("Error reading template {}", templateSelected, e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Validate before starting
   */
  private boolean validateBounds() {
    boolean noErrors = true;
    if(config.getDspBounds() == null) {
      noErrors = false;
      logger.error("No dsp bounds");
    }

    return noErrors;
  }

  /**
   * Show the input config screen
   */
  @FXML
  protected void configureInputType() throws IOException {
    logger.info("Input configure selected");
    final String selected = inputTypeSelect.getValue();
    config.setInput(selected, initInputSelect(selected));

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
   *
   * @return initialized input
   */
  private InputListener initInputSelect(final String selected) {
    if(selected.equals(KeyboardInputListener.NAME)) {
      // KB is always initialized
      logger.info("Keyboard input selected");
      return initializedInputs.get(selected);
    }

    InputListener inputListener = initializedInputs.get(selected);
    if(inputListener != null) {
      logger.info("Reuse initialized input listener");
      return inputListener;
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
      return newInput;
    }

    return null;
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
  protected void determineWindowSize() {
    logger.info("Unable to detect app, please manually determine dimensions");
    config.setDspBounds(new ManualScreenIdentifier().determineScreenSize(""));
  }

  @FXML
  protected void windowDragged(MouseEvent event) {
    Stage stage = (Stage) templateSelect.getScene().getWindow();
    stage.setX(event.getScreenX() - xOffset);
    stage.setY(event.getScreenY() - yOffset);
  }

  @FXML
  protected void windowPressed(MouseEvent event) {
    xOffset = event.getSceneX();
    yOffset = event.getSceneY();
  }
}