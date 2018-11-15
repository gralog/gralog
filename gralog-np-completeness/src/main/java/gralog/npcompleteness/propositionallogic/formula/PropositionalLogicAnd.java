/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.HashMap;
import java.util.Set;

/**
 *
 */
public class PropositionalLogicAnd extends PropositionalLogicFormula {

    public PropositionalLogicFormula left;
    public PropositionalLogicFormula right;

    public PropositionalLogicAnd(PropositionalLogicFormula left,
        PropositionalLogicFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean hasConjunctiveNormalForm() {
        return left.hasConjunctiveNormalForm()
            && right.hasConjunctiveNormalForm();
    }

    @Override
    public boolean hasConjunctiveNormalForm3() {
        return left.hasConjunctiveNormalForm3()
            && right.hasConjunctiveNormalForm3();
    }

    @Override
    public String toString() {
        String l = left.toString();
        if (left instanceof PropositionalLogicOr)
            l = "(" + l + ")";

        String r = right.toString();
        if (right instanceof PropositionalLogicOr
            || right instanceof PropositionalLogicAnd)
            r = "(" + r + ")";

        return l + " ∧ " + r;
    }

    @Override
    public boolean isAClause() {
        return false;
    }

    @Override
    public boolean isAClause3() {
        return false;
    }

    @Override
    public void getClauses(Set<PropositionalLogicFormula> clauses) throws Exception {
        left.getClauses(clauses);
        right.getClauses(clauses);
    }

    @Override
    public void getVariables(Set<String> vars) {
        left.getVariables(vars);
        right.getVariables(vars);
    }

    @Override
    protected PropositionalLogicFormula conjunctiveNormalForm(Integer varId,
        HashMap<PropositionalLogicFormula, String> varIdx) {
        String myName = "v" + varId;
        ++varId;
        varIdx.put(this, myName);
        PropositionalLogicFormula leftCnf = left.conjunctiveNormalForm(varId, varIdx);
        final String leftName = varIdx.get(left);
        PropositionalLogicFormula rightCnf = right.conjunctiveNormalForm(varId, varIdx);
        final String rightName = varIdx.get(right);

        PropositionalLogicFormula result
            = new PropositionalLogicAnd(
                new PropositionalLogicAnd(
                    new PropositionalLogicOr(
                        new PropositionalLogicOr(
                            new PropositionalLogicVariable(leftName),
                            new PropositionalLogicVariable(rightName)
                        ),
                        new PropositionalLogicNot(new PropositionalLogicVariable(myName))
                    ),
                    new PropositionalLogicOr(
                        new PropositionalLogicOr(
                            new PropositionalLogicVariable(leftName),
                            new PropositionalLogicNot(new PropositionalLogicVariable(rightName))
                        ),
                        new PropositionalLogicVariable(myName)
                    )
                ),
                new PropositionalLogicAnd(
                    new PropositionalLogicOr(
                        new PropositionalLogicOr(
                            new PropositionalLogicNot(new PropositionalLogicVariable(leftName)),
                            new PropositionalLogicVariable(rightName)
                        ),
                        new PropositionalLogicVariable(myName)
                    ),
                    new PropositionalLogicOr(
                        new PropositionalLogicOr(
                            new PropositionalLogicNot(new PropositionalLogicVariable(leftName)),
                            new PropositionalLogicNot(new PropositionalLogicVariable(rightName))
                        ),
                        new PropositionalLogicVariable(myName)
                    )
                )
            );

        if (leftCnf != null)
            result = new PropositionalLogicAnd(leftCnf, result);
        if (rightCnf != null)
            result = new PropositionalLogicAnd(rightCnf, result);
        return result;
    }
}
