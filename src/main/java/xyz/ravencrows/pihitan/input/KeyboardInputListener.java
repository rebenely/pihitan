package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyboardInputListener implements InputListener {
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

}
