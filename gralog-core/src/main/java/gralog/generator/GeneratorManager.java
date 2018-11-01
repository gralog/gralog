/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public final class GeneratorManager {

    private static final HashMap<String, String> GENERATOR_NAMES = new HashMap<>();
    private static final HashMap<String, GeneratorDescription> GENERATOR_DESCRIPTIONS = new HashMap<>();
    private GeneratorManager() {
    }

    public static void registerGeneratorClass(Class<?> aClass, String classname) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (!aClass.isAnnotationPresent(GeneratorDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @GeneratorDescription annotation");
        GeneratorDescription descr = aClass.getAnnotation(GeneratorDescription.class);
        GENERATOR_NAMES.put(descr.name(), classname);
        GENERATOR_DESCRIPTIONS.put(descr.name(), descr);
    }

    public static List<String> getGeneratorClasses() {
        ArrayList<String> result = new ArrayList<>(GENERATOR_NAMES.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static GeneratorDescription getGeneratorDescription(String identifier) {
        return GENERATOR_DESCRIPTIONS.get(identifier);
    }

    public static Generator instantiateGenerator(String identifier) throws Exception {
        String classname = GENERATOR_NAMES.get(identifier);
        return (Generator) instantiateClass(classname);
    }
}
