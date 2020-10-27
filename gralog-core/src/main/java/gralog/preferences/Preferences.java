/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.preferences;

import gralog.rendering.GralogColor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * Stores user-specific preferences. In contrast to the Java Preferences API,
 * this class stores everything in a single file in the user's home directory.
 */
public final class Preferences {
    private static final String VERSION = "0.0.4";
    private static final String FILENAME = "preferences";
    private static final java.util.Properties PROPERTIES = new java.util.Properties();
    private static final String PREFERENCE_PATH = buildPreferencePath();

    public static Properties getProperties() {
        return PROPERTIES;
    }

    private Preferences() {
    }


    private static String buildPreferencePath() {
        String path;
        final String os = System.getProperty("os.name").toUpperCase();
        final String myname = "gralog";
        if (os.contains("WIN"))
            path = System.getenv("APPDATA") + "/" + myname;
        else if (os.contains("MAC"))
            path = System.getProperty("user.home") + "/Library/Application Support/" + myname;
        else if (os.contains("NUX")) {
            path = System.getenv("XDG_CONFIG_HOME");
            if (path == null)
                path = System.getProperty("user.home") + "/.config";
            path += "/" + myname;
        } else
            path = System.getProperty("user.dir") + "/." + myname;

        try (FileInputStream in = new FileInputStream(path + "/" + FILENAME)) {
            PROPERTIES.load(in);
            String id = PROPERTIES.getProperty("pref_id");
            if (id == null || !id.equals(VERSION)) {
                in.close();
                createDefaultPrefs(path);
            }
        } catch (FileNotFoundException e) {
            createDefaultPrefs(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

    private static void createDefaultPrefs(String path) {
        System.out.println("No config was found - add default preferences");
        InputStream def = Preferences.class.getClassLoader().getResourceAsStream("default_preferences.txt");

        try {
            Files.copy(def, Paths.get(path + "/" + FILENAME), StandardCopyOption.REPLACE_EXISTING);
            def.close();
            try (FileInputStream in = new FileInputStream(path + "/" + FILENAME)) {
                PROPERTIES.load(in);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /*
     **********
     * STRING *
     **********
     * */
    public static String getString(Class c, String key, String defaultValue) {
        return getString(classKey(c, key), defaultValue);
    }

    public static String getString(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    public static void setString(Class c, String key, String value) {
        setString(classKey(c, key), value);
    }

    public static void setString(String key, String value) {
        PROPERTIES.setProperty(key, value);
        flush();
    }

    /*
     ***********
     * INTEGER *
     ***********
     * */
    public static Integer getInteger(Class c, String key, int defaultValue) {
        return getInteger(classKey(c, key), defaultValue);
    }

    public static Integer getInteger(String key, int defaultValue) {
        try {
            return Integer.parseInt(
                    PROPERTIES.getProperty(key, Integer.toString(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void setInteger(Class c, String key, int value) {
        setInteger(classKey(c, key), value);
    }

    public static void setInteger(String key, int value) {
        PROPERTIES.setProperty(key, Integer.toString(value));
        flush();
    }

    /*
     ***********
     * BOOLEAN *
     ***********
     * */
    public static Boolean getBoolean(Class c, String key, boolean defaultValue) {
        return getBoolean(classKey(c, key), defaultValue);
    }

    public static Boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(PROPERTIES.getProperty(key, Boolean.toString(defaultValue)));
    }

    public static void setBoolean(Class c, String key, boolean b) {
        setBoolean(classKey(c, key), b);
    }

    public static void setBoolean(String key, boolean b) {
        PROPERTIES.setProperty(key, Boolean.toString(b));
        flush();
    }


    /*
     **********
     * FILE *
     **********
     * */

    /**
     * Returns the file with filename from the preferences file with key == key. If the key is not found, returns the
     * defaultValue. If the file is not found, returns null.
     * @param key The key for the preferences file as specified in the definition of PROPERTIES
     * @param defaultValue The default file.
     * @return A file or null
     */
    public static File getFile(String key, String defaultValue) {
        File f = new File(PROPERTIES.getProperty(key, defaultValue));
        if (!f.exists())
            return null;

        // todo:
        // in tests (PreferenceWindowtest), the key "MainWindow_pipingFile" does not exist
        // and an error occurs for new File(null)
        return f;

    }


    public static void setFile(String key, File file) {
        PROPERTIES.setProperty(key, file.getPath());
        flush();
    }

    /*
     **********
     * DOUBLE *
     **********
     * */
    public static Double getDouble(Class c, String key, double defaultValue) {
        return getDouble(classKey(c, key), defaultValue);
    }

    public static Double getDouble(String key, double defaultValue) {
        return Double.parseDouble(PROPERTIES.getProperty(key, Double.toString(defaultValue)));
    }

    public static void setDouble(Class c, String key, double d) {
        setDouble(classKey(c, key), d);
    }

    public static void setDouble(String key, double d) {
        PROPERTIES.setProperty(key, Double.toString(d));
        flush();
    }

    /*
     *********
     * COLOR *
     *********
     * */
    public static GralogColor getColor(Class c, String key, GralogColor defaultValue) {
        return getColor(classKey(c, key), defaultValue);
    }

    public static GralogColor getColor(String key, GralogColor defaultValue) {
        return GralogColor.parseColor(PROPERTIES.getProperty(key, defaultValue.toHtmlString()));
    }

    public static void setColor(Class c, String key, GralogColor color) {
        setColor(classKey(c, key), color);
    }

    public static void setColor(String key, GralogColor c) {
        PROPERTIES.setProperty(key, c.toHtmlString());
        flush();
    }

    private static String classKey(Class c, String key) {
        return c.getName() + "-" + key;
    }

    private static void flush() {
        try {
            Files.createDirectories(Paths.get(PREFERENCE_PATH));
            try (FileOutputStream out = new FileOutputStream(PREFERENCE_PATH + "/" + FILENAME)) {
                PROPERTIES.store(out, "Gralog preferences");
            }
        } catch (IOException e) {
        }
    }

    /*
     *********
     * PRINT PROPS*
     *********
     * */
    public static void printProps() {
        System.out.println("\u001B[31m PRINTING PROPS \u001B[0m");
        for (var pn : PROPERTIES.stringPropertyNames()) {
            System.out.println(pn + " " + PROPERTIES.getProperty(pn));
        }

    }
}
