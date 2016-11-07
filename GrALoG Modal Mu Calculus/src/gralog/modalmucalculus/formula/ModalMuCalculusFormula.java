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

    @Override
    public String toString() {
        return toString(FormulaPosition.Fixpoint, FormulaEndPosition.AT_END);
    }

    abstract public String toString(FormulaPosition pos, FormulaEndPosition endPos);

    // For the toString method, we track the position in the formula in order
    // to produce a string with a minimum number of parentheses.
    public enum FormulaPosition {
        OrLeft, OrRight, AndLeft, AndRight, BoxDiamondNot, Fixpoint
    }

    // We also track if we are in the middle of a disjunction/conjunction or not
    // in order to save parentheses for the quantifiers (quantifiers reach as
    // far to the right as possible).
    public enum FormulaEndPosition {
        MIDDLE, AT_END
    }
}
