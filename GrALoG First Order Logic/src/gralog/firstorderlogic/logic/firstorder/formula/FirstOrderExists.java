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
public class FirstOrderExists extends FirstOrderFormula {
    
    String variable;
    FirstOrderFormula subformula1;
    
    public FirstOrderExists(String variable, FirstOrderFormula subformula1)
    {
        this.variable = variable;
        this.subformula1 = subformula1;
    }

    @Override
    public String toString()
    {
        return "EXISTS " + variable + " . (" + subformula1.toString() + ")";
    }

}
