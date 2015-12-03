/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.structure.*;

/**
 *
 * @author viktor
 */
public abstract class ExportFilter {
    
    //public abstract void Export(StreamWriter stream, Structure structure, ExportParameters params);
    
    // null means it has no parameters
    public ExportParameters GetParameters(Structure structure) {
        return null;
    }
    
}
