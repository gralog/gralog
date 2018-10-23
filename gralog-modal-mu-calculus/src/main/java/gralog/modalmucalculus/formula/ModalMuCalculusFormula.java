/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.formula;

import gralog.modallogic.*;
import gralog.modalmucalculus.structure.*;
import gralog.rendering.Vector2D;
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

    protected abstract ModalMuCalculusFormula negateVariable(String variable);

    protected abstract ModalMuCalculusFormula negationNormalForm(boolean negated);

    public ModalMuCalculusFormula negationNormalForm() {
        return negationNormalForm(false);
    }

    public abstract void createParityGamePositions(
        double scale, Vector2D pos, Vector2D size, KripkeStructure s,
        ParityGame p, int nextPriority,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index) throws Exception;

    public abstract void createParityGameTransitions(KripkeStructure s,
        ParityGame p,
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> index,
        Map<String, ModalMuCalculusFormula> variableDefinitionPoints) throws Exception;

    @Override
    public String toString() {
        return toString(FormulaPosition.Fixpoint, FormulaEndPosition.AT_END);
    }

    public abstract String toString(FormulaPosition pos, FormulaEndPosition endPos);

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
