/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

import gralog.structure.*;
import java.io.InputStream;
import java.io.FileInputStream;

/**
 *
 * @author viktor
 */
abstract public class ImportFilter {
    
    // null means it has no parameters
    public ImportFilterParameters GetParameters() {
        return null;
    }
    
    public Structure Import(String FileName, ImportFilterParameters params) throws Exception
    {
        FileInputStream stream = new FileInputStream(FileName);
        return Import(stream, params);
    }
    
    public Structure Import(InputStream stream, ImportFilterParameters params) throws Exception
    {
        throw new Exception("class " + this.getClass().getName() + " has no Stream-based Import Method");
    }
    
    
    public ImportFilterDescription getDescription() throws Exception {
        if(!this.getClass().isAnnotationPresent(ImportFilterDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @ImportFilterDescription Annotation");
        return this.getClass().getAnnotation(ImportFilterDescription.class);
    }
    
}
