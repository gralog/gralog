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
public class StringImportParameter extends ImportParameters {
    
    public String parameter;
    
    public StringImportParameter() {
        this.parameter = "";
    }

    public StringImportParameter(String parameter) {
        this.parameter = parameter;
    }

}
