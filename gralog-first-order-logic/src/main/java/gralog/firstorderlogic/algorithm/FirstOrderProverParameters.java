/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.algorithm;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderSyntaxChecker;

/**
 *
 */
public class FirstOrderProverParameters extends StringAlgorithmParameter {

    public FirstOrderProverParameters(String initialValue) {
        super("Formula", initialValue,
            new FirstOrderSyntaxChecker(), FirstOrderSyntaxChecker.explanation());
    }
}
