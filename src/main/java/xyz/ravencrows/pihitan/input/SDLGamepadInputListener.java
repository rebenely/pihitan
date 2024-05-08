package xyz.ravencrows.pihitan.input;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.libsdl.SDL_Error;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Input listener for gamepad inputs
 * Uses sdl2gdx
 */
public class SDLGamepadInputListener implements InputListener {
  private final List<InputConfigSettings> actions;
  private Thread inputThread;
  private final SDL2Controller controller;
  private final SDL2ControllerManager manager;
  private volatile boolean isRunning;
  private volatile long lastPressMillis = 0;
  private static final long DEBOUNCE_DURATION = 200;

  public SDLGamepadInputListener(ArrayList<InputConfigSettings> actions, SDL2Controller controller, SDL2ControllerManager manager) {
    this.actions = actions;
    this.manager = manager;
    this.controller = controller;
    this.isRunning = false;
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
    if(isRunning) {
      System.out.println("Thread is already running!");
      return;
    }
    this.isRunning = true;
    inputThread = createInputCodeThread(inputConsumer);

    inputThread.setDaemon(true);
    inputThread.start();
  }

  @Override
  public void listenToSceneAction(Scene scene, Consumer<PihitanAction> actionConsumer) {
    if(isRunning) {
      System.out.println("Thread is already running!");
      return;
    }

    final Map<String, PihitanAction> actionsMapped =
            actions
            .stream()
            .collect(Collectors.toMap(InputConfigSettings::getInputCode, InputConfigSettings::getAction));

    this.isRunning = true;
    inputThread = createInputCodeThread(inputCode -> {
      if(inputCode == null) {
        return;
      }
      PihitanAction action = actionsMapped.get(inputCode.code());
      if(action == null) {
        return;
      }
      actionConsumer.accept(action);
    });


    inputThread.setDaemon(true);
    inputThread.start();
  }

  @Override
  public void stopListener() {
    isRunning = false;
  }

  /**
   * Create thread for listening
   */
  private Thread createInputCodeThread(Consumer<InputCode> inputConsumer) {
    return  new Thread(() -> {
      while(isRunning) {
        try {
          manager.pollState();
          Thread.sleep(50);
          for(int i = 0; i < 20; i++) {
            // debounce for a bit
            if(controller.getButton(i) && System.currentTimeMillis() - lastPressMillis >= DEBOUNCE_DURATION) {
              lastPressMillis = System.currentTimeMillis();
              final int button = i;
              Platform.runLater(() -> {
                inputConsumer.accept(new InputCode(String.valueOf(button), true));
              });
            }
          }
        } catch (SDL_Error e) {
          throw new RuntimeException(e);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
