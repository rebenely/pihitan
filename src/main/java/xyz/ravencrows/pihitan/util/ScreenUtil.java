package xyz.ravencrows.pihitan.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.ravencrows.pihitan.PihitanApp;

import java.io.IOException;
import java.util.Objects;

public class ScreenUtil {
  public static final int WIDTH = 480;
  public static final int HEIGHT = 540;

  public static Scene setupScreen(Stage stage, FXMLLoader loader, String windowTitle) throws IOException {
    Scene scene = setupScreen(loader);

    stage.setTitle(windowTitle);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);
    stage.setResizable(false);

    return scene;
  }

  public static Scene setupScreen(FXMLLoader loader) throws IOException {
    Scene scene = new Scene(loader.load(), WIDTH, HEIGHT);
    scene.getStylesheets().add(Objects.requireNonNull(PihitanApp.class.getResource("pihitan.css")).toExternalForm());
    scene.setFill(Color.TRANSPARENT);

    return scene;
  }
}
