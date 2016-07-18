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
public class PropositionalLogicOr extends PropositionalLogicFormula
{
    public PropositionalLogicFormula left;
    public PropositionalLogicFormula right;
    
    public PropositionalLogicOr(PropositionalLogicFormula left, PropositionalLogicFormula right)
    {
        this.left = left;
        this.right = right;
    }
    
    @Override
    public String toString()
    {
        return left.toString() + " ∨ " + right.toString();
    }

    @Override
    public boolean isAClause()
    {
        return left.isAClause() && right.isAClause();
    }
    
    @Override
    public void GetLiterals(Set<PropositionalLogicFormula> literals)
    {
        left.GetLiterals(literals);
        right.GetLiterals(literals);
    }
    
    @Override
    public void GetVariables(Set<String> vars)
    {
        left.GetVariables(vars);
        right.GetVariables(vars);
    }
}
