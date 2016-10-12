/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modalmucalculus.formula;

import gralog.modallogic.*;
import gralog.modalmucalculus.structure.*;
import java.util.Map;

public abstract class ModalMuCalculusFormula {

    public ModalMuCalculusFormula() {
    }

    public double formulaWidth() {
        return 0d;
    }

    public double formulaDepth() {
        return 0d;
    }

    abstract protected ModalMuCalculusFormula negateVariable(String variable);

    abstract protected ModalMuCalculusFormula negationNormalForm(boolean negated);

    public ModalMuCalculusFormula NegationNormalForm() {
        return negationNormalForm(false);
    }

    abstract public void createParityGamePositions(double scale, double x,
            double y, double w, double h, KripkeStructure s, ParityGame p,
            int NextPriority,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception;

    abstract public void createParityGameTransitions(KripkeStructure s,
            ParityGame p,
            Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
            Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception;
}
