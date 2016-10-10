/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.structure;

import static gralog.plugins.PluginManager.InstantiateClass;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author viktor
 */
public class StructureManager {
    
    // The XNames HashMaps are maps from description-name -> fully qualified class name
    private final static HashMap<String, String> StructureNames = new HashMap<String,String>();
    private final static HashMap<String, StructureDescription> StructureDescriptions = new HashMap<String,StructureDescription>();
    
    public static void RegisterStructureClass(Class<?> aClass, String classname) throws Exception
    {
        if(Modifier.isAbstract(aClass.getModifiers()))
            return;
        
        if(!aClass.isAnnotationPresent(StructureDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @StructureDescription annotation");
        StructureDescription descr = aClass.getAnnotation(StructureDescription.class);
        StructureNames.put(descr.name(), classname);
        StructureDescriptions.put(descr.name(), descr);
    }
    
    
    public static List<String> getStructureClasses() {
        ArrayList<String> result = new ArrayList<>(StructureNames.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }
    
    public static Structure InstantiateStructure(String identifier) throws Exception {
        String classname = StructureNames.get(identifier);
        return (Structure)InstantiateClass(classname);
    }
    
    public static StructureDescription getStructureDescription(String identifier) {
        return StructureDescriptions.get(identifier);
    }
    
}
