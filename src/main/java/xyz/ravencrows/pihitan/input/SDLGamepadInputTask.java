package xyz.ravencrows.pihitan.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import javafx.concurrent.Task;
import org.libsdl.SDL_Error;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;

import java.util.function.Consumer;

public class SDLGamepadInputTask<V> extends Task<V> {
  private final SDL2Controller controller;
  private final SDL2ControllerManager manager;
  private Consumer<InputCode> consumer;

  public SDLGamepadInputTask(SDL2Controller controller, SDL2ControllerManager manager) {
    this.controller = controller;
    this.manager = manager;
  }

  public void setConsumer(Consumer<InputCode> consumer) {
    this.consumer = consumer;
  }

  @Override
  protected V call() throws Exception {
    while (true) {
      try {
        Thread.sleep(50);
        manager.pollState();
      } catch (SDL_Error sdl_error) {
        sdl_error.printStackTrace();
        throw new RuntimeException(sdl_error);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      for(int i = 0; i < 10; i++) {
        if(controller.getButton(i)) {
          consumer.accept(new InputCode(String.valueOf(i), true));
        }
      }
    }

  }
}
