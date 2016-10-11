package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;

public class ComputationTreeLogicExistsNext extends ComputationTreeLogicFormula {

    ComputationTreeLogicFormula subformula;

    public ComputationTreeLogicExistsNext(ComputationTreeLogicFormula subformula) {
        this.subformula = subformula;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = new HashSet<>();
        HashSet<World> subresult = subformula.interpretation(structure);

        // the result is the set of predecessors of subresult
        for (World w : subresult)
            for (Edge e : w.getConnectedEdges())
                if (e.getTarget() == w)
                    result.add((World) e.getSource());

        return result;
    }
}
