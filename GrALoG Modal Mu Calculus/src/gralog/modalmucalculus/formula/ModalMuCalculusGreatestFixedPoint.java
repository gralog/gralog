
package gralog.modalmucalculus.formula;

import gralog.structure.*;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;



public class ModalMuCalculusGreatestFixedPoint extends ModalMuCalculusFormula
{
    ModalMuCalculusFormula formula;
    String variable;
    
    public ModalMuCalculusGreatestFixedPoint(String variable, ModalMuCalculusFormula formula)
    {
        this.variable = variable;
        this.formula = formula;
    }
}
