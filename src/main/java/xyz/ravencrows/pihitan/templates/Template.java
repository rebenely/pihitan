package xyz.ravencrows.pihitan.templates;

import java.util.List;

public class Template {
  private String id;
  private String name;
  private List<TemplateSection> sections;

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
}
