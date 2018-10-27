/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;


import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.Vertex;

import java.util.HashSet;


/**
 *
 */

public class ComputationTreeLogicNot extends ComputationTreeLogicFormula {

    ComputationTreeLogicFormula formula;

    public ComputationTreeLogicNot(ComputationTreeLogicFormula formula) {
        this.formula = formula;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = new HashSet<>();
        HashSet<World> fresult = formula.interpretation(structure);

        // difference to set of all worlds
        for (Vertex v : structure.getVertices()) {
            if (v instanceof World) {
                if (!fresult.contains((World) v)) {
                    result.add((World) v);
                }
            }
        }

        return result;
    }

}
