/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 * @author viktor
 */
public class ImportFilterManager {

    private final static HashMap<String, String> importFilterNames = new HashMap<>();
    private final static HashMap<String, String> importFilterExtensions = new HashMap<>();
    private final static HashMap<String, ImportFilterDescription> importFilterDescriptions = new HashMap<>();

    public static void registerImportFilterClass(Class<?> aClass,
            String classname) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (!aClass.isAnnotationPresent(ImportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ImportFilterDescription annotation");
        ImportFilterDescription descr = aClass.getAnnotation(ImportFilterDescription.class);
        importFilterNames.put(descr.name(), classname);
        importFilterExtensions.put(descr.fileExtension(), classname);
        importFilterDescriptions.put(descr.name(), descr);
    }

    public static List<String> getImportFilterClasses() {
        List<String> result = new ArrayList<>(importFilterNames.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static ImportFilter instantiateImportFilter(String identifier) throws Exception {
        String classname = importFilterNames.get(identifier);
        return (ImportFilter) instantiateClass(classname);
    }

    public static ImportFilter instantiateImportFilterByExtension(
            String identifier) throws Exception {
        if (!importFilterExtensions.containsKey(identifier))
            return null;
        String classname = importFilterExtensions.get(identifier);
        return (ImportFilter) instantiateClass(classname);
    }

    public static ImportFilterDescription getImportFilterDescription(
            String identifier) {
        return importFilterDescriptions.get(identifier);
    }
}
