package gralog.computationtreelogic.formula;

import gralog.modallogic.*;
import java.util.HashSet;

/**
 * Base class for all formulas.
 *
 *
 */
public abstract class ComputationTreeLogicFormula {

    public ComputationTreeLogicFormula() {
    }

    abstract public HashSet<World> interpretation(KripkeStructure structure);
}
