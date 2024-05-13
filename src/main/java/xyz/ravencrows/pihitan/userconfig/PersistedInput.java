package xyz.ravencrows.pihitan.userconfig;

import java.util.List;

public class PersistedInput {
  private String name;
  private List<InputConfigSettings> actions;

  public PersistedInput(String name, List<InputConfigSettings> actions) {
    this.name = name;
    this.actions = actions;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<InputConfigSettings> getActions() {
    return actions;
  }

  public void setActions(List<InputConfigSettings> actions) {
    this.actions = actions;
  }

}
