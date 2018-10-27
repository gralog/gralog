/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.HashMap;
import java.util.Set;

/**
 *
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
        HashMap<PropositionalLogicFormula, String> varIdx) {
        varId++;
        varIdx.put(this, variable);
        return null;
    }
}
