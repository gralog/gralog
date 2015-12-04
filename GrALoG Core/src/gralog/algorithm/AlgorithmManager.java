/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import static gralog.plugins.PluginManager.InstantiateClass;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author viktor
 */
public class AlgorithmManager {
    
    // for algorithms and export, have different maps for each class they are applicable to
    private final static HashMap<Class, HashMap<String, String>> AlgorithmNames = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, AlgorithmDescription>> AlgorithmDescriptions = new HashMap<Class, HashMap<String, AlgorithmDescription>>();

    public static void RegisterAlgorithmClass(Class<?> aClass, String classname) throws Exception {
        Method[] methods = aClass.getMethods();
        Class applicableToClass = null;
        for(Method method : methods)
        {
            if(!method.getName().equals("Run"))
                continue;
            Class[] params = method.getParameterTypes();
            if(params.length < 1)
                continue;
            applicableToClass = params[0];
        }

        if(applicableToClass == null)
            throw new Exception("class " + aClass.getName() + " is derived from Algorithm, but has no valid Run-method!");

        if(!aClass.isAnnotationPresent(AlgorithmDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @AlgorithmDescription annotation");

        AlgorithmDescription descr = aClass.getAnnotation(AlgorithmDescription.class);
        if(!AlgorithmNames.containsKey(applicableToClass))
        {
            AlgorithmNames.put(applicableToClass, new HashMap<String,String>());
            AlgorithmDescriptions.put(applicableToClass, new HashMap<String, AlgorithmDescription>());
        }
        AlgorithmNames.get(applicableToClass).put(descr.name(), classname);
        AlgorithmDescriptions.get(applicableToClass).put(descr.name(), descr);

    }
    
    public static Vector<String> getAlgorithms(Class<?> forClass) {
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(AlgorithmNames.containsKey(i))
                result.addAll(AlgorithmNames.get(i).keySet());
        return result;
    }
    
    public static Algorithm InstantiateAlgorithm(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(AlgorithmNames.containsKey(c))
            {
                HashMap<String,String> algos = AlgorithmNames.get(c);
                if(algos.containsKey(identifier))
                    return (Algorithm)InstantiateClass(algos.get(identifier));
            }
        return null;
    }
    
    public static AlgorithmDescription getAlgorithmDescription(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(AlgorithmDescriptions.containsKey(c))
            {
                HashMap<String,AlgorithmDescription> algos = AlgorithmDescriptions.get(c);
                if(algos.containsKey(identifier))
                    return algos.get(identifier);
            }
        return null;
    }    
    
    
}
