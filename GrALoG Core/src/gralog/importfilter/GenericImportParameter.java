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
public class GenericImportParameter<T extends Object> extends ImportParameters {
    
    public T parameter;
    
    public GenericImportParameter() {
        this.parameter = null;
    }

    public GenericImportParameter(T parameter) {
        this.parameter = parameter;
    }

}
