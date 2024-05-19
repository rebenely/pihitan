package xyz.ravencrows.pihitan.templates;

import java.util.List;

/**
 * Base class for sections,
 * Will probably implement sections in sections in the future
 */
public class TemplateSection {
  private String id;
  private ItemPosition pos;
  private List<TemplateItem> items;

  private ItemPosition postStep;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ItemPosition getPos() {
    return pos;
  }

  public void setPos(ItemPosition defaultPos) {
    this.pos = defaultPos;
  }

  public List<TemplateItem> getItems() {
    return items;
  }

  public void setItems(List<TemplateItem> items) {
    this.items = items;
  }

  public ItemPosition getPostStep() {
    return postStep;
  }

  public void setPostStep(ItemPosition postStep) {
    this.postStep = postStep;
  }
}
