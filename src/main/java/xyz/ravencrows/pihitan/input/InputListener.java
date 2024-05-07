package xyz.ravencrows.pihitan.input;

import javafx.scene.Parent;
import javafx.scene.Scene;
import xyz.ravencrows.pihitan.navigator.ScreenNavigator;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.List;
import java.util.function.Consumer;

public interface InputListener {

  void setKeys(List<InputConfigSettings> actions);

  List<InputConfigSettings> getKeys();

  /**
   * Listens to all key press
   * @param parent
   * @param inputConsumer
   */
  void listenToRoot(Parent parent, Consumer<InputCode> inputConsumer);

  /**
   * listens to action events in scene
   * uses scene since overlay doesnt work when using parent only
   * but scene is not defined yet in fxml initialize
   *
   * @param scene
   * @param actionConsumer
   */
  void listenToSceneAction(Scene scene, Consumer<PihitanAction> actionConsumer);
}
