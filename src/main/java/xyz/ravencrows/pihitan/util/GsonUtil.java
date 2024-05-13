package xyz.ravencrows.pihitan.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Gson singleton
 */
public class GsonUtil {

  private final static Gson instance = new GsonBuilder().setPrettyPrinting().create();

  public static Gson getInstance() {
    return instance;
  }
}
