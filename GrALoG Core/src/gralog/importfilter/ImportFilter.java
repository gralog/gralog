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
public class ImportFilter {
    
    // null means it has no parameters
    public ImportParameters GetParameters() {
        return null;
    }
    
    public Structure Import(String FileName, ImportParameters params) throws Exception
    {
        FileInputStream stream = new FileInputStream(FileName);
        return Import(stream, params);
    }
    
    public Structure Import(InputStream stream, ImportParameters params) throws Exception
    {
        throw new Exception("class " + this.getClass().getName() + " has no Stream-based Import Method");
    }
    
}
