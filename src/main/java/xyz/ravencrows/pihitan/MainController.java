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
import javafx.util.Pair;
import javafx.util.StringConverter;
import xyz.ravencrows.pihitan.userconfig.*;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.IOException;

public class MainController {
  private Pair<Double, Double> upperLeft;
  private Pair<Double, Double> lowerRight;

  private int step;

  private InputConfigSettings inputConfigSettings;

  private final PihitanConfig config = PihitanConfig.getInstance();

  @FXML
  protected ChoiceBox<VstAppOption> vstSelect;
  @FXML
  protected ChoiceBox<String> inputTypeSelect;

  @FXML
  public void initialize() {
    vstSelect.getItems().removeAll(vstSelect.getItems());
    vstSelect.getItems().addAll(new VstAppOption("plini-x", "Plini X"));
    vstSelect.setConverter(new StringConverter<VstAppOption>() {
      @Override
      public String toString(VstAppOption vstAppOption) {
        // check why does this become null
        // vstSelect is initialized in fxml
        return vstAppOption == null ? null : vstAppOption.label();
      }

      @Override
      public VstAppOption fromString(String label) {
        return vstSelect.getItems().stream().filter(item -> item.label().equals(label)).findFirst().orElse(null);
      }
    });

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
    Rectangle2D bounds = config.getDspBounds();
    scene.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      if (KeyCode.A.equals(keyEvent.getCode())) {
        Robot robot = new Robot();
        robot.mouseMove(bounds.getMinX(), bounds.getMinY());
      } else if (KeyCode.D.equals(keyEvent.getCode())) {
        Robot robot = new Robot();
        robot.mouseMove(bounds.getMaxX(), bounds.getMaxY());
      }
    });
    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
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

    // Pass parent scene so we can go back
    ConfigController configController = loader.getController();
    configController.initController(inputTypeSelect.getScene());

    // Get the current stage
    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setScene(scene);
  }

  @FXML
  protected void exit(ActionEvent event) {
    ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
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
      if (this.step == 0) {
        upperLeft = new Pair<>(mouseEvent.getX(), mouseEvent.getY());
        label.setText("Click on the lower right corner of your archetype window");
      } else if (this.step == 1) {
        lowerRight = new Pair<>(mouseEvent.getX(), mouseEvent.getY());

        final double width = lowerRight.getKey() - upperLeft.getKey();
        final double height = lowerRight.getValue() - upperLeft.getValue();
        final boolean invalidDimension = width <= 0 || height <= 0;

        label.setText(invalidDimension ? "Invalid points, please resetup" : "Click anywhere to continue");
        if (!invalidDimension) {
          config.setDspBounds(new Rectangle2D(upperLeft.getKey(), upperLeft.getValue(), width, height));
        }
      } else {
        stage.close();
      }
      this.step++;
    });

    stage.show();
  }

  @FXML
  protected void selectVst(ActionEvent event) {
    System.out.println(vstSelect.getValue());
    config.setVstApp(vstSelect.getValue());
  }
}