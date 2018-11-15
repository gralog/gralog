/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.structure.Structure;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public final class ExportFilterManager {

    private static final HashMap<Class, HashMap<String, String>> EXPORT_FILTER_NAMES
            = new HashMap<>();
    private static final HashMap<Class, HashMap<String, String>> EXPORT_FILTER_EXTENSIONS
            = new HashMap<>();
    private static final HashMap<Class, HashMap<String, ExportFilterDescription>> EXPORT_FILTER_DESCRIPTIONS
            = new HashMap<>();
    private ExportFilterManager() {
    }

    public static void registerExportFilterClass(Class<?> aClass,
                                                 String className) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        Method[] methods = aClass.getMethods();
        Class applicableToClass = null;
        for (Method method : methods) {
            if (!method.getName().equals("export"))
                continue;
            Class[] params = method.getParameterTypes();
            if (params.length != 3)
                continue;
            applicableToClass = params[0];
        }

        // Must have a method export(FOO x) where FOO is derived from Structure.
        if (applicableToClass == null || !Structure.class.isAssignableFrom(applicableToClass))
            throw new Exception("class " + aClass.getName() + " is derived from ExportFilter, but has no valid export method!");

        if (!aClass.isAnnotationPresent(ExportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ExportFilterDescription annotation");

        ExportFilterDescription descr = aClass.getAnnotation(ExportFilterDescription.class);
        if (!EXPORT_FILTER_NAMES.containsKey(applicableToClass)) {
            EXPORT_FILTER_NAMES.put(applicableToClass, new HashMap<>());
            EXPORT_FILTER_EXTENSIONS.put(applicableToClass, new HashMap<>());
            EXPORT_FILTER_DESCRIPTIONS.put(applicableToClass, new HashMap<>());
        }
        EXPORT_FILTER_NAMES.get(applicableToClass).put(descr.name(), className);
        EXPORT_FILTER_EXTENSIONS.get(applicableToClass).put(descr.fileExtension(), className);
        EXPORT_FILTER_DESCRIPTIONS.get(applicableToClass).put(descr.name(), descr);
    }

    public static List<String> getExportFilters(Class<?> forClass) {
        HashSet<String> extensions = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (Class i = forClass; i != null; i = i.getSuperclass())
            if (EXPORT_FILTER_NAMES.containsKey(i))
                for (String f : EXPORT_FILTER_NAMES.get(i).keySet()) {
                    // if a derived class has an export-filter for the same
                    // file-extension, the derived one overrides the base one
                    if (EXPORT_FILTER_DESCRIPTIONS.get(i).containsKey(f)) {
                        ExportFilterDescription descr = EXPORT_FILTER_DESCRIPTIONS.get(i).get(f);
                        if (extensions.contains(descr.fileExtension())) // the export filter was overridden
                            continue;
                        extensions.add(descr.fileExtension());
                    }
                    result.add(f);
                }
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static ExportFilter instantiateExportFilter(Class<?> forClass,
                                                       String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (EXPORT_FILTER_NAMES.containsKey(c)) {
                HashMap<String, String> filters = EXPORT_FILTER_NAMES.get(c);
                if (filters.containsKey(identifier))
                    return (ExportFilter) instantiateClass(filters.get(identifier));
            }
        return null;
    }

    public static ExportFilter instantiateExportFilterByExtension(
            Class<?> forClass, String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (EXPORT_FILTER_EXTENSIONS.containsKey(c)) {
                HashMap<String, String> filters = EXPORT_FILTER_EXTENSIONS.get(c);
                if (filters.containsKey(identifier))
                    return (ExportFilter) instantiateClass(filters.get(identifier));
            }
        return null;
    }

    public static ExportFilterDescription getExportFilterDescription(
            Class<?> forClass, String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (EXPORT_FILTER_DESCRIPTIONS.containsKey(c)) {
                HashMap<String, ExportFilterDescription> filters = EXPORT_FILTER_DESCRIPTIONS.get(c);
                if (filters.containsKey(identifier))
                    return filters.get(identifier);
            }
        return null;
    }

    /**
     * Removes all registered filters.  After calling this method, the static
     * state of this class will be the same as when you started the program.
     */
    public static void clear() {
        EXPORT_FILTER_NAMES.clear();
        EXPORT_FILTER_EXTENSIONS.clear();
        EXPORT_FILTER_DESCRIPTIONS.clear();
    }
}
