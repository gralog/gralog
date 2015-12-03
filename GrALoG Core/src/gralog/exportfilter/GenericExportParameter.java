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
public class GenericExportParameter<T extends Object> extends ExportParameters {
    
    public T parameter;
    
    public GenericExportParameter() {
        this.parameter = null;
    }

    public GenericExportParameter(T parameter) {
        this.parameter = parameter;
    }

}
