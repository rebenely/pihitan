package xyz.ravencrows.pihitan.userconfig;

public enum InputType {
  KEYBOARD("Keyboard", "keyboard-config.fxml"),
  PIHITAN("Pihitan Pedal", "keyboard-config.fxml");
  final private String code;
  final private String fxml;
  InputType(String code, String fxml) {
    this.code = code;
    this.fxml = fxml;
  }

  public static InputType of(String inputCode) {
    for(InputType inputTypes : InputType.values()) {
      if(inputTypes.code.equals(inputCode)) {
        return inputTypes;
      }
    }

    throw new RuntimeException("No input type found");
  }

  public String getFxml() {
    return fxml;
  }

  public String getCode() {
    return code;
  }
}
