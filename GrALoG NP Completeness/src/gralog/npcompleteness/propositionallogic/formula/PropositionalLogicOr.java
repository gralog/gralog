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
public class PropositionalLogicOr extends PropositionalLogicFormula {

    public PropositionalLogicFormula left;
    public PropositionalLogicFormula right;

    public PropositionalLogicOr(PropositionalLogicFormula left,
            PropositionalLogicFormula right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return left.toString() + " ∨ " + right.toString();
    }

    @Override
    public boolean isAClause() {
        return left.isAClause() && right.isAClause();
    }

    @Override
    public boolean isAClause3() {
        if (!isAClause())
            return false;

        if (left instanceof PropositionalLogicOr) {
            PropositionalLogicOr lOr = (PropositionalLogicOr) left;
            return right.isLiteral() && lOr.left.isLiteral() && lOr.right.isLiteral();
        }

        if (right instanceof PropositionalLogicOr) {
            PropositionalLogicOr rOr = (PropositionalLogicOr) right;
            return left.isLiteral() && rOr.left.isLiteral() && rOr.right.isLiteral();
        }

        return left.isLiteral() && right.isLiteral(); // 2-SAT
    }

    @Override
    public void getLiterals(Set<PropositionalLogicFormula> literals) {
        left.getLiterals(literals);
        right.getLiterals(literals);
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
                                        new PropositionalLogicNot(new PropositionalLogicVariable(myName))
                                )
                        ),
                        new PropositionalLogicAnd(
                                new PropositionalLogicOr(
                                        new PropositionalLogicOr(
                                                new PropositionalLogicNot(new PropositionalLogicVariable(leftName)),
                                                new PropositionalLogicVariable(rightName)
                                        ),
                                        new PropositionalLogicNot(new PropositionalLogicVariable(myName))
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
