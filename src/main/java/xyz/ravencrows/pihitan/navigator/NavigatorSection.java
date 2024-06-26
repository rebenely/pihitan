package xyz.ravencrows.pihitan.navigator;

import java.util.List;

public class NavigatorSection implements NavigatorDisplay {
  private String id;

  private NavigatorPos pos;

  private NavigatorPos postStep;

  private List<NavigatorItem> items;

  public NavigatorSection(String id, NavigatorPos pos) {
    this.id = id;
    this.pos = pos;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public NavigatorPos getPos() {
    return pos;
  }

  public void setPos(NavigatorPos pos) {
    this.pos = pos;
  }

  public List<NavigatorItem> getItems() {
    return items;
  }

  public void setItems(List<NavigatorItem> items) {
    this.items = items;
  }

  public NavigatorPos getPostStep() {
    return postStep;
  }

  public void setPostStep(NavigatorPos postStep) {
    this.postStep = postStep;
  }

  @Override
  public String getDisplayName() {
    return id;
  }
}
