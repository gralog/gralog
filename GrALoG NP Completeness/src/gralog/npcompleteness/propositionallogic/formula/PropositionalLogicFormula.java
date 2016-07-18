/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.propositionallogic.formula;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

/**
 *
 * @author viktor
 */
abstract public class PropositionalLogicFormula 
{
    abstract boolean isAClause();

    public boolean hasConjunctiveNormalForm()
    {
        return isAClause();
    }

    public void GetClauses(Set<PropositionalLogicFormula> clauses) throws Exception // only works on cnf formulas
    {
        if(!isAClause())
            throw new Exception("Formula is not in Conjunctive Normal Form");
        clauses.add(this);
    }

    public void GetLiterals(Set<PropositionalLogicFormula> literals) // only works on cnf clauses
    {
        literals.add(this);
    }
    
    public void GetVariables(Set<String> vars)
    {
    }
    
    
    abstract protected PropositionalLogicFormula ConjunctiveNormalForm(Integer varId, HashMap<PropositionalLogicFormula, String> VarIdx);
    
    public PropositionalLogicFormula ConjunctiveNormalForm()
    {
        if(hasConjunctiveNormalForm())
            return this;
        
        HashMap<PropositionalLogicFormula, String> VarIdx = new HashMap<>();
        Integer id = 0;
        
        PropositionalLogicFormula sub = ConjunctiveNormalForm(id, VarIdx);
        
        return new PropositionalLogicAnd(sub,
            new PropositionalLogicVariable(VarIdx.get(this)));
    }
    
}
