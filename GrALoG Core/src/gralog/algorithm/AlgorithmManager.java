/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 * @author viktor
 */
public class AlgorithmManager {

    // for algorithms and export, have different maps for each class they are applicable to
    private final static HashMap<Class, HashMap<String, String>> algorithmNames = new HashMap<>();
    private final static HashMap<Class, HashMap<String, AlgorithmDescription>> algorithmDescriptions = new HashMap<>();

    public static void registerAlgorithmClass(Class<?> aClass, String className) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        Method[] methods = aClass.getMethods();
        Class applicableToClass = null;
        for (Method method : methods) {
            if (!method.getName().equals("run"))
                continue;
            Class[] params = method.getParameterTypes();
            if (params.length < 1)
                continue;
            applicableToClass = params[0];
        }

        if (applicableToClass == null)
            throw new Exception("class " + aClass.getName() + " is derived from Algorithm, but has no valid run method!");

        if (!aClass.isAnnotationPresent(AlgorithmDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @AlgorithmDescription annotation");

        AlgorithmDescription descr = aClass.getAnnotation(AlgorithmDescription.class);
        if (!algorithmNames.containsKey(applicableToClass)) {
            algorithmNames.put(applicableToClass, new HashMap<>());
            algorithmDescriptions.put(applicableToClass, new HashMap<>());
        }
        algorithmNames.get(applicableToClass).put(descr.name(), className);
        algorithmDescriptions.get(applicableToClass).put(descr.name(), descr);
    }

    public static List<String> getAlgorithms(Class<?> forClass) {
        List<String> result = new ArrayList<>();
        for (Class i = forClass; i != null; i = i.getSuperclass())
            if (algorithmNames.containsKey(i))
                result.addAll(algorithmNames.get(i).keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static Algorithm instantiateAlgorithm(Class<?> forClass,
            String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (algorithmNames.containsKey(c)) {
                HashMap<String, String> algos = algorithmNames.get(c);
                if (algos.containsKey(identifier))
                    return (Algorithm) instantiateClass(algos.get(identifier));
            }
        return null;
    }

    public static AlgorithmDescription getAlgorithmDescription(Class<?> forClass,
            String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (algorithmDescriptions.containsKey(c)) {
                HashMap<String, AlgorithmDescription> algos = algorithmDescriptions.get(c);
                if (algos.containsKey(identifier))
                    return algos.get(identifier);
            }
        return null;
    }
}
