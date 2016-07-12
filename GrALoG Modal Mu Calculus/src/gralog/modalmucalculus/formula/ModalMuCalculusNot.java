
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;



public class ModalMuCalculusNot extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula formula;
    
    public ModalMuCalculusNot(ModalMuCalculusFormula formula)
    {
        this.formula = formula;
    }
}
