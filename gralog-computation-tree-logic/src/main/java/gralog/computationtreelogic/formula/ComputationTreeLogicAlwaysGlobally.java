/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.computationtreelogic.formula;

public class ComputationTreeLogicAlwaysGlobally extends ComputationTreeLogicForwarder {
    // AG phi = not EF not phi

    public ComputationTreeLogicAlwaysGlobally(
        ComputationTreeLogicFormula subformula) {
        super(new ComputationTreeLogicNot(
            new ComputationTreeLogicExistsFinally(
                new ComputationTreeLogicNot(subformula)
            )));
    }
}
