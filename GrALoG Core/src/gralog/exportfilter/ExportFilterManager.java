/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

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
public class ExportFilterManager {

    private final static HashMap<Class, HashMap<String, String>> exportFilterNames
            = new HashMap<>();
    private final static HashMap<Class, HashMap<String, String>> exportFilterExtensions
            = new HashMap<>();
    private final static HashMap<Class, HashMap<String, ExportFilterDescription>> exportFilterDescriptions
            = new HashMap<>();

    public static void registerExportFilterClass(Class<?> aClass,
            String className) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        Method[] methods = aClass.getMethods();
        Class applicableToClass = null;
        for (Method method : methods) {
            if (!method.getName().equals("exportGraph"))
                continue;
            Class[] params = method.getParameterTypes();
            if (params.length != 3)
                continue;
            applicableToClass = params[0];
        }

        // must have a method Export(FOO x) where FOO is derived from Structure!
        if (applicableToClass == null)
            throw new Exception("class " + aClass.getName() + " is derived from ExportFilter, but has no valid exportGraph method!");

        if (!aClass.isAnnotationPresent(ExportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ExportFilterDescription annotation");

        ExportFilterDescription descr = aClass.getAnnotation(ExportFilterDescription.class);
        if (!exportFilterNames.containsKey(applicableToClass)) {
            exportFilterNames.put(applicableToClass, new HashMap<>());
            exportFilterExtensions.put(applicableToClass, new HashMap<>());
            exportFilterDescriptions.put(applicableToClass, new HashMap<>());
        }
        exportFilterNames.get(applicableToClass).put(descr.name(), className);
        exportFilterExtensions.get(applicableToClass).put(descr.fileExtension(), className);
        exportFilterDescriptions.get(applicableToClass).put(descr.name(), descr);
    }

    public static List<String> getExportFilters(Class<?> forClass) {
        HashSet<String> extensions = new HashSet<>();
        List<String> result = new ArrayList<>();
        for (Class i = forClass; i != null; i = i.getSuperclass())
            if (exportFilterNames.containsKey(i))
                for (String f : exportFilterNames.get(i).keySet()) {
                    // if a derived class has an export-filter for the same
                    // file-extension, the derived one overrides the base one
                    if (exportFilterDescriptions.get(i).containsKey(f)) {
                        ExportFilterDescription descr = exportFilterDescriptions.get(i).get(f);
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
            if (exportFilterNames.containsKey(c)) {
                HashMap<String, String> filters = exportFilterNames.get(c);
                if (filters.containsKey(identifier))
                    return (ExportFilter) instantiateClass(filters.get(identifier));
            }
        return null;
    }

    public static ExportFilter instantiateExportFilterByExtension(
            Class<?> forClass, String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (exportFilterExtensions.containsKey(c)) {
                HashMap<String, String> filters = exportFilterExtensions.get(c);
                if (filters.containsKey(identifier))
                    return (ExportFilter) instantiateClass(filters.get(identifier));
            }
        return null;
    }

    public static ExportFilterDescription getExportFilterDescription(
            Class<?> forClass, String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (exportFilterDescriptions.containsKey(c)) {
                HashMap<String, ExportFilterDescription> filters = exportFilterDescriptions.get(c);
                if (filters.containsKey(identifier))
                    return filters.get(identifier);
            }
        return null;
    }
}
