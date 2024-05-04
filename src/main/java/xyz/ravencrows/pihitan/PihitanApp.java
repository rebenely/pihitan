package xyz.ravencrows.pihitan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;
import java.util.Objects;

public class PihitanApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(PihitanApp.class.getResource("main.fxml"));
        ScreenUtil.setupTranparentScreen(stage, fxmlLoader, "Pihitan");
        stage.setAlwaysOnTop(true);

        PihitanConfig config = PihitanConfig.getInstance();
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