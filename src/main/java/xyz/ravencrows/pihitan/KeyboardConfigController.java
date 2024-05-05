package xyz.ravencrows.pihitan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

import java.util.List;
import java.util.Optional;

public class KeyboardConfigController implements ConfigController {
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

  private List<Label> buttons;

  private Scene parent;

  private Label selectedBtn = null;

  private final PihitanConfig config = PihitanConfig.getInstance();


  @Override
  public void initController(Scene parent) {
    this.parent = parent;
  }

  @FXML
  public void initialize() {
    InputConfigSettings keys = config.getInputSettings();

    // add key listener
    mainBody.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      final boolean isListening = selectedBtn != null;
      if(!isListening) {
        return;
      }

      KeyCode code = keyEvent.getCode();
      final boolean isBackspace = code == KeyCode.BACK_SPACE;
      final boolean isAlphanumeric = code.isLetterKey() || code.isDigitKey();
      final boolean isSpace = code == KeyCode.SPACE;
      if (!isAlphanumeric && !isBackspace && !isSpace) {
        keyEvent.consume();
        return;
      }

      final String newCode;
      if (isAlphanumeric) {
        newCode = keyEvent.getText().toUpperCase();
      } else if (isBackspace) {
        // remove
        newCode = "";
      } else {
        newCode = "<SPACE>";
      }

      selectedBtn.getStyleClass().remove("listening");
      selectedBtn.setText(newCode);

      if(isBackspace) {
        return;
      }

      // Remove existing similar code
      Optional<Label> similarCode = buttons
              .stream()
              .filter(existingBrn -> !selectedBtn.equals(existingBrn) && newCode.equals(existingBrn.getText().toUpperCase()))
              .findFirst();
      similarCode.ifPresent(button -> button.setText(""));

      // remove listening
      selectedBtn = null;
    });

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

  public void listenToKeyBtn(MouseEvent event) {
    if (selectedBtn != null) {
      return;
    }
    Label btn = ((Label) event.getSource());
    btn.getStyleClass().add("listening");
    btn.requestFocus();
    selectedBtn = btn;
  }
}
