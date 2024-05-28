package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.templates.ItemPosition;

public class NavigatorPos {
  private double x;
  private double y;

  private double origX;
  private double origY;

  private NavigatorPos(double x, double y, double origX, double origY) {
    this.x = x;
    this.y = y;
    this.origX = origX;
    this.origY = origY;
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

  public double getOrigX() {
    return origX;
  }

  public void setOrigX(double origX) {
    this.origX = origX;
  }

  public double getOrigY() {
    return origY;
  }

  public void setOrigY(double origY) {
    this.origY = origY;
  }

  /**
   * Creates the actual screen positions based on the external app bounds
   */
  public static NavigatorPos fromItem(ItemPosition pos, Rectangle2D externalAppBounds) {
    return new NavigatorPos(
            externalAppBounds.getWidth() * (pos.getX()*.01) + externalAppBounds.getMinX(),
            externalAppBounds.getHeight() * (pos.getY()*.01) + externalAppBounds.getMinY(),
            pos.getX(),
            pos.getY()
          );
  }

  public Point2D getPoint() {
    return new Point2D(getX(), getY());
  }

  /**
   * Recompute the actual screen position given the new externalAppBounds
   */
  public void recompute(Rectangle2D externalAppBounds) {
    this.x = externalAppBounds.getWidth() * (this.origX*.01) + externalAppBounds.getMinX();
    this.y = externalAppBounds.getHeight() * (this.origY*.01) + externalAppBounds.getMinY();
  }
}
