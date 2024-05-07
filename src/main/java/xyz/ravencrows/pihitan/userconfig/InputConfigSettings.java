package xyz.ravencrows.pihitan.userconfig;

import xyz.ravencrows.pihitan.input.PihitanAction;

public class InputConfigSettings {
  private PihitanAction action;
  private String inputCode;

  public InputConfigSettings(PihitanAction action, String inputCode) {
    this.action = action;
    this.inputCode = inputCode;
  }

  public PihitanAction getAction() {
    return action;
  }

  public void setAction(PihitanAction action) {
    this.action = action;
  }

  public String getInputCode() {
    return inputCode;
  }

  public void setInputCode(String inputCode) {
    this.inputCode = inputCode;
  }
}
