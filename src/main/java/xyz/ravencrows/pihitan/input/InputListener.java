package xyz.ravencrows.pihitan.input;

import javafx.scene.Parent;
import javafx.scene.Scene;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.List;
import java.util.function.Consumer;

public interface InputListener {

  void setKeys(List<InputConfigSettings> actions);

  List<InputConfigSettings> getKeys();

  /**
   * Listens to all key press
   */
  void listenToRoot(Parent parent, Consumer<InputCode> inputConsumer);

  /**
   * listens to action events in scene
   * uses scene since overlay doesnt work when using parent only
   * but scene is not defined yet in fxml initialize
   */
  void listenToSceneAction(Scene scene, Consumer<PihitanAction> actionConsumer);

  /**
   * Stops listening
   */
  void stopListener();
}
