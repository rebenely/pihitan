package xyz.ravencrows.pihitan.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;

/**
 * User32 interface
 */
public interface PihitanUser32 extends User32 {
  PihitanUser32 INSTANCE = Native.load("user32", PihitanUser32.class, W32APIOptions.DEFAULT_OPTIONS);

  boolean SetProcessDPIAware(); // For Windows Vista and later
  int GetDpiForWindow(WinDef.HWND hwnd); // For Windows 10, version 1607 and later
}
