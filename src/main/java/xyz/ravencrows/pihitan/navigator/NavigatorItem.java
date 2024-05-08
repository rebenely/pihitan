package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.templates.TemplateItem;

public class NavigatorItem implements NavigatorDisplay {
  private String id;
  private NavigatorPos pos;

  private String parent;

  private NavigatorPos pressPos;

  public NavigatorItem(TemplateItem item, Rectangle2D bounds) {
    this.id = item.getId();
    this.pos = NavigatorPos.fromItem(item.getPos(), bounds);
  }

  public NavigatorPos getPressPos() {
    return pressPos;
  }

  public void setPressPos(NavigatorPos pressPos) {
    this.pressPos = pressPos;
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

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  @Override
  public String getDisplayName() {
    return parent + ":" + id;
  }
}
