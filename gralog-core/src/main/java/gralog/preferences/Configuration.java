package gralog.preferences;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public class Configuration {

    Map<String, Object> config;

    public Configuration() {
        config = new HashMap<>();
    }

    public Configuration(Properties prefs) {
        this();
        for(String key : prefs.stringPropertyNames()) {
            config.put(key, prefs.get(key));
        }
    }

    public Configuration(Configuration config) {
        this();
        if(config == null) {
            return;
        }
        for(String key : config.config.keySet()) {
            this.config.put(key, config.config.get(key));
        }
    }


    /**
     * Returns an object from the configuration dictionary. Can provide
     * a default argument
     */
    public <T> T getValue(String key, Function<String, T> parser, T def) {
        String obj = (String) config.getOrDefault(key, "[non_existent]0x002");
        if(obj.equals("[non_existent]0x002")) {
            return def;
        }else {
            return parser.apply(obj);
        }
    }
}
