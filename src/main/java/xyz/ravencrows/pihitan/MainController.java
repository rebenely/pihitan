package xyz.ravencrows.pihitan;

import com.badlogic.gdx.controllers.Controller;
import com.google.gson.Gson;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import uk.co.electronstudio.sdl2gdx.SDL2Controller;
import uk.co.electronstudio.sdl2gdx.SDL2ControllerManager;
import xyz.ravencrows.pihitan.input.KeyboardInputListener;
import xyz.ravencrows.pihitan.input.SDLGamepadInputListener;
import xyz.ravencrows.pihitan.templates.Template;
import xyz.ravencrows.pihitan.userconfig.ConfigController;
import xyz.ravencrows.pihitan.userconfig.PihitanConfig;
import xyz.ravencrows.pihitan.util.ScreenUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
  private Pair<Double, Double> upperLeft;
  private Pair<Double, Double> lowerRight;

  private int step;

  private final PihitanConfig config = PihitanConfig.getInstance();

  @FXML
  protected Button templateSelectBtn;
  @FXML
  protected ChoiceBox<String> inputTypeSelect;

  @FXML
  public void initialize() {
    // get plugged in controllers
    List<String> inputOptions = new ArrayList<>();
    inputOptions.add(KeyboardInputListener.NAME);
    for(Controller controller : config.getManager().getControllers()){
      inputOptions.add(controller.getName());
    }

    inputTypeSelect.getItems().removeAll(inputTypeSelect.getItems());
    inputTypeSelect.getItems().addAll(inputOptions);
    inputTypeSelect.getSelectionModel().select(KeyboardInputListener.NAME);
  }

  @FXML
  protected void startOverlay() {
    final OverlayController controller = new OverlayController(config);
    controller.start();

    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setAlwaysOnTop(false);
    currentStage.hide();
  }

  @FXML
  protected void configureInputType() throws IOException {
    // setup selected config first so upon load of InputConfigController, config is properly updated
    final String selected = inputTypeSelect.getValue();
    if (KeyboardInputListener.NAME.equals(selected)) {
      config.setInput(new KeyboardInputListener(new ArrayList<>()));
    } else {
      SDL2ControllerManager manager = config.getManager();
      for(Controller controller : manager.getControllers()){
        if(!selected.equals(controller.getName())) {
          continue;
        }
        config.setInput(new SDLGamepadInputListener(new ArrayList<>(), (SDL2Controller)controller, manager)); // initialize actions
        System.out.println("found");
      }
    }

    // Get the current stage
    FXMLLoader loader = new FXMLLoader(getClass().getResource("input-config.fxml"));
    Scene scene = ScreenUtil.setupScreen(loader);

    // Pass parent scene so we can go back
    ConfigController configController = loader.getController();
    configController.initController(inputTypeSelect.getScene());

    Stage currentStage = (Stage) inputTypeSelect.getScene().getWindow();
    currentStage.setScene(scene);
  }

  @FXML
  protected void exit(ActionEvent event) {
    ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
  }

  @FXML
  protected void determineWindowSize() {
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
    stage.setResizable(true);
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
  protected void selectTemplate() throws IOException {
    final FileChooser fileChooser = new FileChooser();
    final Stage stage = (Stage) templateSelectBtn.getScene().getWindow();
    fileChooser.setInitialDirectory(new File("."));
    fileChooser.getExtensionFilters().addAll(
      new FileChooser.ExtensionFilter("Pihitan Templates", "*.json")
    );

    final File selectedFile = fileChooser.showOpenDialog(stage);
    if(selectedFile == null) {
      System.out.println("No file selected");
      return;
    }
    Gson gson = new Gson();

    try(final BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
      Template template = gson.fromJson(br, Template.class);
      templateSelectBtn.setText(template.getName());

      config.setTemplate(template);
    }

  }
}