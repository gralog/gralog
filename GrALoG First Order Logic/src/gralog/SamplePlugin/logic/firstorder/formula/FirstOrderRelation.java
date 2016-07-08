/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.SamplePlugin.logic.firstorder.formula;

import java.util.Vector;

/**
 *
 * @author viktor
 */
public class FirstOrderRelation extends FirstOrderFormula {
    
    String relation;
    Vector<String> parameters;
    
    public FirstOrderRelation(String relation, Vector<String> parameters)
    {
        this.relation = relation;
        this.parameters = parameters;
    }
 
    @Override
    public String toString()
    {
        String result = "";
        String glue = "";
        
        for(String p : parameters)
        {
            result += glue + p;
            glue = ",";
        }
        
        return relation + "(" + result + ")";
    }
}
