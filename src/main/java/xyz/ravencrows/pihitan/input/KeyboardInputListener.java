package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class KeyboardInputListener implements InputListener {
  public static final String BACKSPACE = "BACKSPACE";
  public static final String SPACE = "SPACE";
  private List<InputConfigSettings> actions;
  public KeyboardInputListener() {
    actions = new ArrayList<>();
  }

  @Override
  public void start(Scene scene, ScreenNavigator navigator) {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      final String keyName = keyEvent.getCode().getName().toUpperCase();
      // TODO create map from actions
//      if(actions.containsKey(keyName)) {
//        navigator.navigate(actions.get(keyName), scene);
//      }
    });
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
  public void listen(Pane pane, Consumer<InputCode> inputConsumer) {
    pane.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      final KeyCode code = keyEvent.getCode();
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
        newCode = BACKSPACE;
      } else {
        newCode = SPACE;
      }

      inputConsumer.accept(new InputCode(newCode, isBackspace || isSpace));
    });
  }

}
