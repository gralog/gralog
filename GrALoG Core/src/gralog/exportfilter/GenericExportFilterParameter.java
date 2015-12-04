/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

/**
 *
 * @author viktor
 */
public class GenericExportFilterParameter<T extends Object> extends ExportFilterParameters {
    
    public T parameter;
    
    public GenericExportFilterParameter() {
        this.parameter = null;
    }

    public GenericExportFilterParameter(T parameter) {
        this.parameter = parameter;
    }

}
