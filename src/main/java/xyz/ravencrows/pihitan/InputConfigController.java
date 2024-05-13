package xyz.ravencrows.pihitan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.input.PihitanAction;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for input config screen
 * TODO save and load config to a file
 */
public class InputConfigController implements ConfigController {
  private static final Logger logger = LoggerFactory.getLogger(InputConfigController.class);

  @FXML
  public GridPane mainBody;
  @FXML
  private Label turnLeftKey;
  @FXML
  private Label turnRightKey;
  @FXML
  private Label pressKey;
  @FXML
  private Label navRight;
  @FXML
  private Label navLeft;
  @FXML
  private Label navItemRight;
  @FXML
  private Label navItemLeft;
  @FXML
  private Label presetLeft;
  @FXML
  private Label presetRight;

  private Map<PihitanAction, Label> buttons;

  private Scene parent;

  private Label selectedBtn = null;

  private final PihitanConfig config = PihitanConfig.getInstance();

  // draggable screen
  private double xOffset;
  private double yOffset;

  @Override
  public void initController(Scene parent) {
    this.parent = parent;
  }

  @FXML
  public void initialize() {
    logger.info("Initializing config screen");
    buttons =  Map.of(
      PihitanAction.KNOB_LEFT, turnLeftKey,
      PihitanAction.KNOB_RIGHT, turnRightKey,
      PihitanAction.PRESS, pressKey,
      PihitanAction.PREV_SECTION, navLeft,
      PihitanAction.NEXT_SECTION, navRight,
      PihitanAction.PREV_ITEM, navItemLeft,
      PihitanAction.NEXT_ITEM, navItemRight,
      PihitanAction.PREV_PRESET, presetLeft,
      PihitanAction.NEXT_PRESET, presetRight
    );

    final InputListener listener = config.getInput();
    final List<InputConfigSettings> actions = listener.getKeys();
    listener.listenToRoot(mainBody, inputCode -> {
      final boolean isListening = selectedBtn != null;
      if(!isListening) {
        return;
      }

      // special case for backspace
      final boolean isBackspace = inputCode.isSpecialKey() && KeyboardInputListener.BACKSPACE.equals(inputCode.code());
      selectedBtn.getStyleClass().remove("listening");
      selectedBtn.setText(isBackspace ? "" : inputCode.code());
      if(isBackspace) {
        return;
      }

      // Remove existing similar code
      Optional<Label> similarCode = buttons
              .values()
              .stream()
              .filter(existingBtn -> !selectedBtn.equals(existingBtn) && (existingBtn.getText() != null && inputCode.code().equals(existingBtn.getText().toUpperCase())))
              .findFirst();
      similarCode.ifPresent(button -> button.setText(""));

      // remove listening
      selectedBtn = null;
    });

    // this is assumed to be a proper bimap :)
    Map<PihitanAction, String> actionsMapped =
            actions
            .stream()
            .collect(Collectors.toMap(InputConfigSettings::getAction, InputConfigSettings::getInputCode));

    // get values from existing config
    for (Map.Entry<PihitanAction, Label> entry : buttons.entrySet()) {
      final Label btn = entry.getValue();
      btn.setText(actionsMapped.get(entry.getKey()));
    }
  }

  public void save(ActionEvent event) {
    List<InputConfigSettings> newActions = new ArrayList<>();

    logger.info("Saving config");
    for (Map.Entry<PihitanAction, Label> entry : buttons.entrySet()) {
      final Label btn = entry.getValue();
      final String btnKey = btn.getText();
      if(btnKey != null) {
        logger.info(entry.getKey() + " = " + btnKey);
        newActions.add(new InputConfigSettings(entry.getKey(), btnKey));
      }
    }

    InputListener input = config.getInput();
    input.setKeys(newActions);
    exit(event);
  }


  public void exit(ActionEvent event) {
    logger.info("Exiting input config screen");
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

    // stop listener
    InputListener input = config.getInput();
    if(input != null) {
      input.stopListener();
    }

    stage.setScene(parent);
  }

  public void listenToKeyBtn(MouseEvent event) {
    if (selectedBtn != null) {
      return;
    }
    Label btn = ((Label) event.getSource());
    btn.getStyleClass().add("listening");
    btn.requestFocus();
    selectedBtn = btn;
  }

  @FXML
  protected void windowDragged(MouseEvent event) {
    Stage stage = (Stage) mainBody.getScene().getWindow();
    stage.setX(event.getScreenX() - xOffset);
    stage.setY(event.getScreenY() - yOffset);
  }

  @FXML
  protected void windowPressed(MouseEvent event) {
    xOffset = event.getSceneX();
    yOffset = event.getSceneY();
  }
}
