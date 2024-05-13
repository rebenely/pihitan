package xyz.ravencrows.pihitan.util;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.ravencrows.pihitan.InputConfigController;
import xyz.ravencrows.pihitan.userconfig.PersistedConfig;
import xyz.ravencrows.pihitan.userconfig.PersistedInput;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Works on current directory
 */
public class PersistUtil {
  private static final Map<Integer, PersistedConfig> cache = new ConcurrentHashMap<>();

  private static final Logger logger = LoggerFactory.getLogger(InputConfigController.class);
  public static final String CONFIG_JSON = "config.json";

  /**
   * Cached value
   */
  public static PersistedConfig getConfig() {
    return cache.computeIfAbsent(0, key -> loadConfig());
  }

  private static PersistedConfig loadConfig() {
    logger.info("Loading config.js");
    final Gson gson = GsonUtil.getInstance();
    try(final BufferedReader br = new BufferedReader(new FileReader(CONFIG_JSON))) {
      return gson.fromJson(br, PersistedConfig.class);
    } catch (Exception e) {
      logger.error("Unable to read config", e);
      logger.info("Creating empty config"); // to prevent exceptions in future calls
      final PersistedConfig newConfig = new PersistedConfig();
      persistConfig(newConfig);
      return newConfig;
    }
  }

  public static void addOrUpdateInputConfig(PersistedInput input) {
    PersistedConfig config = getConfig();

    List<PersistedInput> inputs = config.getInputs();
    if(inputs == null) {
      logger.info("No inputs, creating new");
      config.setInputs(new ArrayList<>());
      config.getInputs().add(input);
    } else {
      boolean foundExisting = false;
      for(PersistedInput persistedInput : inputs) {
        if(!persistedInput.getName().equals(input.getName())) {
          continue;
        }
        foundExisting = true;
        logger.info("Updating persisted input");
        persistedInput.setActions(input.getActions());
      }

      if(!foundExisting) {
        logger.info("Adding new persisted input");
        config.getInputs().add(input);
      }
    }

    persistConfig(config);
  }

  public static void persistConfig(PersistedConfig persistedConfig) {
    logger.info("Persisting to file");
    final Gson gson = GsonUtil.getInstance();

    try (Writer writer = new FileWriter(CONFIG_JSON)) {
      gson.toJson(persistedConfig, writer);
    } catch (Exception e) {
      logger.error("Unable to write config", e);
    }

    // clear cache
    cache.clear();
  }

  public static void setDefaults(String templates, String input) {
    PersistedConfig config = getConfig();
    config.setInput(input);
    config.setTemplate(templates);
    persistConfig(config);
  }
}
