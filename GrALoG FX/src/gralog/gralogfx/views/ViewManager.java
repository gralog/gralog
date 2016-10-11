/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx.views;

import java.io.File;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javafx.scene.Node;

/**
 *
 */
public class ViewManager {

    private final static HashMap<Class, Constructor> viewRegister = new HashMap<>();

    public static void initialize() throws Exception {
        File f = new File(ViewManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        loadPlugin(f.getAbsolutePath());
    }

    public static View instantiateView(Class<?> forClass) throws Exception {
        for (Class sup = forClass; sup != null; sup = sup.getSuperclass()) {
            if (viewRegister.containsKey(sup)) {
                Constructor ctor = viewRegister.get(sup);
                return (View) ctor.newInstance();
            }
        }

        return null;
    }

    // copy-n-paste of Gralogs 
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

        // Register the views
        for (Class<?> c : classes) {
            if (Modifier.isAbstract(c.getModifiers()))
                continue;

            boolean isView = false;
            boolean isNode = false;
            for (Class sup = c; sup != null; sup = sup.getSuperclass()) {
                if (sup == Node.class)
                    isNode = true;

                for (Class iface : sup.getInterfaces())
                    if (iface == View.class)
                        isView = true;
            }

            if (isNode && isView) {
                // okay, it is a view. (try to) register it
                Constructor ctor = c.getConstructor(new Class[]{});
                if (ctor == null)
                    throw new Exception("class \"" + c.getName() + "\" has no empty constructor");

                if (!c.isAnnotationPresent(ViewDescription.class))
                    throw new Exception("class " + c.getName() + " has no @ViewDescription annotation");
                ViewDescription descr = c.getAnnotation(ViewDescription.class);

                // Register it
                viewRegister.put(descr.forClass(), ctor);
            }
        }
    }
}
