package xyz.ravencrows.pihitan.navigator;

import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import javafx.geometry.Rectangle2D;
import xyz.ravencrows.pihitan.util.PihitanUser32;

/**
 * Use JNA to detect window size automatically
 */
public class JnaScreenIdentifier implements StdCallLibrary, ScreenPointsIdentifier {
  private static JnaScreenIdentifier INSTANCE;

  private JnaScreenIdentifier() {
  }

  public static JnaScreenIdentifier getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new JnaScreenIdentifier();
    }

    return INSTANCE;
  }

  public static final double DEFAULT_SCALING_FACTOR = 96.0;

  @Override
  public Rectangle2D determineScreenSize(final String windowName) {
    PihitanUser32.INSTANCE.SetProcessDPIAware();
    WinDef.HWND hwnd = PihitanUser32.INSTANCE.FindWindow(null, windowName);
    if (hwnd == null) {
      throw new RuntimeException("Window not found");
    }

    WinDef.RECT rect = new WinDef.RECT();
    boolean result = PihitanUser32.INSTANCE.GetWindowRect(hwnd, rect);
    if(!result) {
      throw new RuntimeException("Unable to get window size");
    }

    double dpi = getDpi(hwnd);
    double scalingFactor = dpi / DEFAULT_SCALING_FACTOR;

    // Adjust the width and height based on the scaling factor
    final int width = (int) ((rect.right - rect.left) / scalingFactor);
    final int height = (int) ((rect.bottom - rect.top) / scalingFactor);

    // Adjust the left and top coordinates based on the scaling factor
    final int left = (int) (rect.left / scalingFactor);
    final int top = (int) (rect.top / scalingFactor);

    return new Rectangle2D(left, top, width, height);
  }

  private double getDpi(WinDef.HWND hwnd) {
    try {
      return PihitanUser32.INSTANCE.GetDpiForWindow(hwnd);
    } catch (UnsatisfiedLinkError e) {
      throw new RuntimeException("Unable to get window size");
    }
  }

}
