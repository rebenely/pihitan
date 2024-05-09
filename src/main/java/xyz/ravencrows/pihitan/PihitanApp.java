package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;

public class PihitanApp extends Application {
  @Override
  public void start(Stage stage) throws IOException {
    PihitanConfig config = PihitanConfig.getInstance();
    config.setManager(new SDL2ControllerManager());

    FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
    ScreenUtil.setupScreen(stage, fxmlLoader, "Pihitan");
    stage.show();
  }


  public static void main(String[] args) {
    launch();
  }
}