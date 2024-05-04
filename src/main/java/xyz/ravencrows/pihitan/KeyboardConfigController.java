package xyz.ravencrows.pihitan;

import xyz.ravencrows.pihitan.userconfig.InputConfigController;
import xyz.ravencrows.pihitan.userconfig.InputConfigSettings;

import java.util.function.Consumer;

public class KeyboardConfigController implements InputConfigController {
  private Consumer<InputConfigSettings> onComplete;
  @Override
  public void initData(Consumer<InputConfigSettings> onComplete) {
    this.onComplete = onComplete;
  }
}
