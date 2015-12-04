/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

import static gralog.plugins.PluginManager.InstantiateClass;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author viktor
 */
public class GeneratorManager {
 
    private final static HashMap<String, String> GeneratorNames = new HashMap<String,String>();
    private final static HashMap<String, GeneratorDescription> GeneratorDescriptions = new HashMap<String,GeneratorDescription>();
    
    public static void RegisterGeneratorClass(Class<?> aClass, String classname) throws Exception {
        if(!aClass.isAnnotationPresent(GeneratorDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @GeneratorDescription annotation");
        GeneratorDescription descr = aClass.getAnnotation(GeneratorDescription.class);
        GeneratorNames.put(descr.name(), classname);
        GeneratorDescriptions.put(descr.name(), descr);
    }

    public static Set<String> getGeneratorClasses() {
        return GeneratorNames.keySet();
    }
    
    public static GeneratorDescription getGeneratorDescription(String identifier) {
        return GeneratorDescriptions.get(identifier);
    }
    
    public static Generator InstantiateGenerator(String identifier) throws Exception {
        String classname = GeneratorNames.get(identifier);
        return (Generator)InstantiateClass(classname);
    }
}
