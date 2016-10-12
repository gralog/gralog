/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 *
 */
abstract public class PropositionalLogicFormula {

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

    public void getClauses(Set<PropositionalLogicFormula> clauses) throws Exception // only works on cnf formulas
    {
        if (!isAClause())
            throw new Exception("Formula is not in Conjunctive Normal Form");
        clauses.add(this);
    }

    public void getLiterals(Set<PropositionalLogicFormula> literals) // only works on cnf clauses
    {
        literals.add(this);
    }

    public void getVariables(Set<String> vars) {
    }

    abstract protected PropositionalLogicFormula conjunctiveNormalForm(
            Integer varId, HashMap<PropositionalLogicFormula, String> VarIdx);

    public PropositionalLogicFormula conjunctiveNormalForm() {
        if (hasConjunctiveNormalForm())
            return this;
        return conjunctiveNormalForm3();
    }

    public PropositionalLogicFormula conjunctiveNormalForm3() {
        if (hasConjunctiveNormalForm3())
            return this;

        HashMap<PropositionalLogicFormula, String> VarIdx = new HashMap<>();
        Integer id = 0;

        PropositionalLogicFormula sub = conjunctiveNormalForm(id, VarIdx);

        return new PropositionalLogicAnd(
                sub,
                new PropositionalLogicVariable(VarIdx.get(this)));
    }
}
