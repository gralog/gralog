
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;



public class ModalMuCalculusDiamond extends ModalMuCalculusFormula
{
    String transitiontype;
    ModalMuCalculusFormula subformula;
    
    public ModalMuCalculusDiamond(ModalMuCalculusFormula subformula)
    {
        this(null, subformula);
    }
    
    public ModalMuCalculusDiamond(String transitiontype, ModalMuCalculusFormula subformula)
    {
        this.transitiontype = transitiontype;
        this.subformula = subformula;
    }
}

