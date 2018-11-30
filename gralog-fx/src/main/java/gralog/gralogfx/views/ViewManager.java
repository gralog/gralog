/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
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
 * Views are classes are usually implemented by custom javaFX Nodes classes
 * like the GridPaneView. The workflow is not trivial, so below is an in-depth
 * explanation.
 *
 * A view works tightly with the object inspector and is used to visualize
 * or modify class properties. The workflow is as follows.
 *
 * 1) Create a class that implements View V and extends a Node
 * 2) Annotate it with @ViewDescription(forClass = X.class)
 * 3) X should be a class that can be selected, like a Vertex or Edge etc
 * 3.1) In the View V you define what happens when you select an object of X.
 *
 * Example: Class - ReflectedView
 * The method setObject(Object o,..) adds several labels and textfields to its own node for
 * every field in o. Since its forClass annotation is Object.class, this view will be a kind
 * of last resort for all objects.
 * A ReflectedView object will, after calling setObject(x), create multiple UI objects and
 * sort them according to the layout specifications of GridPane. Afterwards an external callee
 * (say the object inspector) can now decide if and how this UI element is to be included into the
 * scene.
 *
 *
 * 4) Now, unless there is a class Y that extends X AND there is View that
 * has a forClass=Y.class annotation, selecting an object o of class X will
 * instantiate a view object of V.
 * 5) In the object inspector that view object will be added to the scene and made
 * visible.
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
        var sysloader = ClassLoader.getSystemClassLoader();
        // Load the classes
        Collection<Class<?>> classes = new ArrayList<>();
        try (JarFile jar = new JarFile(pathToPlugin)) {
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
                JarEntry entry = entries.nextElement();
                String file = entry.getName();
                if (file.endsWith(".class")) {
                    String classname = file.replace('/', '.').substring(0, file.length() - 6);

                    //catches classdeferrors from jflex generated classes
                    try {
                        Class<?> c = Class.forName(classname, false, sysloader);
                        classes.add(c);
                    }catch (NoClassDefFoundError e) {
                        System.err.println("ncdf " +classname);
                    }catch (ClassNotFoundException e) {
                        System.err.println("cnf " + classname);
                    }
                }
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
                Constructor<? extends View> ctor = (Constructor<? extends View>) c.getConstructor(new Class[] {});
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
