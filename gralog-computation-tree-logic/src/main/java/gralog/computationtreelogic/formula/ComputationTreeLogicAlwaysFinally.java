/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;

/**
 *
 */

public class ComputationTreeLogicAlwaysFinally extends ComputationTreeLogicForwarder {
    // AF phi = not EG not phi

    public ComputationTreeLogicAlwaysFinally(
        ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicNot(
            new ComputationTreeLogicExistsGlobally(
                new ComputationTreeLogicNot(subformula))));
    }
}
