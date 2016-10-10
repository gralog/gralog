/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import static gralog.plugins.PluginManager.InstantiateClass;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author viktor
 */
public class ImportFilterManager {
 
    private final static HashMap<String, String> ImportFilterNames = new HashMap<String,String>();
    private final static HashMap<String, String> ImportFilterExtensions = new HashMap<String,String>();
    private final static HashMap<String, ImportFilterDescription> ImportFilterDescriptions = new HashMap<String,ImportFilterDescription>();
    
    public static void RegisterImportFilterClass(Class<?> aClass, String classname) throws Exception
    {
        if(Modifier.isAbstract(aClass.getModifiers()))
            return;
        
        if(!aClass.isAnnotationPresent(ImportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ImportFilterDescription annotation");
        ImportFilterDescription descr = aClass.getAnnotation(ImportFilterDescription.class);
        ImportFilterNames.put(descr.name(), classname);
        ImportFilterExtensions.put(descr.fileextension(), classname);
        ImportFilterDescriptions.put(descr.name(), descr);
    }
    
    public static List<String> getImportFilterClasses() {
        ArrayList<String> result = new ArrayList<>();
        result.addAll(ImportFilterNames.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }
    
    public static ImportFilter InstantiateImportFilter(String identifier) throws Exception {
        String classname = ImportFilterNames.get(identifier);
        return (ImportFilter)InstantiateClass(classname);
    }
    
    public static ImportFilter InstantiateImportFilterByExtension(String identifier) throws Exception {
        if(!ImportFilterExtensions.containsKey(identifier))
            return null;
        String classname = ImportFilterExtensions.get(identifier);
        return (ImportFilter)InstantiateClass(classname);
    }
    
    public static ImportFilterDescription getImportFilterDescription(String identifier) {
        return ImportFilterDescriptions.get(identifier);
    }

}
