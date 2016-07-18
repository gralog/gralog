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
public class PropositionalLogicNot extends PropositionalLogicFormula
{
    
    public PropositionalLogicFormula subformula;
    
    public PropositionalLogicNot(PropositionalLogicFormula subformula)
    {
        this.subformula = subformula;
    }
    
    @Override
    public String toString()
    {
        String s = subformula.toString();
        if(subformula instanceof PropositionalLogicOr
        || subformula instanceof PropositionalLogicAnd)
            s = "(" + s + ")";

        return "¬" + s;
    }

    @Override
    public boolean isAClause()
    {
        return (subformula instanceof PropositionalLogicVariable);
    }
    
    @Override
    public void GetVariables(Set<String> vars)
    {
        subformula.GetVariables(vars);
    }
}
