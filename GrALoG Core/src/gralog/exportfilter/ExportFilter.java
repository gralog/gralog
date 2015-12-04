/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.structure.*;
import java.lang.reflect.*;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

/**
 *
 * @author viktor
 */
public abstract class ExportFilter {
    
    //public abstract void Export(Structure structure, OutputStreamWriter stream, ExportParameters params);
    
    // null means it has no parameters
    public ExportFilterParameters GetParameters(Structure structure) {
        return null;
    }
    
    public void DoExport(Structure structure, String FileName, ExportFilterParameters params) throws Exception
    {
        FileOutputStream stream = new FileOutputStream(FileName);
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        DoExport(structure, writer, params);
        writer.flush();
        writer.close();
    }
    
    public void DoExport(Structure structure, OutputStreamWriter stream, ExportFilterParameters params) throws Exception
    {
        Method[] methods = this.getClass().getMethods();
        for(Method method : methods)
        {
            if(!method.getName().equals("Export"))
                continue;
            Class[] paramTypes = method.getParameterTypes();
            if(paramTypes.length != 3)
                continue;
                
            method.invoke(this, new Object[]{structure, stream, params});
            break;
        }
    }
    
    public ExportFilterDescription getDescription() throws Exception {
        if(!this.getClass().isAnnotationPresent(ExportFilterDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @ExportFilterDescription Annotation");
        return this.getClass().getAnnotation(ExportFilterDescription.class);
    }
    
}
