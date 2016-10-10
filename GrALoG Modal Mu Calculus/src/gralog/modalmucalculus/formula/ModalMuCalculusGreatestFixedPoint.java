
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import java.util.HashMap;
import java.util.Map;



public class ModalMuCalculusGreatestFixedPoint extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula formula;
    String variable;
    
    public ModalMuCalculusGreatestFixedPoint(String variable, ModalMuCalculusFormula formula)
    {
        this.variable = variable;
        this.formula = formula;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
        {
            ModalMuCalculusFormula temp = this.NegateVariable(variable);
            return new ModalMuCalculusLeastFixedPoint(variable, temp.NegationNormalForm(negated));
        }
        else
            return new ModalMuCalculusGreatestFixedPoint(variable, formula.NegationNormalForm(negated));
    }
    
    @Override
    protected ModalMuCalculusFormula NegateVariable(String variable)
    {
        if(variable.equals(this.variable))
            return this; // don't negate in subformula, because inside the variable refers to a different one
        
        return new ModalMuCalculusGreatestFixedPoint(this.variable, formula.NegateVariable(variable));
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
    public void CreateParityGamePositions(Double scale, Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p, int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception
    {
        int MyPriority = NextPriority + NextPriority%2;
        formula.CreateParityGamePositions(scale, x, y+scale, w, h, s, p, MyPriority, index);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x);
            node.Coordinates = new Vector2D(
                    index.get((World)v).get(formula).Coordinates.getX(),
                    scale * h * v.Coordinates.getY() + y
            );
            node.Priority = MyPriority;
            node.Label = "ν" + variable;
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
