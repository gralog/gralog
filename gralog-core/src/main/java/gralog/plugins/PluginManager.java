/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.plugins;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmManager;
import gralog.exportfilter.ExportFilter;
import gralog.exportfilter.ExportFilterManager;
import gralog.generator.Generator;
import gralog.generator.GeneratorManager;
import gralog.importfilter.ImportFilter;
import gralog.importfilter.ImportFilterManager;
import gralog.structure.Structure;
import gralog.structure.StructureManager;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
public final class PluginManager {

    private static final HashMap<String, Constructor> CLASS_REGISTER = new HashMap<String, Constructor>();

    private PluginManager() {
    }

    /**
     * Registers all classes in the current jar file. Requires that the program
     * is running from a jar file.
     *
     * @throws Exception Throws if there are duplicate XML names or if a class
     *                   does not satisfy the requirements imposed by the manager class (for
     *                   example, it is an export class without an "export" method).
     */
    public static void initialize() throws Exception {
        File f = new File(PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        loadPlugin(f.getAbsolutePath());
    }

    /**
     * Instantiates a class from its name or XML name. Only considers classes
     * that have been registered before with one of the other methods.
     *
     * @param className The name or the XML name of the class to instantiate.
     * @return A new instance of the requested class.
     * @throws Exception Throws if the class has not been registered.
     */
    public static Object instantiateClass(String className) throws Exception {
        if (!CLASS_REGISTER.containsKey(className))
            throw new Exception("class \"" + className + "\" is unknown, has no @XmlName annotation or has no empty constructor");

        Constructor ctor = CLASS_REGISTER.get(className);
        return ctor.newInstance();
    }

    /**
     * Registers a single class to the respective manager class. Ignores
     * abstract classes. Determines the XML name from the XmlName annotation.
     *
     * @param c The class to register.
     * @throws Exception Throws if the class name or the XML name has already
     *                   been registered or if the class does not satisfy the requirements imposed
     *                   by the manager class (for example, it is an export class without an
     *                   "export" method).
     */
    public static void registerClass(Class<?> c) throws Exception {
        String xmlAlias = null;
        if (c.isAnnotationPresent(XmlName.class)) {
            XmlName xmlname = c.getAnnotation(XmlName.class);
            xmlAlias = xmlname.name();
        }
        registerClass(c.getName(), xmlAlias, c);
    }

    /**
     * Registers a single class to the respective manager class. Ignores
     * abstract classes.
     *
     * @param classname The name of the class to register.
     * @param xmlAlias  The XML name of the class to register.
     * @param aClass    The class to register.
     * @throws Exception Throws if the class name or the XML name has already
     *                   been registered or if the class does not satisfy the requirements imposed
     *                   by the manager class (for example, it is an export class without an
     *                   "export" method).
     */
    public static void registerClass(String classname, String xmlAlias,
                                     Class<?> aClass) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (CLASS_REGISTER.containsKey(classname))
            throw new Exception("class name \"" + classname + "\" already exists!");
        if (xmlAlias != null)
            if (CLASS_REGISTER.containsKey(xmlAlias))
                throw new Exception("class name \"" + xmlAlias + "\" already exists!");

        try {
            Constructor ctor = aClass.getConstructor(new Class[]{});
            // Register the Class
            CLASS_REGISTER.put(classname, ctor);
            if (xmlAlias != null)
                CLASS_REGISTER.put(xmlAlias, ctor);

            // Register the classes in the corresponding managers
            for (Class sup = aClass; sup != null; sup = sup.getSuperclass()) {
                if (sup == Structure.class) // Structure
                    StructureManager.registerStructureClass(aClass, classname);

                if (sup == Generator.class) // Generator
                    GeneratorManager.registerGeneratorClass(aClass, classname);

                if (sup == ImportFilter.class) // Import Filter
                    ImportFilterManager.registerImportFilterClass(aClass, classname);

                if (sup == Algorithm.class) // Algorithms
                    AlgorithmManager.registerAlgorithmClass(aClass, classname);

                if (sup == ExportFilter.class) // Export Filter
                    ExportFilterManager.registerExportFilterClass(aClass, classname);
            }
        } catch (NoSuchMethodException e) {
        }
    }

    /**
     * Load a jar file and register all its classes.
     *
     * @param pathToPlugin The path to the jar file.
     * @throws Exception Throws if the jar file cannot be loaded.
     */
    public static void loadPlugin(String pathToPlugin) throws Exception {
        File plugin = new File(pathToPlugin);

        // Add the plugin to the classpath
        URLClassLoader sysloader = new URLClassLoader(new URL[]{plugin.toURL()}, ClassLoader.getSystemClassLoader());

        // Load the classes
        Collection<Class<?>> classes = new ArrayList<>();
        try (JarFile jar = new JarFile(pathToPlugin)) {
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String file = entry.getName();
                if (file.endsWith(".class")) {
                    String classname = file.replace('/', '.').substring(0, file.length() - 6);

                    //catches classdeferrors from jflex generated classes
                    try {
                        Class<?> c = Class.forName(classname, false, sysloader);
                        classes.add(c);
                    } catch (NoClassDefFoundError e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Register all the classes we found.
        for (Class<?> c : classes)
            PluginManager.registerClass(c);
    }

    /**
     * Removes all registered classes. After calling this method, the static
     * state of this class will be nearly the same as when you started the
     * program.
     *
     * However, jar files loaded with loadPlugin() will not be unloaded,
     * although all their classes will become unknown to PluginManager. It will
     * also not clear the state of ExportFilterManager etc., even though
     * PluginManager changes their state when registering classes.
     *
     * When you call this method, you most likely also want to call the clear()
     * methods of the resource managing classes like ExportFilterManager.
     */
    public static void clear() {
        CLASS_REGISTER.clear();
    }
}
