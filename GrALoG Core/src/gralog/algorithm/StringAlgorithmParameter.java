/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.algorithm;

/**
 *
 */
public class StringAlgorithmParameter extends AlgorithmParameters {

    public String parameter;

    public StringAlgorithmParameter() {
        this.parameter = "";
    }

    public StringAlgorithmParameter(String parameter) {
        this.parameter = parameter;
    }
}
