/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.formula;

import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import java.util.HashSet;

public class ModalLogicOr extends ModalLogicFormula {

    ModalLogicFormula left;
    ModalLogicFormula right;

    public ModalLogicOr(ModalLogicFormula left, ModalLogicFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public HashSet<World> interpretation(KripkeStructure structure) {
        HashSet<World> result = left.interpretation(structure);
        // union
        result.addAll(right.interpretation(structure));
        return result;
    }

    @Override
    public String toString(FormulaPosition pos) {
        if (pos == FormulaPosition.BoxDiamondNot || pos == FormulaPosition.And)
            return "(" + left.toString(FormulaPosition.Or) + " ∨ "
                + right.toString(FormulaPosition.Or) + ")";
        return left.toString(FormulaPosition.Or) + " ∨ "
            + right.toString(FormulaPosition.Or);
    }
}
