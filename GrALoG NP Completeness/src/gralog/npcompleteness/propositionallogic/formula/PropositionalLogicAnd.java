/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author viktor
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
        if (right instanceof PropositionalLogicOr)
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
            HashMap<PropositionalLogicFormula, String> VarIdx) {
        String myName = "v" + varId;
        varId++;
        VarIdx.put(this, myName);
        PropositionalLogicFormula leftCnf = left.conjunctiveNormalForm(varId, VarIdx);
        String leftName = VarIdx.get(left);
        PropositionalLogicFormula rightCnf = right.conjunctiveNormalForm(varId, VarIdx);
        String rightName = VarIdx.get(right);

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
