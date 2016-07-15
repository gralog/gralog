
package gralog.computationtreelogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;



public class ComputationTreeLogicTop extends ComputationTreeLogicFormula
{
    public ComputationTreeLogicTop()
    {
    }
    
    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> result = new HashSet<World>();
        for(Vertex v : structure.getVertices())
            if(v instanceof World)
                result.add((World)v);
        return result;
    }

}
