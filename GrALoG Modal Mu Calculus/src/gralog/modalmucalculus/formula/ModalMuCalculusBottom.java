
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.modalmucalculus.structure.ParityGame;
import gralog.modalmucalculus.structure.ParityGamePosition;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Map;



public class ModalMuCalculusBottom extends ModalMuCalculusFormula
{
    public ModalMuCalculusBottom()
    {
    }
    
    @Override
    protected ModalMuCalculusFormula NegationNormalForm(boolean negated)
    {
        if(negated)
            return new ModalMuCalculusTop();
        else
            return new ModalMuCalculusBottom();
    }
    
    @Override
    protected ModalMuCalculusFormula NegateVariable(String variable)
    {
        return new ModalMuCalculusBottom();
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
            node.Label = "⊥";
            node.Player1Position = true; // verifier loses
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
