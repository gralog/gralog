
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.rendering.Vector2D;
import gralog.structure.Vertex;
import java.util.HashMap;
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
    protected ModalMuCalculusFormula NegateVariable(String variable)
    {
        return new ModalMuCalculusOr(left.NegateVariable(variable),
                                      right.NegateVariable(variable));
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
    public void CreateParityGamePositions(Double scale, Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p, int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception
    {
        Double lw = left.FormulaWidth();
        left.CreateParityGamePositions (scale, x,                y+scale, w, h, s, p, NextPriority, index);
        right.CreateParityGamePositions(scale, x+scale*lw+scale, y+scale, w, h, s, p, NextPriority, index);
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            //node.Coordinates.add(scale * w * v.Coordinates.get(0) + x + scale*(lw + 0.5d));
            node.Coordinates = new Vector2D(
                    (
                        index.get((World)v).get(left).Coordinates.getX() +
                        index.get((World)v).get(right).Coordinates.getX()
                    ) / 2d,
                scale * h * v.Coordinates.getY() + y
            );
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
