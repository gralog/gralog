
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusNot extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula formula;
    
    public ModalMuCalculusNot(ModalMuCalculusFormula formula)
    {
        this.formula = formula;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        return formula.NegationNormalForm(!negated);
    }
    
    @Override
    public Double FormulaWidth()
    {
        return formula.FormulaWidth();
    }

    @Override
    public Double FormulaDepth()
    {
        return formula.FormulaDepth() + 1;
    }
    
    
    @Override
    public void CreateParityGamePositions(Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception
    {
        // Parity game can only be constructed from NNF formulas.
        if(!(formula instanceof ModalMuCalculusProposition))
            throw new Exception("Formula is not in Negation Normal Form");
        ModalMuCalculusProposition prop = (ModalMuCalculusProposition)formula;
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(w * v.Coordinates.get(0) + x);
            node.Coordinates.add(h * v.Coordinates.get(1) + y);
            node.Label = prop.proposition;
            node.Player1Position = ((World)v).SatisfiesProposition(prop.proposition);
            p.AddVertex(node);
            
            if(!index.containsKey((World)v))
                index.put((World)v, new HashMap<>());
            index.get((World)v).put(this, node);
        }
        formula.CreateParityGamePositions(x, y, w, h, s, p, index);
    }
    
    @Override
    public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception
    {
        if(!(formula instanceof ModalMuCalculusProposition))
            throw new Exception("Formula is not in Negation Normal Form");
        
        ModalMuCalculusProposition prop = (ModalMuCalculusProposition)formula;
        if(variableDefinitionPoints.containsKey(prop.proposition))
            throw new Exception("Formula contains bound variable \"" + prop.proposition + "\" negatively");
    }
}
