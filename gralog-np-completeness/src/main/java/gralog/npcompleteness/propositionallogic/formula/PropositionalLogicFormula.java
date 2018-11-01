/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.Set;
import java.util.HashMap;

/**
 *
 */
public abstract class PropositionalLogicFormula {

    public boolean isLiteral() {
        return false;
    }

    abstract boolean isAClause();

    public boolean hasConjunctiveNormalForm() {
        return isAClause();
    }

    abstract boolean isAClause3();

    public boolean hasConjunctiveNormalForm3() {
        return isAClause3();
    }

    // only works on cnf formulas
    public void getClauses(Set<PropositionalLogicFormula> clauses) throws Exception {
        if (!isAClause())
            throw new Exception("Formula is not in Conjunctive Normal Form");
        clauses.add(this);
    }

    // only works on cnf clauses
    public void getLiterals(Set<PropositionalLogicFormula> literals) {
        literals.add(this);
    }

    public void getVariables(Set<String> vars) {
    }

    protected abstract PropositionalLogicFormula conjunctiveNormalForm(
        Integer varId, HashMap<PropositionalLogicFormula, String> varIdx);

    public PropositionalLogicFormula conjunctiveNormalForm() {
        if (hasConjunctiveNormalForm())
            return this;
        return conjunctiveNormalForm3();
    }

    public PropositionalLogicFormula conjunctiveNormalForm3() {
        if (hasConjunctiveNormalForm3())
            return this;

        HashMap<PropositionalLogicFormula, String> varIdx = new HashMap<>();
        Integer id = 0;

        PropositionalLogicFormula sub = conjunctiveNormalForm(id, varIdx);

        return new PropositionalLogicAnd(
            sub,
            new PropositionalLogicVariable(varIdx.get(this)));
    }
}
