package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

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
