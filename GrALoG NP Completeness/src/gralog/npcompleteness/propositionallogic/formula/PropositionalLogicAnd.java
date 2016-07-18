/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.propositionallogic.formula;
import java.util.Set;

/**
 *
 * @author viktor
 */
public class PropositionalLogicAnd extends PropositionalLogicFormula
{
    public PropositionalLogicFormula left;
    public PropositionalLogicFormula right;
    
    public PropositionalLogicAnd(PropositionalLogicFormula left, PropositionalLogicFormula right)
    {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString()
    {
        String l = left.toString();
        if(left instanceof PropositionalLogicOr)
            l = "(" + l + ")";

        String r = right.toString();
        if(right instanceof PropositionalLogicOr)
            r = "(" + r + ")";

        return l + " ∧ " + r;
    }
    
    @Override   
    public boolean isAClause()
    {
        return false;
    }
    
    @Override
    public void GetClauses(Set<PropositionalLogicFormula> clauses) throws Exception
    {
        left.GetClauses(clauses);
        right.GetClauses(clauses);
    }
    
    @Override
    public void GetVariables(Set<String> vars)
    {
        left.GetVariables(vars);
        right.GetVariables(vars);
    }
}
