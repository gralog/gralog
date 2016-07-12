
package gralog.modalmucalculus.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.Action;
import gralog.modallogic.World;
import gralog.structure.*;
import java.util.HashSet;



public class ModalMuCalculusBox extends ModalMuCalculusFormula
{
    String transitiontype;
    ModalMuCalculusFormula subformula;

    public ModalMuCalculusBox(ModalMuCalculusFormula subformula)
    {
        this(null, subformula);
    }

    public ModalMuCalculusBox(String transitiontype, ModalMuCalculusFormula subformula)
    {
        this.transitiontype = transitiontype;
        this.subformula = subformula;
    }
}

