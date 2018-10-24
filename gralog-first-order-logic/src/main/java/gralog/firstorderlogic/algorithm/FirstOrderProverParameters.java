/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.algorithm;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.firstorderlogic.parser.FirstOrderSyntaxChecker;

/**
 * An extension of StringAlgorithmParameter in order to provide a custom view.
 */
public class FirstOrderProverParameters extends StringAlgorithmParameter {

    public FirstOrderProverParameters(String initialValue) {
        super("Formula", initialValue,
            new FirstOrderSyntaxChecker(), FirstOrderSyntaxChecker.explanation());
    }
}
