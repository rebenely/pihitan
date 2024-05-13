package xyz.ravencrows.pihitan.input;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.libsdl.SDL_Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger logger = LoggerFactory.getLogger(SDLGamepadInputListener.class);

  private final List<InputConfigSettings> actions;
  private Thread inputThread;
  private final SDL2Controller controller;
  private final SDL2ControllerManager manager;
  private volatile boolean isRunning;
  private volatile long lastPressMillis = 0;
  private static final long DEBOUNCE_DURATION = 200;

  public SDLGamepadInputListener(SDL2Controller controller, SDL2ControllerManager manager) {
    this.actions = defaults();
    this.manager = manager;
    this.controller = controller;
    this.isRunning = false;
  }

  /**
   * Find a better way to do this.
   * Also, consider having a config.json where this can be read and saved
   */
  public static List<InputConfigSettings> defaults() {
    final List<InputConfigSettings> defaults = new ArrayList<>();
    defaults.add(new InputConfigSettings(PihitanAction.KNOB_LEFT, "2")); // X
    defaults.add(new InputConfigSettings(PihitanAction.KNOB_RIGHT, "1")); // B
    defaults.add(new InputConfigSettings(PihitanAction.PRESS, "0")); // A
    defaults.add(new InputConfigSettings(PihitanAction.PREV_SECTION, "9")); // LT
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_SECTION, "10")); // RT
    defaults.add(new InputConfigSettings(PihitanAction.PREV_ITEM, "13")); // DPAD_L
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_ITEM, "14")); // DPAD_R
    defaults.add(new InputConfigSettings(PihitanAction.PREV_PRESET, "4")); // SELECT / VIEW
    defaults.add(new InputConfigSettings(PihitanAction.NEXT_PRESET, "6")); // START / MENU

    return defaults;
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
      logger.warn("Thread is already running! Will skip this call.");
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
      logger.warn("Thread is already running! Will skip this call.");
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
          // no analog stick for now :(
          for(int i = 0; i < 20; i++) {
            // debounce for a bit
            if(controller.getButton(i) && System.currentTimeMillis() - lastPressMillis >= DEBOUNCE_DURATION) {
              lastPressMillis = System.currentTimeMillis();
              final int button = i;
              Platform.runLater(() -> inputConsumer.accept(new InputCode(String.valueOf(button), true)));
            }
          }
        } catch (SDL_Error | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
