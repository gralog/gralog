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

    
    private final static HashMap<String, Constructor> ClassRegister = new HashMap<String,Constructor>();
    private final static HashMap<String, Constructor> StructureClasses = new HashMap<String,Constructor>();
    private final static HashMap<String, Constructor> GeneratorClasses = new HashMap<String,Constructor>();
    private final static HashMap<String, Constructor> ImportFilterClasses = new HashMap<String,Constructor>();
    
    private final static HashMap<Class, Vector<String> > AlgorithmClasses = new HashMap<Class, Vector<String> >();
    private final static HashMap<Class, Vector<String> > ExportFilterClasses = new HashMap<Class, Vector<String> >();
    
    
    public static Object InstantiateClass(String className) throws Exception {
        className = className.toLowerCase();
        if(!ClassRegister.containsKey(className))
            throw new Exception("class \"" + className + "\" is unknown, has no XmlName annotation or has no empty constructor");
        
        Constructor ctor = ClassRegister.get(className);
        return ctor.newInstance();
    }
    
    
    public static void RegisterClass(String ClassName) throws Exception {
        Class<?> c = Class.forName(ClassName);
        RegisterClass(c);
    }
    
    public static void RegisterClass(Class<?> c) throws Exception {
        if(c.isAnnotationPresent(XmlName.class))
        {
            XmlName xmlname = c.getAnnotation(XmlName.class);
            RegisterClass(xmlname.name(), c);
        } else if(c.isAnnotationPresent(Description.class))
        {
            Description descr = c.getAnnotation(Description.class);
            RegisterClass(descr.name(), c);
        }
    }
    
    public static void RegisterClass(String name, Class aClass) throws Exception
    {
        // name = name.toLowerCase();
        if(ClassRegister.containsKey(name))
            throw new Exception("class name \"" + name + "\" already exists!");
        Constructor ctor = null;
        try {
            ctor = aClass.getConstructor(new Class[]{});
            ClassRegister.put(name, ctor);
            
            for(Class sup = aClass; sup != null; sup = sup.getSuperclass())
            {
                if(sup == Structure.class)
                    StructureClasses.put(name, ctor);
                
                if(sup == Generator.class)
                    GeneratorClasses.put(name, ctor);

                if(sup == ImportFilter.class)
                    ImportFilterClasses.put(name, ctor);
                
                
                if(sup == Algorithm.class) {
                    Method[] methods = aClass.getMethods();
                    Class applicableToClass = null;
                    for(Method method : methods)
                    {
                        if(!method.getName().equals("Run"))
                            continue;
                        Class[] params = method.getParameterTypes();
                        if(params.length < 1)
                            continue;
                        applicableToClass = params[0];
                    }
                    
                    if(applicableToClass == null)
                        throw new Exception("class " + aClass.getName() + " is derived from Algorithm, but has no valid Run-method!");
                    
                    if(!AlgorithmClasses.containsKey(applicableToClass))
                        AlgorithmClasses.put(applicableToClass, new Vector<String>());
                    AlgorithmClasses.get(applicableToClass).add(name);
                }
                
                if(sup == ExportFilter.class) {
                    Method[] methods = aClass.getMethods();
                    Class applicableToClass = null;
                    for(Method method : methods)
                    {
                        if(!method.getName().equals("Export"))
                            continue;
                        Class[] params = method.getParameterTypes();
                        if(params.length < 1)
                            continue;
                        applicableToClass = params[0];
                    }
                    
                    // must have a method Export(FOO x) where FOO is derived from Structure!
                    if(applicableToClass == null)
                        throw new Exception("class " + aClass.getName() + " is derived from ExportFilter, but has no valid Export-method!");
                    
                    if(!ExportFilterClasses.containsKey(applicableToClass))
                        ExportFilterClasses.put(applicableToClass, new Vector<String>());
                    ExportFilterClasses.get(applicableToClass).add(name);
                }                
            }
            
        } catch(NoSuchMethodException e)
        {
        }
    }
    
    public static Set<String> getStructureClasses() {
        return StructureClasses.keySet();
    }

    public static Set<String> getGeneratorClasses() {
        return GeneratorClasses.keySet();
    }

    public static Set<String> getImportFilterClasses() {
        return ImportFilterClasses.keySet();
    }    
    
    public static Vector<String> getAlgorithms(Class<?> forClass) {
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(AlgorithmClasses.containsKey(i))
                result.addAll(AlgorithmClasses.get(i));
        return result;
    }

    public static Vector<String> getExportFilters(Class<?> forClass) {
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(ExportFilterClasses.containsKey(i))
                result.addAll(ExportFilterClasses.get(i));
        return result;
    }    
    

    public static void Initialize() throws Exception {
        RegisterClass(DirectedGraph.class);
        RegisterClass(Vertex.class);
        RegisterClass(Edge.class);
        RegisterClass(TrivialGraphFormatImport.class);
    }

    
    public static void LoadPlugin(String pathToPlugin) throws Exception {
        File plugin = new File(pathToPlugin);
        
        
        // Add the plugin to the classpath
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;
        Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
        method.setAccessible(true);
        method.invoke(sysloader, new Object[]{plugin.toURL()});


        // Load the classes
        Collection<Class<?>> classes = new ArrayList<Class<?>>();
        JarFile jar = new JarFile(pathToPlugin);
        for (Enumeration<JarEntry> entries = jar.entries() ; entries.hasMoreElements() ;)
        {
            JarEntry entry = entries.nextElement();
            String file = entry.getName();
            if (file.endsWith(".class"))
            {
                String classname = file.replace('/', '.').substring(0, file.length() - 6);
                Class<?> c = Class.forName(classname,false,sysloader);
                classes.add(c);
            }
        }

        
        // Register the classes
        for (Class<?> c : classes)
            RegisterClass(c);
        
 
    }
    
    
}
