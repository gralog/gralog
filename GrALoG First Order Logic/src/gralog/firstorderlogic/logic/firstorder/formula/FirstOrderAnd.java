/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

/**
 *
 * @author Hv
 */
public class FirstOrderAnd extends FirstOrderFormula {
    
    FirstOrderFormula subformula1;
    FirstOrderFormula subformula2;
    
    public FirstOrderAnd(FirstOrderFormula subformula1, FirstOrderFormula subformula2)
    {
        this.subformula1 = subformula1;
        this.subformula2 = subformula2;
    }

    @Override
    public String toString()
    {
        return "(" + subformula1.toString() + " AND " + subformula2.toString() + ")";
    }
}
