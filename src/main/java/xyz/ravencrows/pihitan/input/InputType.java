package xyz.ravencrows.pihitan.input;

public class InputType {
  final private String code;
  final private String label;
  public InputType(String code, String label) {
    this.code = code;
    this.label = label;
  }

  public String getCode() {
    return code;
  }

  public String getLabel() {
    return label;
  }
}
