/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.generator;

/**
 *
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
