/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.importfilter;

/**
 *
 * @author viktor
 */
public class GenericImportFilterParameter<T extends Object> extends ImportFilterParameters {
    
    public T parameter;
    
    public GenericImportFilterParameter() {
        this.parameter = null;
    }

    public GenericImportFilterParameter(T parameter) {
        this.parameter = parameter;
    }

}
