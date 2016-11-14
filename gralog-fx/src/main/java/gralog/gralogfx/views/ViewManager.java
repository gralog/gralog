/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
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
public final class ViewManager {

    private ViewManager() {
    }

    private static final HashMap<Class, Constructor<? extends View>> VIEW_REGISTER
        = new HashMap<>();

    public static void initialize() throws Exception {
        File f = new File(ViewManager.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        loadPlugin(f.getAbsolutePath());
    }

    public static View instantiateView(Class<?> forClass) throws Exception {
        for (Class sup = forClass; sup != null; sup = sup.getSuperclass()) {
            if (VIEW_REGISTER.containsKey(sup)) {
                Constructor<? extends View> ctor = VIEW_REGISTER.get(sup);
                return ctor.newInstance();
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
        method.invoke(sysloader, new Object[]{plugin.toURI().toURL()});

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
                Constructor<? extends View> ctor = (Constructor<? extends View>) c.getConstructor(new Class[]{});
                if (ctor == null)
                    throw new Exception("class \"" + c.getName() + "\" has no empty constructor");

                if (!c.isAnnotationPresent(ViewDescription.class))
                    throw new Exception("class " + c.getName() + " has no @ViewDescription annotation");
                ViewDescription descr = c.getAnnotation(ViewDescription.class);

                // Register it
                VIEW_REGISTER.put(descr.forClass(), ctor);
            }
        }
    }
}
