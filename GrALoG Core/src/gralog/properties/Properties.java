/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Stores user-specific preferences. In contrast to the Java Preferences API,
 * this class stores everything in a single file in the user's home directory.
 */
public class Properties {

    private static final String FILENAME = "preferences";
    private static final java.util.Properties properties = new java.util.Properties();
    private static final String PREFERENCE_PATH = buildPreferencePath();

    private static String buildPreferencePath() {
        String path;
        final String OS = System.getProperty("os.name").toUpperCase();
        final String myname = "GrALoG FX";
        if (OS.contains("WIN"))
            path = System.getenv("APPDATA") + "/" + myname;
        else if (OS.contains("MAC"))
            path = System.getProperty("user.home") + "/Library/Application Support/" + myname;
        else if (OS.contains("NUX")) {
            path = System.getenv("XDG_CONFIG_HOME");
            if (path == null)
                path = System.getProperty("user.home") + "/.config";
            path += "/" + myname;
        }
        else
            path = System.getProperty("user.dir") + "/." + myname;

        try {
            FileInputStream in = new FileInputStream(path + "/" + FILENAME);
            properties.load(in);
        }
        catch (IOException e) {
            // Most likely the config file does not exist, so we can ignore
            // this exception.
        }
        return path;
    }

    public static String getString(Class c, String key, String defaultValue) {
        return getString(classKey(c, key), defaultValue);
    }

    public static String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static Integer getInteger(Class c, String key, int defaultValue) {
        return getInteger(classKey(c, key), defaultValue);
    }

    public static Integer getInteger(String key, int defaultValue) {
        try {
            return Integer.parseInt(
                    properties.getProperty(key, Integer.toString(defaultValue)));
        }
        catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void setString(Class c, String key, String value) {
        setString(classKey(c, key), value);
    }

    public static void setString(String key, String value) {
        properties.setProperty(key, value);
        flush();
    }

    public static void setInteger(Class c, String key, int value) {
        setInteger(classKey(c, key), value);
    }

    public static void setInteger(String key, int value) {
        properties.setProperty(key, Integer.toString(value));
        flush();
    }

    private static String classKey(Class c, String key) {
        return c.getName() + "-" + key;
    }

    private static void flush() {
        try {
            Files.createDirectories(Paths.get(PREFERENCE_PATH));
            FileOutputStream out = new FileOutputStream(PREFERENCE_PATH + "/" + FILENAME);
            properties.store(out, "GrALoG preferences");
        }
        catch (IOException e) {
        }
    }
}
