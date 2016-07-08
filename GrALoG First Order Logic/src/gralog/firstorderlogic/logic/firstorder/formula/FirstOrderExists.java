/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.Set;

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
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign) throws Exception
    {
        Vertex oldvalue = varassign.get(variable);
        boolean result = false;
        
        Set<Vertex> V = s.getVertices();
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            if(subformula1.Evaluate(s, varassign))
            {
                result = true;
                break;
            }
        }
        
        varassign.remove(variable);
        if(oldvalue != null)
            varassign.put(variable, oldvalue);
        
        return result;
    }

}
