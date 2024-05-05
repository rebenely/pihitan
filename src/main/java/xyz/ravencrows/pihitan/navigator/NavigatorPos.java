package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.templates.ItemPosition;

public class NavigatorPos {
  private double x;
  private double y;

  public NavigatorPos(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  /**
   * Creates the actual screen positions based on the external app bounds
   *
   * @param pos
   * @param externalAppBounds
   * @return
   */
  public static NavigatorPos fromItem(ItemPosition pos, Rectangle2D externalAppBounds) {
    return new NavigatorPos(
            externalAppBounds.getWidth() * (pos.getX()*.01) + externalAppBounds.getMinX(),
            externalAppBounds.getHeight() * (pos.getY()*.01) + externalAppBounds.getMinY()
          );
  }
}
