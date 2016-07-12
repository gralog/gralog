
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;



public class ModalMuCalculusOr extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula left;
    ModalMuCalculusFormula right;
    
    public ModalMuCalculusOr(ModalMuCalculusFormula left, ModalMuCalculusFormula right)
    {
        this.left = left;
        this.right = right;
    }
}
