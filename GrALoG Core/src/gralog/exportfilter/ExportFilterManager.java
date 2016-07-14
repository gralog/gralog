/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import static gralog.plugins.PluginManager.InstantiateClass;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 *
 * @author viktor
 */
public class ExportFilterManager {
    
    private final static HashMap<Class, HashMap<String, String>> ExportFilterNames = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, String>> ExportFilterExtensions = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, ExportFilterDescription>> ExportFilterDescriptions = new HashMap<Class, HashMap<String, ExportFilterDescription>>();

    public static void RegisterExportFilterClass(Class<?> aClass, String classname) throws Exception {
        if(Modifier.isAbstract(aClass.getModifiers()))
            return;        
        
        Method[] methods = aClass.getMethods();
        Class applicableToClass = null;
        for(Method method : methods)
        {
            if(!method.getName().equals("Export"))
                continue;
            Class[] params = method.getParameterTypes();
            if(params.length != 3)
                continue;
            applicableToClass = params[0];
        }

        // must have a method Export(FOO x) where FOO is derived from Structure!
        if(applicableToClass == null)
            throw new Exception("class " + aClass.getName() + " is derived from ExportFilter, but has no valid Export-method!");

        if(!aClass.isAnnotationPresent(ExportFilterDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @ExportFilterDescription annotation");

        ExportFilterDescription descr = aClass.getAnnotation(ExportFilterDescription.class);
        if(!ExportFilterNames.containsKey(applicableToClass))
        {
            ExportFilterNames.put(applicableToClass, new HashMap<String,String>());
            ExportFilterExtensions.put(applicableToClass, new HashMap<String,String>());
            ExportFilterDescriptions.put(applicableToClass, new HashMap<String, ExportFilterDescription>());
        }
        ExportFilterNames.get(applicableToClass).put(descr.name(), classname);
        ExportFilterExtensions.get(applicableToClass).put(descr.fileextension(), classname);
        ExportFilterDescriptions.get(applicableToClass).put(descr.name(), descr);

    }
    
    public static Vector<String> getExportFilters(Class<?> forClass) {
        HashSet<String> extensions = new HashSet<String>();
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(ExportFilterNames.containsKey(i))
                for(String f : ExportFilterNames.get(i).keySet())
                {
                    // if a derived class has an export-filter for the same
                    // file-extension, the derived one overrides the base one
                    if(ExportFilterDescriptions.get(i).containsKey(f))
                    {
                        ExportFilterDescription descr = ExportFilterDescriptions.get(i).get(f);
                        if(extensions.contains(descr.fileextension())) // the export filter was overridden
                            continue;
                        extensions.add(descr.fileextension());
                    }
                    result.add(f);
                }
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }    
    
    public static ExportFilter InstantiateExportFilter(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterNames.containsKey(c))
            {
                HashMap<String,String> filters = ExportFilterNames.get(c);
                if(filters.containsKey(identifier))
                    return (ExportFilter)InstantiateClass(filters.get(identifier));
            }
        return null;
    }
    
    public static ExportFilter InstantiateExportFilterByExtension(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterExtensions.containsKey(c))
            {
                HashMap<String,String> filters = ExportFilterExtensions.get(c);
                if(filters.containsKey(identifier))
                    return (ExportFilter)InstantiateClass(filters.get(identifier));
            }
        return null;
    }
    
    public static ExportFilterDescription getExportFilterDescription(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterDescriptions.containsKey(c))
            {
                HashMap<String,ExportFilterDescription> filters = ExportFilterDescriptions.get(c);
                if(filters.containsKey(identifier))
                    return filters.get(identifier);
            }
        return null;
    }

    
    
}
