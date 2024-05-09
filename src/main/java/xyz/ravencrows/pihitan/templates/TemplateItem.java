package xyz.ravencrows.pihitan.templates;

import java.util.List;

/**
 * Item in template, can have items in items
 */
public class TemplateItem {
  private String id;
  private ItemType type;
  private ItemPosition pos;
  private List<TemplateItem> items;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ItemType getType() {
    return type;
  }

  public void setType(ItemType type) {
    this.type = type;
  }

  public ItemPosition getPos() {
    return pos;
  }

  public void setPos(ItemPosition pos) {
    this.pos = pos;
  }

  public List<TemplateItem> getItems() {
    return items;
  }

  public void setItems(List<TemplateItem> items) {
    this.items = items;
  }
}
