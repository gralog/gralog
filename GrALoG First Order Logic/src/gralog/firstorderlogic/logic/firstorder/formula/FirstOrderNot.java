/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;


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
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign) throws Exception
    {
        return !subformula1.Evaluate(s, varassign);
    }

}
