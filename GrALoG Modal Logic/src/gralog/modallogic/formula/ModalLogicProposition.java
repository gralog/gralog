
package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;

public class ModalLogicProposition extends ModalLogicFormula
{
    String proposition;
    
    public ModalLogicProposition(String proposition)
    {
        this.proposition = proposition;
    }
    
    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> result = new HashSet<World>();
        for(Vertex v : structure.getVertices())
            if(v instanceof World)
            {
                World w = (World)v;
                if(w.SatisfiesProposition(this.proposition))
                    result.add(w);
            }
        return result;
    }

}
