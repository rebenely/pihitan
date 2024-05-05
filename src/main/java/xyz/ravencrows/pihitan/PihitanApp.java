package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PihitanApp extends Application {
  @Override
  public void start(Stage stage) throws IOException, URISyntaxException {
    FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
    ScreenUtil.setupTranparentScreen(stage, fxmlLoader, "Pihitan");
    stage.setAlwaysOnTop(true);

    PihitanConfig config = PihitanConfig.getInstance();

    // default input settings
    config.setInputSettings(
            new InputConfigSettings(
                    "A",
                    "D",
                    "S",
                    "Q",
                    "E",
                    "Z",
                    "X",
                    "G",
                    "H"));

    stage.show();
  }


  public static void main(String[] args) {
    launch();
  }
}