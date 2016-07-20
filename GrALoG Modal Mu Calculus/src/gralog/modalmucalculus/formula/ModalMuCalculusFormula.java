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
        return 0d;
    }

    public Double FormulaDepth()
    {
        return 0d;
    }
    
    abstract protected ModalMuCalculusFormula NegateVariable(String variable);
    abstract protected ModalMuCalculusFormula NegationNormalForm(boolean negated);
    public ModalMuCalculusFormula NegationNormalForm()
    {
        return NegationNormalForm(false);
    }
    
    abstract public void CreateParityGamePositions(Double scale, Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p, int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception;
    
    abstract public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception;
    
}
