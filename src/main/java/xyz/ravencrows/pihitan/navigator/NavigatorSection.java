package xyz.ravencrows.pihitan.navigator;

import xyz.ravencrows.pihitan.templates.TemplateSection;

import java.util.List;

public class NavigatorSection {
  private String id;

  private NavigatorPos pos;

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
}