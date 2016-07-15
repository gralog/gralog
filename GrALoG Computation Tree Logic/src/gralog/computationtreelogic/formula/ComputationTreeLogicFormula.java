package gralog.computationtreelogic.formula;

import gralog.modallogic.*;
import java.util.HashSet;

/**
 * Base class for all formulas.
 * @author viktor
 *
 */
public abstract class ComputationTreeLogicFormula
{
    public ComputationTreeLogicFormula()
    {
    }
	
    abstract public HashSet<World> Interpretation(KripkeStructure structure);
}
