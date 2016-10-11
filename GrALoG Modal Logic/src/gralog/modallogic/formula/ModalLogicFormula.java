package gralog.modallogic.formula;

import gralog.modallogic.*;
import java.util.HashSet;

/**
 * Base class for all formulas.
 *
 *
 */
public abstract class ModalLogicFormula {

    public ModalLogicFormula() {
    }

    abstract public HashSet<World> interpretation(KripkeStructure structure);
}
