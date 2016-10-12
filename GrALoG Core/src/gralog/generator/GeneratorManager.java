/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.generator;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public class GeneratorManager {

    private final static HashMap<String, String> generatorNames = new HashMap<>();
    private final static HashMap<String, GeneratorDescription> generatorDescriptions = new HashMap<>();

    public static void registerGeneratorClass(Class<?> aClass, String classname) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (!aClass.isAnnotationPresent(GeneratorDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @GeneratorDescription annotation");
        GeneratorDescription descr = aClass.getAnnotation(GeneratorDescription.class);
        generatorNames.put(descr.name(), classname);
        generatorDescriptions.put(descr.name(), descr);
    }

    public static List<String> getGeneratorClasses() {
        ArrayList<String> result = new ArrayList<>(generatorNames.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static GeneratorDescription getGeneratorDescription(String identifier) {
        return generatorDescriptions.get(identifier);
    }

    public static Generator instantiateGenerator(String identifier) throws Exception {
        String classname = generatorNames.get(identifier);
        return (Generator) instantiateClass(classname);
    }
}
