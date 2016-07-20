
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.structure.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusProposition extends ModalMuCalculusFormula
{
    String proposition;
    
    public ModalMuCalculusProposition(String proposition)
    {
        this.proposition = proposition;
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            return new ModalMuCalculusNot( new ModalMuCalculusProposition(proposition));
        else
            return new ModalMuCalculusProposition(proposition);
    }

    @Override
    protected ModalMuCalculusFormula NegateVariable(String variable)
    {
        if(variable.equals(proposition))
            return new ModalMuCalculusNot(new ModalMuCalculusProposition(proposition));
        else
            return new ModalMuCalculusProposition(proposition);
    }

    @Override
    public void CreateParityGamePositions(Double scale, Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p, int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index)
    {
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(scale * w * v.Coordinates.get(0) + x);
            node.Coordinates.add(scale * h * v.Coordinates.get(1) + y);
            node.Label = proposition;
            node.Player1Position = !((World)v).SatisfiesProposition(proposition);
            p.AddVertex(node);
            
            if(!index.containsKey((World)v))
                index.put((World)v, new HashMap<>());
            index.get((World)v).put(this, node);
        }
    }
    
    @Override
    public void CreateParityGameTransitions(KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints)
    {
        if(!variableDefinitionPoints.containsKey(proposition))
            return; // simple proposition
        
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition pv = index.get((World)v).get(this);
            ParityGamePosition pdef = index.get((World)v).get(variableDefinitionPoints.get(proposition));
            p.AddEdge( p.CreateEdge(pv, pdef) );
        }
    }
}
