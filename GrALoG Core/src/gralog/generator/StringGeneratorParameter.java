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
public class StringGeneratorParameter extends GeneratorParameters {

    public String parameter;

    public StringGeneratorParameter() {
        this.parameter = "";
    }

    public StringGeneratorParameter(String parameter) {
        this.parameter = parameter;
    }
}
