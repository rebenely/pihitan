package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;
import java.net.URISyntaxException;

public class PihitanApp extends Application {
  @Override
  public void start(Stage stage) throws IOException, URISyntaxException {
    FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
    ScreenUtil.setupTranparentScreen(stage, fxmlLoader, "Pihitan");
    stage.setAlwaysOnTop(false);

    PihitanConfig config = PihitanConfig.getInstance();
    config.setInput(new KeyboardInputListener());

    stage.show();
  }


  public static void main(String[] args) {
    launch();
  }
}