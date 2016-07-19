package gralog.modalmucalculus.formula;

import gralog.modallogic.*;
import gralog.modalmucalculus.structure.*;
import java.util.Map;

public abstract class ModalMuCalculusFormula
{
    public ModalMuCalculusFormula()
    {
    }
    
    public Double FormulaWidth()
    {
        return 1d;
    }

    public Double FormulaDepth()
    {
        return 1d;
    }
    
    abstract protected ModalMuCalculusFormula NegationNormalForm(boolean negated);
    public ModalMuCalculusFormula NegationNormalForm()
    {
        return NegationNormalForm(false);
    }
    
    abstract public void CreateParityGamePositions(Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception;
    
    abstract public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception;
    
}
