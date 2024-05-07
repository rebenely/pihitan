package xyz.ravencrows.pihitan.input;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.List;
import java.util.function.Consumer;

public interface InputListener {
  void start(Scene scene, ScreenNavigator navigator);

  void setKeys(List<InputConfigSettings> actions);

  List<InputConfigSettings> getKeys();

  void listen(Pane pane, Consumer<InputCode> inputConsumer);
}
