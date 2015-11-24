
package gralog.modallogic.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;



public class ModalLogicNot extends ModalLogicFormula
{
    ModalLogicFormula formula;
    
    public ModalLogicNot(ModalLogicFormula formula)
    {
        this.formula = formula;
    }
    
    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> result = new HashSet<World>();
        HashSet<World> fresult = formula.Interpretation(structure);
        
        // difference to set of all worlds
        for(Vertex v : structure.getVertices())
            if(v instanceof World)
                if(!fresult.contains((World)v))
                    result.add((World)v);
        
        return result;
    }

}
