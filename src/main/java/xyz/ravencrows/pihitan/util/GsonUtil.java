package xyz.ravencrows.pihitan.util;

import com.google.gson.Gson;

/**
 * Gson singleton
 */
public class GsonUtil {

  private final static Gson instance = new Gson();

  public static Gson getInstance() {
    return instance;
  }
}
