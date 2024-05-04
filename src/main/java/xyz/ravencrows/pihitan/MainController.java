package xyz.ravencrows.pihitan;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import xyz.ravencrows.pihitan.userconfig.InputConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;
import xyz.ravencrows.pihitan.userconfig.InputType;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;

public class MainController {
    protected double vstAppX;
    protected double vstAppY;

    private int step;

    private InputConfigSettings inputConfigSettings;

    @FXML
    protected ChoiceBox<String> archetypeSelect;
    @FXML
    protected ChoiceBox<String> inputTypeSelect;

    @FXML
    public void initialize() {
        archetypeSelect.getItems().removeAll(archetypeSelect.getItems());
        archetypeSelect.getItems().addAll("Plini X");

        inputTypeSelect.getItems().removeAll(inputTypeSelect.getItems());
        inputTypeSelect.getItems().addAll("Keyboard", "Pihitan Pedal");
        inputTypeSelect.getSelectionModel().select("Keyboard");
    }

    @FXML
    protected void startOverlay() {
        Stage stage = new Stage();
        HBox root = new HBox();
        Scene scene = new Scene(root);
        Label label = new Label("Is it working");

        label.setStyle("-fx-text-fill: #F1F6F9");
        label.setFont(new Font(24));

        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
        root.getChildren().add(label);

        scene.setFill(Color.TRANSPARENT);
        scene.setCursor(Cursor.CROSSHAIR);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Pihitan overlay");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (KeyCode.A.equals(keyEvent.getCode())) {
                Robot robot = new Robot();
                robot.mouseMove(vstAppX, vstAppY);
                robot.mouseWheel(1);
            } else if (KeyCode.D.equals(keyEvent.getCode())) {
                Robot robot = new Robot();
                robot.mouseMove(vstAppX, vstAppY);
                robot.mouseWheel(-1);
            }
        });
        Stage currentStage = (Stage)inputTypeSelect.getScene().getWindow();
        currentStage.setAlwaysOnTop(false);
        currentStage.hide();

        stage.show();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((screenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((screenBounds.getHeight() - stage.getHeight()) / 2);
    }

    @FXML
    protected void configureInputType() throws IOException {
        final InputType selectedType = InputType.of(inputTypeSelect.getSelectionModel().getSelectedItem());
        FXMLLoader loader = new FXMLLoader(getClass().getResource(selectedType.getFxml()));
        Scene scene = ScreenUtil.setupTranparentScreen(loader);
        // TODO instead of passing this value to the config controller
        // pass all existing configs then pass it back
        InputConfigController configController = loader.getController();
        configController.initData(configData -> {
            this.inputConfigSettings = configData;
        });

        // Get the current stage
        Stage currentStage = (Stage)inputTypeSelect.getScene().getWindow();
        currentStage.setScene(scene);
    }

    @FXML
    protected void exit(ActionEvent event) {
        ((Stage)(((Button)event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    protected void determineWindowSize(ActionEvent event) {
        Stage stage = new Stage();
        HBox root = new HBox();
        Scene scene = new Scene(root);
        Label label = new Label("Click on the upper left corner of your archetype window");

        label.setStyle("-fx-text-fill: #F1F6F9");
        label.setFont(new Font(24));

        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5)");
        root.getChildren().add(label);

        scene.setFill(Color.TRANSPARENT);
        scene.setCursor(Cursor.CROSSHAIR);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        stage.setTitle("Specify window size");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setAlwaysOnTop(true);

        this.step = 0;
        scene.setOnMouseClicked(mouseEvent -> {
            if(this.step == 0) {
                double screenX = mouseEvent.getX();
                System.out.println(screenX);
                this.vstAppX = screenX;
                label.setText("Click on the lower right corner of your archetype window");
            } else if (this.step == 1) {
                double screenY = mouseEvent.getY();
                System.out.println(screenY);
                this.vstAppY = screenY;
                label.setText("Click anywhere to continue");
            } else {
                stage.close();
            }
            this.step++;
        });

        stage.show();
    }
}