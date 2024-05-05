package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.robot.Robot;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

public class KeyboardInputListener implements InputListener {
  @Override
  public void start(Scene scene, ScreenNavigator navigator, InputConfigSettings settings) {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      final String keyName = keyEvent.getCode().getName().toUpperCase();

      if (keyName.equals(settings.navLeft())) {
        navigator.moveToPreviousSection();
      } else if (keyName.equals(settings.navRight())) {
        navigator.moveToNextSection();
      } else if (keyName.equals(settings.press())) {
        navigator.press(scene);
      } else if (keyName.equals(settings.navItemLeft())) {
        navigator.moveToPreviousItem();
      } else if (keyName.equals(settings.navItemRight())) {
        navigator.moveToNextItem();
      } else if (keyName.equals(settings.turnLeft())) {
        navigator.turnKnobLeft();;
      } else if (keyName.equals(settings.turnRight())) {
        navigator.turnKnobRight();
      }
    });
  }
}
