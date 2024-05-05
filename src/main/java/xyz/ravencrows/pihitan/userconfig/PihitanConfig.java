package xyz.ravencrows.pihitan.userconfig;

import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.templates.Template;

/**
 * Singleton config for the app
 */
public class PihitanConfig {
  private final static PihitanConfig instance = new PihitanConfig();
  private Rectangle2D dspBounds;
  private InputType inputType;
  private InputConfigSettings inputSettings;

  private Template template;

  public static PihitanConfig getInstance() {
    return instance;
  }

  public Rectangle2D getDspBounds() {
    return dspBounds;
  }

  public void setDspBounds(Rectangle2D dspBounds) {
    this.dspBounds = dspBounds;
  }

  public InputType getInputType() {
    return inputType;
  }

  public void setInputType(InputType inputType) {
    this.inputType = inputType;
  }

  public InputConfigSettings getInputSettings() {
    return inputSettings;
  }

  public void setInputSettings(InputConfigSettings inputSettings) {
    this.inputSettings = inputSettings;
  }

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }
}
