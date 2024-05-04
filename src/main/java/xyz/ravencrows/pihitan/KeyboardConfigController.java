package xyz.ravencrows.pihitan;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class KeyboardConfigController implements ConfigController {
  @FXML
  private Button turnLeftKey;
  @FXML
  private Button turnRightKey;
  @FXML
  private Button pressKey;
  @FXML
  private Button navRight;
  @FXML
  private Button navLeft;
  @FXML
  private Button navItemRight;
  @FXML
  private Button navItemLeft;
  @FXML
  private Button presetLeft;
  @FXML
  private Button presetRight;

  private List<Button> buttons;

  private Scene parent;


  private boolean isListening = false;

  private final PihitanConfig config = PihitanConfig.getInstance();


  @Override
  public void initController(Scene parent) {
    this.parent = parent;
  }

  @FXML
  public void initialize() {
    InputConfigSettings keys = config.getInputSettings();

    // get values from existing config
    turnLeftKey.setText(keys.turnLeft());
    turnRightKey.setText(keys.turnRight());
    pressKey.setText(keys.press());
    navLeft.setText(keys.navLeft());
    navRight.setText(keys.navRight());
    navItemLeft.setText(keys.navItemLeft());
    navItemRight.setText(keys.navItemRight());
    presetLeft.setText(keys.prevPreset());
    presetRight.setText(keys.nextPreset());

    buttons = List.of(
            turnLeftKey,
            turnRightKey,
            pressKey,
            navLeft,
            navRight,
            navItemLeft,
            navItemRight,
            presetLeft,
            presetRight);
  }

  public void save(ActionEvent event) {
    config.setInputSettings(
      new InputConfigSettings(
        turnLeftKey.getText(),
        turnRightKey.getText(),
        pressKey.getText(),
        navLeft.getText(),
        navRight.getText(),
        navItemLeft.getText(),
        navItemRight.getText(),
        presetLeft.getText(),
        presetRight.getText()
      )
    );

    exit(event);
  }

  public void exit(ActionEvent event) {
    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    stage.setScene(parent);
  }

  public void listenToKeyBtn(ActionEvent event) {
    if (isListening) {
      return;
    }
    Button btn = ((Button) event.getSource());
    btn.getStyleClass().add("listening");
    isListening = true;
    final EventHandler<KeyEvent> selectKeyEvent = new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent keyEvent) {
        final boolean isBackspace = keyEvent.getCode() == KeyCode.BACK_SPACE;
        final boolean isAlphanumeric = keyEvent.getCode().isLetterKey() || keyEvent.getCode().isDigitKey();
        if (!isAlphanumeric && !isBackspace) {
          keyEvent.consume();
          return;
        }

        final String newCode = keyEvent.getText().toUpperCase();

        btn.getStyleClass().remove("listening");
        isListening = false;
        btn.removeEventHandler(KeyEvent.KEY_PRESSED, this);
        btn.setText(isBackspace ? "" : newCode);

        if(isBackspace) {
          return;
        }

        // Remove existing similar code
        Optional<Button> similarCode = buttons
                .stream()
                .filter(existingBrn -> !btn.equals(existingBrn) && newCode.equals(existingBrn.getText().toUpperCase()))
                .findFirst();
        similarCode.ifPresent(button -> button.setText(""));
      }
    };
    btn.addEventFilter(KeyEvent.KEY_PRESSED, selectKeyEvent);
  }
}
