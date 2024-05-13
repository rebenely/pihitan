package xyz.ravencrows.pihitan.userconfig;

import java.util.List;

public class PersistedConfig {
  private List<PersistedInput> inputs;

  public List<PersistedInput> getInputs() {
    return inputs;
  }

  public void setInputs(List<PersistedInput> inputs) {
    this.inputs = inputs;
  }
}
