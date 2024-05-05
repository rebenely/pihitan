package xyz.ravencrows.pihitan.templates;

import java.util.List;

/**
 * Base class for template
 */
public class TemplateSection {
  private String id;
  private ItemPosition defaultPos;
  private List<TemplateItem> items;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ItemPosition getDefaultPos() {
    return defaultPos;
  }

  public void setDefaultPos(ItemPosition defaultPos) {
    this.defaultPos = defaultPos;
  }

  public List<TemplateItem> getItems() {
    return items;
  }

  public void setItems(List<TemplateItem> items) {
    this.items = items;
  }
}
