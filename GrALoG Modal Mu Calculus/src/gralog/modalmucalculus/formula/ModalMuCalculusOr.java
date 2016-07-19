
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusOr extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula left;
    ModalMuCalculusFormula right;
    
    public ModalMuCalculusOr(ModalMuCalculusFormula left, ModalMuCalculusFormula right)
    {
        this.left = left;
        this.right = right;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            return new ModalMuCalculusAnd(left.NegationNormalForm(negated), right.NegationNormalForm(negated));
        else
            return new ModalMuCalculusOr(left.NegationNormalForm(negated), right.NegationNormalForm(negated));
    }
    
    @Override
    public Double FormulaWidth()
    {
        return left.FormulaWidth() + right.FormulaWidth() + 1;
    }

    @Override
    public Double FormulaDepth()
    {
        return Math.max(left.FormulaDepth(), right.FormulaDepth()) + 1;
    }
    
    
    
    @Override
    public void CreateParityGamePositions(Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception
    {
        Double lw = left.FormulaWidth();
        left.CreateParityGamePositions (x,      y+1, w, h, s, p, index);
        right.CreateParityGamePositions(x+lw+1, y+1, w, h, s, p, index);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(w * v.Coordinates.get(0) + x + lw + 0.5d);
            node.Coordinates.add(h * v.Coordinates.get(1) + y);
            node.Label = "∨";
            node.Player1Position = true;
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
        left.CreateParityGameTransitions(s, p, index, variableDefinitionPoints);
        right.CreateParityGameTransitions(s, p, index, variableDefinitionPoints);
        
        for(Vertex v : s.getVertices())
        {
            p.AddEdge( p.CreateEdge(index.get((World)v).get(this), index.get((World)v).get(left)) );
            p.AddEdge( p.CreateEdge(index.get((World)v).get(this), index.get((World)v).get(right)) );
        }
    }
}
