
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusLeastFixedPoint extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula formula;
    String variable;
    
    public ModalMuCalculusLeastFixedPoint(String variable, ModalMuCalculusFormula formula)
    {
        this.variable = variable;
        this.formula = formula;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            // not sure if this is right... maybe the variable must be negated in all subformulas?
            return new ModalMuCalculusGreatestFixedPoint(variable, formula.NegationNormalForm(negated));
        else
            return new ModalMuCalculusLeastFixedPoint(variable, formula.NegationNormalForm(negated));
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
        formula.CreateParityGamePositions(x, y+1, w, h, s, p, index);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(w * v.Coordinates.get(0) + x);
            node.Coordinates.add(h * v.Coordinates.get(1) + y);
            node.Label = "μ" + variable;
            p.AddVertex(node);
            
            if(!index.containsKey((World)v))
                index.put((World)v, new HashMap<>());
            index.get((World)v).put(this, node);
        }
    }
    
    @Override
    public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception
    {
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition pv = index.get((World)v).get(this);
            ParityGamePosition ps = index.get((World)v).get(formula);
            p.AddEdge(p.CreateEdge(pv, ps));
        }

        
        ModalMuCalculusFormula olddef = variableDefinitionPoints.get(variable);
        variableDefinitionPoints.put(variable, this);
        
        formula.CreateParityGameTransitions(s, p, index, variableDefinitionPoints);
        
        variableDefinitionPoints.remove(variable);
        if(olddef != null)
            variableDefinitionPoints.put(variable, olddef);
    }
}
