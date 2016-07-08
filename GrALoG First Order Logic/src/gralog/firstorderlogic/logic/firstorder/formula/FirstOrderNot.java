/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

/**
 *
 * @author viktor
 */
public class FirstOrderNot extends FirstOrderFormula
{
    FirstOrderFormula subformula1;
    
    public FirstOrderNot(FirstOrderFormula subformula1)
    {
        this.subformula1 = subformula1;
    }
    
    @Override
    public String toString()
    {
        return "NOT (" + subformula1.toString() + ")";
    }
}
