package xyz.ravencrows.pihitan.navigator;

import javafx.geometry.Rectangle2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenIdentifierService {
  private static final Logger logger = LoggerFactory.getLogger(ScreenIdentifierService.class);
  private static ScreenIdentifierService INSTANCE;

  private ScreenIdentifierService() {
  }

  public static ScreenIdentifierService getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new ScreenIdentifierService();
    }

    return INSTANCE;
  }

  /**
   * Determine screen size using automatic detection or manually
   */
  public Rectangle2D getScreenSize(final String windowName) {
    // determine screen size
    final boolean hasWindowName = windowName != null && !windowName.isBlank();
    if (!hasWindowName) {
      // manually determine window size, add prompt here
      return determineWindowSizeManually();
    }

    // automatically detect
    try {
      return JnaScreenIdentifier.getInstance().determineScreenSize(windowName);
    } catch (Exception e) {
      logger.error("Error while finding window, please manually determine points", e);
      return determineWindowSizeManually();
    }
  }

  /**
   * Show the overlay for the user screen size input
   */
  protected Rectangle2D determineWindowSizeManually() {
    logger.info("Unable to detect app, please manually determine dimensions");
    return new ManualScreenIdentifier().determineScreenSize("");
  }

}
