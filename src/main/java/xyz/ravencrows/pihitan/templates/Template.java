package xyz.ravencrows.pihitan.templates;

import javafx.scene.paint.Color;

import java.util.List;

/**
 * Main template class for holding details for ScreenNavigator
 */
public class Template {
  private String id;
  private String windowName;
  private List<TemplateSection> sections;
  private ItemPosition prevPreset;
  private ItemPosition nextPreset;
  private Color color;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public String getWindowName() {
    return windowName;
  }

  public void setWindowName(String windowName) {
    this.windowName = windowName;
  }
}
