package xyz.ravencrows.pihitan.templates;

import java.util.List;

/**
 * Main template class for holding details for ScreenNavigator
 */
public class Template {
  private String id;
  private String name;
  private List<TemplateSection> sections;
  private ItemPosition prevPreset;
  private ItemPosition nextPreset;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<TemplateSection> getSections() {
    return sections;
  }

  public void setSections(List<TemplateSection> sections) {
    this.sections = sections;
  }

  public ItemPosition getPrevPreset() {
    return prevPreset;
  }

  public void setPrevPreset(ItemPosition prevPreset) {
    this.prevPreset = prevPreset;
  }

  public ItemPosition getNextPreset() {
    return nextPreset;
  }

  public void setNextPreset(ItemPosition nextPreset) {
    this.nextPreset = nextPreset;
  }
}
