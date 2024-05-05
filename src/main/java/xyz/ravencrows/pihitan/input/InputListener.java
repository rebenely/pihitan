package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

public interface InputListener {
  void start(Scene scene, ScreenNavigator navigator, InputConfigSettings settings);
}
