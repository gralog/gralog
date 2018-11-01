/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.Edge;

import java.util.HashSet;

/**
 *
 */

public class ComputationTreeLogicExistsUntil extends ComputationTreeLogicFormula {

    ComputationTreeLogicFormula before;
    ComputationTreeLogicFormula after;

    public ComputationTreeLogicExistsUntil(ComputationTreeLogicFormula before,
        ComputationTreeLogicFormula after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> beforeresult = before.interpretation(structure);
        HashSet<World> result = after.interpretation(structure);

        HashSet<World> lastIteration = new HashSet<>();
        lastIteration.addAll(result);
        HashSet<World> nextIteration = new HashSet<>();

        // iteratively add those worlds to the result, that
        // a) satisfy <before> AND
        // b) have sucessors inside the current <result> set
        while (!lastIteration.isEmpty()) {
            for (World l : lastIteration) {
                for (Edge e : l.getIncidentEdges()) {
                    if (e.getTarget() == l) {
                        if (beforeresult.contains((World) e.getSource())
                                && !result.contains((World) e.getSource())) {
                            nextIteration.add((World) e.getSource());
                        }
                    }
                }
            }

            result.addAll(nextIteration);
            HashSet<World> temp = lastIteration;
            lastIteration = nextIteration;
            nextIteration = temp;
            nextIteration.clear();
        }

        return result;
    }

}
