/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.plugins;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.generator.*;
import gralog.exportfilter.*;
import gralog.importfilter.*;

import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author viktor
 */
public class PluginManager {

    private final static HashMap<String, Constructor> ClassRegister = new HashMap<String, Constructor>();

    public static void initialize() throws Exception {
        File f = new File(PluginManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        loadPlugin(f.getAbsolutePath());
    }

    public static Object instantiateClass(String className) throws Exception {
        if (!ClassRegister.containsKey(className))
            throw new Exception("class \"" + className + "\" is unknown, has no @XmlName annotation or has no empty constructor");

        Constructor ctor = ClassRegister.get(className);
        return ctor.newInstance();
    }

    public static void registerClass(String ClassName) throws Exception {
        Class<?> c = Class.forName(ClassName);
        PluginManager.registerClass(c);
    }

    public static void registerClass(Class<?> c) throws Exception {
        String XmlAlias = null;
        if (c.isAnnotationPresent(XmlName.class)) {
            XmlName xmlname = c.getAnnotation(XmlName.class);
            XmlAlias = xmlname.name();
        }
        registerClass(c.getName(), XmlAlias, c);
    }

    public static void registerClass(String classname, String xmlAlias,
            Class<?> aClass) throws Exception {
        if (Modifier.isAbstract(aClass.getModifiers()))
            return;

        if (ClassRegister.containsKey(classname))
            throw new Exception("class name \"" + classname + "\" already exists!");
        if (xmlAlias != null)
            if (ClassRegister.containsKey(xmlAlias))
                throw new Exception("class name \"" + xmlAlias + "\" already exists!");

        try {
            Constructor ctor = aClass.getConstructor(new Class[]{});
            // Register the Class
            ClassRegister.put(classname, ctor);
            if (xmlAlias != null)
                ClassRegister.put(xmlAlias, ctor);

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
        }
        catch (NoSuchMethodException e) {
        }
    }

    public static void loadPlugin(String pathToPlugin) throws Exception {
        File plugin = new File(pathToPlugin);

        // Add the plugin to the classpath
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(sysloader, new Object[]{plugin.toURL()});

        // Load the classes
        Collection<Class<?>> classes = new ArrayList<>();
        JarFile jar = new JarFile(pathToPlugin);
        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.endsWith(".class")) {
                String classname = file.replace('/', '.').substring(0, file.length() - 6);
                Class<?> c = Class.forName(classname, false, sysloader);
                classes.add(c);
            }
        }

        // Register the classes
        for (Class<?> c : classes)
            PluginManager.registerClass(c);
    }
}
