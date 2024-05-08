package xyz.ravencrows.pihitan.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import javafx.scene.Parent;
import javafx.scene.Scene;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.List;
import java.util.function.Consumer;

public class SDLGamepadInputListener implements InputListener {
  private final List<InputConfigSettings> actions;
  private final Controller controller;

  public SDLGamepadInputListener(List<InputConfigSettings> actions, Controller controller) {
    this.actions = actions;
    this.controller = controller;
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
  public void listenToRoot(Parent parent, Consumer<InputCode> inputConsumer) {
    controller.addListener(new ControllerAdapter() {
      @Override
      public boolean buttonDown(Controller controller, int buttonIndex) {
        System.out.println(buttonIndex);
        return true;
      }
    });
  }

  @Override
  public void listenToSceneAction(Scene scene, Consumer<PihitanAction> actionConsumer) {

  }
}
