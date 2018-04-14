/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public final class AlgorithmManager {

    private AlgorithmManager() {
    }

    // for algorithms and export, have different maps for each class they are applicable to
    private static final HashMap<Class, HashMap<String, String>> ALGORITHM_NAMES
        = new HashMap<>();
    private static final HashMap<Class, HashMap<String, AlgorithmDescription>> ALGORITHM_DESCRIPTIONS
        = new HashMap<>();

    public static void registerAlgorithmClass(Class<?> aClass, String className) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;
        Method[] methods = new Method[0];

        try{
            methods = aClass.getMethods();
        }catch(NoClassDefFoundError e){
            System.out.println("WARNING: algorithm class def not found..."+className);

            return;
        }

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
        if (!ALGORITHM_NAMES.containsKey(applicableToClass)) {
            ALGORITHM_NAMES.put(applicableToClass, new HashMap<>());
            ALGORITHM_DESCRIPTIONS.put(applicableToClass, new HashMap<>());
        }
        ALGORITHM_NAMES.get(applicableToClass).put(descr.name(), className);
        ALGORITHM_DESCRIPTIONS.get(applicableToClass).put(descr.name(), descr);
    }

    public static List<String> getAlgorithms(Class<?> forClass) {
        List<String> result = new ArrayList<>();
        for (Class i = forClass; i != null; i = i.getSuperclass())
            if (ALGORITHM_NAMES.containsKey(i))
                result.addAll(ALGORITHM_NAMES.get(i).keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static Algorithm instantiateAlgorithm(Class<?> forClass,
        String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (ALGORITHM_NAMES.containsKey(c)) {
                HashMap<String, String> algos = ALGORITHM_NAMES.get(c);
                if (algos.containsKey(identifier))
                    return (Algorithm) instantiateClass(algos.get(identifier));
            }
        return null;
    }

    public static AlgorithmDescription getAlgorithmDescription(Class<?> forClass,
        String identifier) throws Exception {
        for (Class c = forClass; c != null; c = c.getSuperclass())
            if (ALGORITHM_DESCRIPTIONS.containsKey(c)) {
                HashMap<String, AlgorithmDescription> algos = ALGORITHM_DESCRIPTIONS.get(c);
                if (algos.containsKey(identifier))
                    return algos.get(identifier);
            }
        return null;
    }
}
