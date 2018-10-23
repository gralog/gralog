/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

/**
 *
 */

public class ComputationTreeLogicForwarder extends ComputationTreeLogicFormula {

    ComputationTreeLogicFormula formula;

    public ComputationTreeLogicForwarder(ComputationTreeLogicFormula formula) {
        this.formula = formula;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        return formula.interpretation(structure);
    }
}
