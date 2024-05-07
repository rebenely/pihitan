package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import org.controlsfx.control.action.Action;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.List;
import java.util.Map;

public interface InputListener {
  void start(Scene scene, ScreenNavigator navigator);

  void setKeys(List<InputConfigSettings> actions);

  List<InputConfigSettings> getKeys();
}
