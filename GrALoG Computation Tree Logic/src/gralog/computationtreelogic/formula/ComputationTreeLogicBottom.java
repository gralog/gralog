package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ComputationTreeLogicBottom extends ComputationTreeLogicFormula {

    public ComputationTreeLogicBottom() {
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        return new HashSet<>();
    }

}
