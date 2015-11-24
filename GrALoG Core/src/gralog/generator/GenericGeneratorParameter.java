/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.generator;

/**
 *
 * @author viktor
 */
public class GenericGeneratorParameter<T extends Object> extends GeneratorParameters {
    
    public T parameter;
    
    public GenericGeneratorParameter() {
        this.parameter = null;
    }

    public GenericGeneratorParameter(T parameter) {
        this.parameter = parameter;
    }

}
