/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.structure;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gralog.plugins.PluginManager.instantiateClass;

/**
 *
 */
public final class StructureManager {

    // The XNames HashMaps are maps from description-name -> fully qualified class name
    private static final HashMap<String, String> STRUCTURE_NAMES
            = new HashMap<>();
    private static final HashMap<String, StructureDescription> STRUCTURE_DESCRIPTIONS
            = new HashMap<>();
    private static int nextId = 0;
    private StructureManager() {
    }

    public static void registerStructureClass(Class<?> aClass, String className) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (!aClass.isAnnotationPresent(StructureDescription.class))
            throw new Exception("class " + aClass.getName() + " has no @StructureDescription annotation");
        StructureDescription descr = aClass.getAnnotation(StructureDescription.class);
        STRUCTURE_NAMES.put(descr.name(), className);
        STRUCTURE_DESCRIPTIONS.put(descr.name(), descr);
    }

    public static List<String> getStructureClasses() {
        ArrayList<String> result = new ArrayList<>(STRUCTURE_NAMES.keySet());
        result.sort(String.CASE_INSENSITIVE_ORDER);
        return result;
    }

    public static Structure instantiateStructure(String identifier) throws Exception {
        String classname = STRUCTURE_NAMES.get(identifier);
        Structure structure = (Structure) instantiateClass(classname);
        return structure;
    }

    public static StructureDescription getStructureDescription(String identifier) {
        return STRUCTURE_DESCRIPTIONS.get(identifier);
    }
}
