/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Set;
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
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception
    {
        switch(parameters.size())
        {
            case 1:
                Vertex v = varassign.get(parameters.get(0));
                return relation.equals(v.Label);
                
            case 2:
            
                Vertex from = varassign.get(parameters.get(0));
                Vertex to = varassign.get(parameters.get(1));

                Set<Edge> E = from.getConnectedEdges();
                for(Edge e : E)
                {
                    if(  e.getSource() == from && e.getTarget() == to // same direction
                    || (!e.isDirected) && e.getSource() == to && e.getTarget() == from) // opposite direction, but undirected edge
                    {
                        if(relation.equals("E") // generic query - matches any edge!
                        || relation.equals(e.Label)) // specific query - matches current edge?
                            return true;
                    }
                }
                break;
                
            default:
                throw new Exception("cannot evaluate relation with 0 or >2 parameters");
        }
        
        return false;
    }
}
