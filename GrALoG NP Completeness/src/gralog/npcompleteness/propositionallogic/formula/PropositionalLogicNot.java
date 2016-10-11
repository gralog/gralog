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
 */
public class PropositionalLogicNot extends PropositionalLogicFormula {

    public PropositionalLogicFormula subformula;

    public PropositionalLogicNot(PropositionalLogicFormula subformula) {
        this.subformula = subformula;
    }

    @Override
    public String toString() {
        String s = subformula.toString();
        if (subformula instanceof PropositionalLogicOr
            || subformula instanceof PropositionalLogicAnd)
            s = "(" + s + ")";

        return "¬" + s;
    }

    @Override
    public boolean isLiteral() {
        return (subformula instanceof PropositionalLogicVariable);
    }

    @Override
    public boolean isAClause() {
        return isLiteral();
    }

    @Override
    public boolean isAClause3() {
        return isLiteral();
    }

    @Override
    public void getVariables(Set<String> vars) {
        subformula.getVariables(vars);
    }

    @Override
    protected PropositionalLogicFormula conjunctiveNormalForm(Integer varId,
            HashMap<PropositionalLogicFormula, String> VarIdx) {
        String myName = "v" + varId;
        varId++;
        VarIdx.put(this, myName);
        PropositionalLogicFormula subCnf = subformula.conjunctiveNormalForm(varId, VarIdx);
        String subName = VarIdx.get(subformula);

        PropositionalLogicFormula result
                = new PropositionalLogicAnd(
                        new PropositionalLogicOr(
                                new PropositionalLogicVariable(subName),
                                new PropositionalLogicVariable(myName)
                        ),
                        new PropositionalLogicOr(
                                new PropositionalLogicNot(new PropositionalLogicVariable(subName)),
                                new PropositionalLogicNot(new PropositionalLogicVariable(myName))
                        )
                );

        if (subCnf != null)
            result = new PropositionalLogicAnd(subCnf, result);

        return result;
    }
}
