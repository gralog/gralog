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
public class PropositionalLogicVariable extends PropositionalLogicFormula {

    public String variable;

    public PropositionalLogicVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return variable;
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    @Override
    public boolean isAClause() {
        return true;
    }

    @Override
    public boolean isAClause3() {
        return true;
    }

    @Override
    public void getVariables(Set<String> vars) {
        vars.add(variable);
    }

    @Override
    protected PropositionalLogicFormula conjunctiveNormalForm(Integer varId,
            HashMap<PropositionalLogicFormula, String> VarIdx) {
        varId++;
        VarIdx.put(this, variable);
        return null;
    }
}
