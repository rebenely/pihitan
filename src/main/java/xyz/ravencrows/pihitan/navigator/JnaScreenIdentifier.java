package xyz.ravencrows.pihitan.navigator;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import javafx.geometry.Rectangle2D;

/**
 * Use JNA to detect window size automatically
 */
public class JnaScreenIdentifier implements StdCallLibrary, ScreenPointsIdentifier {
  public static User32 user32;

  private JnaScreenIdentifier() {
    user32 = (User32) Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
  }

  private static class JnaScreenIdentifierLoader {
    private static final JnaScreenIdentifier INSTANCE = new JnaScreenIdentifier();
  }

  public static JnaScreenIdentifier getInstance() {
    return JnaScreenIdentifierLoader.INSTANCE;
  }

  @Override
  public Rectangle2D determineScreenSize(final String windowName) {
    WinDef.HWND hwnd = user32.FindWindow(null, windowName);
    if (hwnd == null) {
      throw new RuntimeException("Window not found");
    }
    
    WinDef.RECT rect = new WinDef.RECT();
    boolean result = user32.GetWindowRect(hwnd, rect);
    if(!result) {
      throw new RuntimeException("Unable to get window size");
    }
    final int width = rect.right - rect.left;
    final int height = rect.bottom - rect.top;

    return new Rectangle2D(rect.left, rect.top, width, height);
  }
}
