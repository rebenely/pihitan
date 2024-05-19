package xyz.ravencrows.pihitan.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

/**
 * Gson singleton
 */
public class GsonUtil {
  private final static Gson instance =
          new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Color.class, new ColorDeserializer())
            .create();

  public static Gson getInstance() {
    return instance;
  }
}
