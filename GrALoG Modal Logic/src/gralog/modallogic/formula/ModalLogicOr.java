
package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;



public class ModalLogicOr extends ModalLogicFormula
{
    ModalLogicFormula left;
    ModalLogicFormula right;
    
    public ModalLogicOr(ModalLogicFormula left, ModalLogicFormula right)
    {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public HashSet<World> Interpretation(KripkeStructure structure)
    {
        HashSet<World> result = left.Interpretation(structure);
        // union
        result.addAll(right.Interpretation(structure));
        return result;
    }

}
