package xyz.ravencrows.pihitan;

import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.Vector3;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import org.libsdl.SDL_Error;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class PihitanApp extends Application {
  @Override
  public void start(Stage stage) throws IOException, URISyntaxException {
    FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
    ScreenUtil.setupTranparentScreen(stage, fxmlLoader, "Pihitan");
    stage.setAlwaysOnTop(false);

    PihitanConfig config = PihitanConfig.getInstance();
    config.setInput(new KeyboardInputListener(new ArrayList<>())); // TODO initialize this


    SDL2ControllerManager manager = new SDL2ControllerManager();
    SDL2Controller controller = (SDL2Controller) manager.getControllers().get(0);
    while (true) {
      try {
        Thread.sleep(30);
        manager.pollState();
      } catch (SDL_Error sdl_error) {
        sdl_error.printStackTrace();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      for (int i = 0; i < manager.getControllers().size; i++) {
        Controller controllerAtIndex = manager.getControllers().get(i);
        System.out.println(controllerAtIndex.getButton(0));
      }
    }
  }


  public static void main(String[] args) {
    launch();
  }
}