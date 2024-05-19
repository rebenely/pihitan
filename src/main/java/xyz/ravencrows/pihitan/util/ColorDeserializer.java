package xyz.ravencrows.pihitan.util;

import com.google.gson.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Load an array of double as Color
 */
public class ColorDeserializer implements JsonDeserializer<Color> {
  private static final Logger logger = LoggerFactory.getLogger(ColorDeserializer.class);

  @Override
  public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    if(!json.isJsonArray() || json.getAsJsonArray().size() != 4) {
      logger.warn("Invalid value {}", json);
      return null;
    }

    JsonArray array = json.getAsJsonArray();
    return new Color(
            array.get(0).getAsDouble(),
            array.get(1).getAsDouble(),
            array.get(2).getAsDouble(),
            array.get(3).getAsDouble());
  }
}
