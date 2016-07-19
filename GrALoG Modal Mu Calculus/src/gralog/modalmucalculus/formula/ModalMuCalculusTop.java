
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.structure.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;



public class ModalMuCalculusTop extends ModalMuCalculusFormula
{
    public ModalMuCalculusTop()
    {
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            return new ModalMuCalculusBottom();
        else
            return new ModalMuCalculusTop();
    }
    
    @Override
    public void CreateParityGamePositions(Double x, Double y, Double w, Double h,
            KripkeStructure s, ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index)
    {
        for(Vertex v : s.getVertices())
        {
            ParityGamePosition node = p.CreateVertex();
            node.Coordinates.add(w * v.Coordinates.get(0) + x);
            node.Coordinates.add(h * v.Coordinates.get(1) + y);
            node.Label = "⊤";
            node.Player1Position = false; // verifier wins
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
        
    }
}
