/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;

/**
 *
 */

public class ComputationTreeLogicExistsFinally extends ComputationTreeLogicForwarder {
    // EF phi = E true U phi

    public ComputationTreeLogicExistsFinally(
        ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicExistsUntil(new ComputationTreeLogicTop(), subformula));
    }
}
