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
    
    // The XNames HashMaps are maps from description-name -> fully qualified class name
    private final static HashMap<String, String> StructureNames = new HashMap<String,String>();
    private final static HashMap<String, StructureDescription> StructureDescriptions = new HashMap<String,StructureDescription>();
    
    private final static HashMap<String, String> GeneratorNames = new HashMap<String,String>();
    private final static HashMap<String, GeneratorDescription> GeneratorDescriptions = new HashMap<String,GeneratorDescription>();
    
    private final static HashMap<String, String> ImportFilterNames = new HashMap<String,String>();
    private final static HashMap<String, String> ImportFilterExtensions = new HashMap<String,String>();
    private final static HashMap<String, ImportFilterDescription> ImportFilterDescriptions = new HashMap<String,ImportFilterDescription>();
    
    // for algorithms and export, have different maps for each class they are applicable to
    private final static HashMap<Class, HashMap<String, String>> AlgorithmNames = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, AlgorithmDescription>> AlgorithmDescriptions = new HashMap<Class, HashMap<String, AlgorithmDescription>>();
    
    private final static HashMap<Class, HashMap<String, String>> ExportFilterNames = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, String>> ExportFilterExtensions = new HashMap<Class, HashMap<String, String>>();
    private final static HashMap<Class, HashMap<String, ExportFilterDescription>> ExportFilterDescriptions = new HashMap<Class, HashMap<String, ExportFilterDescription>>();


    
    public static void Initialize() throws Exception {
        RegisterClass(DirectedGraph.class);
        RegisterClass(Vertex.class);
        RegisterClass(Edge.class);
        RegisterClass(TrivialGraphFormatImport.class);
        RegisterClass(TrivialGraphFormatExport.class);
    }    
    

    public static Object InstantiateClass(String className) throws Exception {
        if(!ClassRegister.containsKey(className))
            throw new Exception("class \"" + className + "\" is unknown, has no @XmlName annotation or has no empty constructor");
        
        Constructor ctor = ClassRegister.get(className);
        return ctor.newInstance();
    }


    public static void RegisterClass(String ClassName) throws Exception {
        Class<?> c = Class.forName(ClassName);
        RegisterClass(c);
    }
    
    
    public static void RegisterClass(Class<?> c) throws Exception {
        String XmlAlias = null;
        if(c.isAnnotationPresent(XmlName.class))
        {
            XmlName xmlname = c.getAnnotation(XmlName.class);
            XmlAlias = xmlname.name();
        }
        RegisterClass(c.getName(), XmlAlias, c);
    }
    

    public static void RegisterClass(String classname, String xmlAlias, Class<?> aClass) throws Exception
    {
        if(ClassRegister.containsKey(classname))
            throw new Exception("class name \"" + classname + "\" already exists!");
        if(xmlAlias != null)
            if(ClassRegister.containsKey(xmlAlias))
                throw new Exception("class name \"" + xmlAlias + "\" already exists!");
        
        Constructor ctor = null;
        try {
            ctor = aClass.getConstructor(new Class[]{});
            // Register the Class
            ClassRegister.put(classname, ctor);
            if(xmlAlias != null)
                ClassRegister.put(xmlAlias, ctor);
            
            
            // Store the descriptions
            for(Class sup = aClass; sup != null; sup = sup.getSuperclass())
            {
                
                if(sup == Structure.class)                                      // Structure
                {
                    if(!aClass.isAnnotationPresent(StructureDescription.class))
                        throw new Exception("class " + aClass.getName() + " has no @StructureDescription annotation");
                    StructureDescription descr = aClass.getAnnotation(StructureDescription.class);
                    StructureNames.put(descr.name(), classname);
                    StructureDescriptions.put(descr.name(), descr);
                }
                
                
                if(sup == Generator.class)                                      // Generator
                {
                    if(!aClass.isAnnotationPresent(GeneratorDescription.class))
                        throw new Exception("class " + aClass.getName() + " has no @GeneratorDescription annotation");
                    GeneratorDescription descr = aClass.getAnnotation(GeneratorDescription.class);
                    GeneratorNames.put(descr.name(), classname);
                    GeneratorDescriptions.put(descr.name(), descr);
                }
                
                
                if(sup == ImportFilter.class)                                   // Import Filter
                {
                    if(!aClass.isAnnotationPresent(ImportFilterDescription.class))
                        throw new Exception("class " + aClass.getName() + " has no @ImportFilterDescription annotation");
                    ImportFilterDescription descr = aClass.getAnnotation(ImportFilterDescription.class);
                    ImportFilterNames.put(descr.name(), classname);
                    ImportFilterExtensions.put(descr.fileextension(), classname);
                    ImportFilterDescriptions.put(descr.name(), descr);
                }
                
                
                if(sup == Algorithm.class) {                                    // Algorithms
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

                    if(!aClass.isAnnotationPresent(AlgorithmDescription.class))
                        throw new Exception("class " + aClass.getName() + " has no @AlgorithmDescription annotation");
                    
                    AlgorithmDescription descr = aClass.getAnnotation(AlgorithmDescription.class);
                    if(!AlgorithmNames.containsKey(applicableToClass))
                    {
                        AlgorithmNames.put(applicableToClass, new HashMap<String,String>());
                        AlgorithmDescriptions.put(applicableToClass, new HashMap<String, AlgorithmDescription>());
                    }
                    AlgorithmNames.get(applicableToClass).put(descr.name(), classname);
                    AlgorithmDescriptions.get(applicableToClass).put(descr.name(), descr);
                }
                
                
                if(sup == ExportFilter.class) {                                 // Export Filter
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
                    
                    if(!aClass.isAnnotationPresent(ExportFilterDescription.class))
                        throw new Exception("class " + aClass.getName() + " has no @ExportFilterDescription annotation");
                    
                    ExportFilterDescription descr = aClass.getAnnotation(ExportFilterDescription.class);
                    if(!ExportFilterNames.containsKey(applicableToClass))
                    {
                        ExportFilterNames.put(applicableToClass, new HashMap<String,String>());
                        ExportFilterExtensions.put(applicableToClass, new HashMap<String,String>());
                        ExportFilterDescriptions.put(applicableToClass, new HashMap<String, ExportFilterDescription>());
                    }
                    ExportFilterNames.get(applicableToClass).put(descr.name(), classname);
                    ExportFilterNames.get(applicableToClass).put(descr.fileextension(), classname);
                    ExportFilterDescriptions.get(applicableToClass).put(descr.name(), descr);
                }
                
            }
            
        } catch(NoSuchMethodException e)
        {
        }
    }
    
    
    
    
    public static Set<String> getStructureClasses() {
        return StructureNames.keySet();
    }
    public static Structure InstantiateStructure(String identifier) throws Exception {
        String classname = StructureNames.get(identifier);
        return (Structure)InstantiateClass(classname);
    }
    public static StructureDescription getStructureDescription(String identifier) {
        return StructureDescriptions.get(identifier);
    }
    
    
    public static Set<String> getGeneratorClasses() {
        return GeneratorNames.keySet();
    }
    public static GeneratorDescription getGeneratorDescription(String identifier) {
        return GeneratorDescriptions.get(identifier);
    }
    public static Generator InstantiateGenerator(String identifier) throws Exception {
        String classname = GeneratorNames.get(identifier);
        return (Generator)InstantiateClass(classname);
    }
    
    
    public static Set<String> getImportFilterClasses() {
        return ImportFilterNames.keySet();
    }
    public static ImportFilter InstantiateImportFilter(String identifier) throws Exception {
        String classname = ImportFilterNames.get(identifier);
        return (ImportFilter)InstantiateClass(classname);
    }
    public static ImportFilter InstantiateImportFilterByExtension(String identifier) throws Exception {
        String classname = ImportFilterExtensions.get(identifier);
        return (ImportFilter)InstantiateClass(classname);
    }
    public static ImportFilterDescription getImportFilterDescription(String identifier) {
        return ImportFilterDescriptions.get(identifier);
    }
    
    
    public static Vector<String> getAlgorithms(Class<?> forClass) {
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(AlgorithmNames.containsKey(i))
                result.addAll(AlgorithmNames.get(i).keySet());
        return result;
    }
    public static Algorithm InstantiateAlgorithm(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(AlgorithmNames.containsKey(c))
            {
                HashMap<String,String> algos = AlgorithmNames.get(c);
                if(algos.containsKey(identifier))
                    return (Algorithm)InstantiateClass(algos.get(identifier));
            }
        return null;
    }
    public static AlgorithmDescription getAlgorithmDescription(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(AlgorithmDescriptions.containsKey(c))
            {
                HashMap<String,AlgorithmDescription> algos = AlgorithmDescriptions.get(c);
                if(algos.containsKey(identifier))
                    return algos.get(identifier);
            }
        return null;
    }
    
    public static Vector<String> getExportFilters(Class<?> forClass) {
        HashSet<String> extensions = new HashSet<String>();
        Vector<String> result = new Vector<String>();
        for(Class i = forClass; i != null; i = i.getSuperclass())
            if(ExportFilterNames.containsKey(i))
                for(String f : ExportFilterNames.get(i).keySet())
                {
                    // if a derived class has an export-filter for the same
                    // file-extension, the derived one overrides the base one
                    if(ExportFilterDescriptions.get(forClass).containsKey(f))
                    {
                        ExportFilterDescription descr = ExportFilterDescriptions.get(forClass).get(f);
                        if(extensions.contains(descr.fileextension())) // the export filter was overridden
                            continue;
                        extensions.add(descr.fileextension());
                    }
                    result.add(f);
                }
        return result;
    }    
    public static ExportFilter InstantiateExportFilter(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterNames.containsKey(c))
            {
                HashMap<String,String> filters = ExportFilterNames.get(c);
                if(filters.containsKey(identifier))
                    return (ExportFilter)InstantiateClass(filters.get(identifier));
            }
        return null;
    }
    public static ExportFilter InstantiateExportFilterByExtension(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterExtensions.containsKey(c))
            {
                HashMap<String,String> filters = ExportFilterExtensions.get(c);
                if(filters.containsKey(identifier))
                    return (ExportFilter)InstantiateClass(filters.get(identifier));
            }
        return null;
    }
    public static ExportFilterDescription getExportFilterDescription(Class<?> forClass, String identifier) throws Exception {
        for(Class c = forClass; c != null; c = c.getSuperclass())
            if(ExportFilterDescriptions.containsKey(c))
            {
                HashMap<String,ExportFilterDescription> filters = ExportFilterDescriptions.get(c);
                if(filters.containsKey(identifier))
                    return filters.get(identifier);
            }
        return null;
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
