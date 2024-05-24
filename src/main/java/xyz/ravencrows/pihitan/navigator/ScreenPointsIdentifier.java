package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * Interface for determining the screen size
 */
public interface ScreenPointsIdentifier {
  Rectangle2D determineScreenSize(final String windowName);
}
