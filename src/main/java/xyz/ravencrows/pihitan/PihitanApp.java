package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;

public class PihitanApp extends Application {
  private static final Logger logger = LoggerFactory.getLogger(PihitanApp.class);

  @Override
  public void start(Stage stage) throws IOException {
    PihitanConfig config = PihitanConfig.getInstance();
    config.setManager(new SDL2ControllerManager());

    FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
    ScreenUtil.setupScreen(stage, fxmlLoader, "Pihitan");
    stage.show();

    logger.info("App start");
  }

  @Override
  public void init() throws Exception {
    // globally catch all exceptions to log it in logback
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      logger.error(
              "Exception in thread \"" + t.getName() + "\"", e);

      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setContentText(e.getLocalizedMessage());
      alert.show();
    });
    super.init();
  }

  public static void main(String[] args) {
    launch();
  }
}