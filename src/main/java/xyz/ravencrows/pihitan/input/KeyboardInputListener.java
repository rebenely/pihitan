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
  private final List<InputConfigSettings> actions;
  public KeyboardInputListener() {
    actions = new ArrayList<>();
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
    root.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      System.out.println("Yeah");
      inputConsumer.accept(getInputCode(keyEvent));
    });
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

  private InputCode getInputCode(KeyEvent keyEvent) {
    System.out.println("Yeah");
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
