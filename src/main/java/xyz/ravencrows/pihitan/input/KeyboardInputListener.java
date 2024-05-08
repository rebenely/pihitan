package xyz.ravencrows.pihitan.input;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class KeyboardInputListener implements InputListener {
  public static final String BACKSPACE = "BACKSPACE";
  public static final String SPACE = "SPACE";

  public static final String NAME = "Keyboard";

  private final List<InputConfigSettings> actions;

  public KeyboardInputListener() {
    this.actions = defaults();
  }

  /**
   * Find a better way to do this.
   * Also, consider having a config.json where this can be read and saved
   */
  public static List<InputConfigSettings> defaults() {
    final List<InputConfigSettings> defaults = new ArrayList<>();
    defaults.add(new InputConfigSettings(PihitanAction.PREV_SECTION, "7"));
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_SECTION, "9"));
    defaults.add(new InputConfigSettings(PihitanAction.KNOB_LEFT, "4"));
    defaults.add(new InputConfigSettings(PihitanAction.KNOB_RIGHT, "6"));
    defaults.add(new InputConfigSettings(PihitanAction.PRESS, "5"));
    defaults.add(new InputConfigSettings(PihitanAction.PREV_ITEM, "1"));
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_ITEM, "3"));
    defaults.add(new InputConfigSettings(PihitanAction.PREV_PRESET, "J"));
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_PRESET, "K"));

    return defaults;
  }

  @Override
  public void setKeys(List<InputConfigSettings> actions) {
    this.actions.clear();
    this.actions.addAll(actions);
  }

  @Override
  public List<InputConfigSettings> getKeys() {
    return actions;
  }

  @Override
  public void listenToRoot(Parent root, Consumer<InputCode> inputConsumer) {
    root.addEventFilter(KeyEvent.KEY_PRESSED,
            keyEvent -> inputConsumer.accept(getInputCode(keyEvent)));
  }

  @Override
  public void listenToSceneAction(Scene scene, Consumer<PihitanAction> actionConsumer) {
    Map<String, PihitanAction> actionsMapped =
            actions
            .stream()
            .collect(Collectors.toMap(InputConfigSettings::getInputCode, InputConfigSettings::getAction));

    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      final InputCode inputCode = getInputCode(keyEvent);
      if(inputCode == null) {
        return;
      }
      PihitanAction action = actionsMapped.get(inputCode.code());
      if(action == null) {
        return;
      }
      actionConsumer.accept(action);
    });
  }

  @Override
  public void stopListener() {
    // do nothing
  }

  /**
   * Create input code
   */
  private InputCode getInputCode(KeyEvent keyEvent) {
    final KeyCode code = keyEvent.getCode();
    final boolean isBackspace = code == KeyCode.BACK_SPACE;
    final boolean isAlphanumeric = code.isLetterKey() || code.isDigitKey();
    final boolean isSpace = code == KeyCode.SPACE;
    if (!isAlphanumeric && !isBackspace && !isSpace) {
      keyEvent.consume();
      return null;
    }
    final String newCode;
    if (isAlphanumeric) {
      newCode = keyEvent.getText().toUpperCase();
    } else if (isBackspace) {
      // remove
      newCode = BACKSPACE;
    } else {
      newCode = SPACE;
    }

    return new InputCode(newCode, isBackspace || isSpace);
  }

}
