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
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.PihitanAction;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;

import java.util.*;
import java.util.stream.Collectors;

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

  private Map<PihitanAction, Label> buttons;

  private Scene parent;

  private Label selectedBtn = null;

  private final PihitanConfig config = PihitanConfig.getInstance();

  @Override
  public void initController(Scene parent) {
    this.parent = parent;
  }

  @FXML
  public void initialize() {
    System.out.println("Run init");
    buttons = Map.of(
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

    InputListener listener = config.getInput();
    List<InputConfigSettings> actions = listener.getKeys();

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
        newCode = "SPACE";
      }

      selectedBtn.getStyleClass().remove("listening");
      selectedBtn.setText(newCode);

      if(isBackspace) {
        return;
      }

      // Remove existing similar code
      Optional<Label> similarCode = buttons
              .values()
              .stream()
              .filter(existingBrn -> !selectedBtn.equals(existingBrn) && (existingBrn.getText() != null && newCode.equals(existingBrn.getText().toUpperCase())))
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
//    turnLeftKey.setText(actionsMapped.get(PihitanAction.KNOB_LEFT));
//    turnRightKey.setText(actionsMapped.get(PihitanAction.KNOB_RIGHT));
//    pressKey.setText(actionsMapped.get(PihitanAction.PRESS));
//    navLeft.setText(actionsMapped.get(PihitanAction.PREV_SECTION));
//    navRight.setText(actionsMapped.get(PihitanAction.NEXT_SECTION));
//    navItemLeft.setText(actionsMapped.get(PihitanAction.PREV_ITEM));
//    navItemRight.setText(actionsMapped.get(PihitanAction.NEXT_ITEM));
//    presetLeft.setText(actionsMapped.get(PihitanAction.PREV_PRESET));
//    presetRight.setText(actionsMapped.get(PihitanAction.NEXT_PRESET));
  }

  public void save(ActionEvent event) {
    List<InputConfigSettings> newActions = new ArrayList<>();

    for (Map.Entry<PihitanAction, Label> entry : buttons.entrySet()) {
      final Label btn = entry.getValue();
      final String btnKey = btn.getText();
      if(btnKey != null) {
        newActions.add(new InputConfigSettings(entry.getKey(), btnKey));
      }
    }

    config.getInput().setKeys(newActions);

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
