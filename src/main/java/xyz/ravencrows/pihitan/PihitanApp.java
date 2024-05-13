package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

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
    Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        logger.error(
                "Exception in thread \"" + t.getName() + "\"", e);
      }
    });
    super.init();
  }

  public static void main(String[] args) {
    launch();
  }
}