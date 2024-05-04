package xyz.ravencrows.pihitan.userconfig;

import java.util.function.Consumer;

public interface InputConfigController {
  public void initData(Consumer<InputConfigSettings> onComplete);
}
