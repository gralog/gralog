package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ComputationTreeLogicOr extends ComputationTreeLogicFormula {

    ComputationTreeLogicFormula left;
    ComputationTreeLogicFormula right;

    public ComputationTreeLogicOr(ComputationTreeLogicFormula left,
            ComputationTreeLogicFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = left.interpretation(structure);
        // union
        result.addAll(right.interpretation(structure));
        return result;
    }

}
