package xyz.ravencrows.pihitan.userconfig;

import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.input.InputListener;
import xyz.ravencrows.pihitan.input.InputType;
import xyz.ravencrows.pihitan.templates.Template;

/**
 * Singleton config for the app
 */
public class PihitanConfig {
  private final static PihitanConfig instance = new PihitanConfig();
  private Rectangle2D dspBounds;
  private InputType inputType;

  private InputListener input;

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

  public InputListener getInput() {
    return input;
  }

  public void setInput(InputListener input) {
    this.input = input;
  }

  public Template getTemplate() {
    return template;
  }

  public void setTemplate(Template template) {
    this.template = template;
  }
}
