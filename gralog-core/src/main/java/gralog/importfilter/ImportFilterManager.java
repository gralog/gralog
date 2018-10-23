/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.importfilter;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public final class ImportFilterManager {

    private static final HashMap<String, String> IMPORT_FILTER_NAMES = new HashMap<>();
    private static final HashMap<String, String> IMPORT_FILTER_EXTENSIONS = new HashMap<>();
    private static final HashMap<String, ImportFilterDescription> IMPORT_FILTER_DESCRIPTIONS = new HashMap<>();
    private ImportFilterManager() {
    }

    public static void registerImportFilterClass(Class<?> aClass,
                                                 String classname) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (!aClass.isAnnotationPresent(ImportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ImportFilterDescription annotation");
        ImportFilterDescription descr = aClass.getAnnotation(ImportFilterDescription.class);
        IMPORT_FILTER_NAMES.put(descr.name(), classname);
        IMPORT_FILTER_EXTENSIONS.put(descr.fileExtension(), classname);
        IMPORT_FILTER_DESCRIPTIONS.put(descr.name(), descr);
    }

    public static List<String> getImportFilterClasses() {
        List<String> result = new ArrayList<>(IMPORT_FILTER_NAMES.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static ImportFilter instantiateImportFilter(String identifier) throws Exception {
        String classname = IMPORT_FILTER_NAMES.get(identifier);
        return (ImportFilter) instantiateClass(classname);
    }

    public static ImportFilter instantiateImportFilterByExtension(
            String identifier) throws Exception {
        if (!IMPORT_FILTER_EXTENSIONS.containsKey(identifier))
            return null;
        String classname = IMPORT_FILTER_EXTENSIONS.get(identifier);
        return (ImportFilter) instantiateClass(classname);
    }

    public static ImportFilterDescription getImportFilterDescription(
            String identifier) {
        return IMPORT_FILTER_DESCRIPTIONS.get(identifier);
    }

    /**
     * Removes all registered filters.  After calling this method, the static
     * state of this class will be the same as when you started the program.
     */
    public static void clear() {
        IMPORT_FILTER_NAMES.clear();
        IMPORT_FILTER_EXTENSIONS.clear();
        IMPORT_FILTER_DESCRIPTIONS.clear();
    }
}
