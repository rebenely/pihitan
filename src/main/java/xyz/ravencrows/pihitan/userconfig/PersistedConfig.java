package xyz.ravencrows.pihitan.userconfig;

import java.util.ArrayList;
import java.util.List;

public class PersistedConfig {
  private String input;

  private String template;
  private List<PersistedInput> inputs = new ArrayList<>();

  public List<PersistedInput> getInputs() {
    return inputs;
  }

  public void setInputs(List<PersistedInput> inputs) {
    this.inputs = inputs;
  }

  public String getInput() {
    return input;
  }

  public void setInput(String input) {
    this.input = input;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }
}
