/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.rendering.GralogColor;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author viktor
 */
public class FirstOrderForall extends FirstOrderFormula {

    String variable;
    FirstOrderFormula subformula1;
    
    public FirstOrderForall(String variable, FirstOrderFormula subformula1)
    {
        this.variable = variable;
        this.subformula1 = subformula1;
    }
    
    @Override
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception
    {
        Vertex oldvalue = varassign.get(variable);
        boolean result = true;
        
        Set<Vertex> V = s.getVertices();
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            
            GralogColor bak = v.FillColor;
            v.FillColor = GralogColor.green;
            onprogress.OnProgress(s);

            result = subformula1.Evaluate(s, varassign, onprogress);

            v.FillColor = bak;
            
            if(!result)
                break;
        }
        onprogress.OnProgress(s);
        
        varassign.remove(variable);
        if(oldvalue != null)
            varassign.put(variable, oldvalue);
        
        return result;
    }

}
