package xyz.ravencrows.pihitan.userconfig;

import java.util.List;

public class PersistedInput {
  private String name;
  private List<InputConfigSettings> actions;

  private boolean select;

  public PersistedInput(String name, List<InputConfigSettings> actions, boolean select) {
    this.name = name;
    this.actions = actions;
    this.select = select;
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

  public boolean isSelect() {
    return select;
  }

  public void setSelect(boolean select) {
    this.select = select;
  }
}
