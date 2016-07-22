/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;


import gralog.firstorderlogic.prover.TreeDecomposition.*;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import gralog.firstorderlogic.structure.*;
import java.util.HashSet;
import java.util.Map;

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
    @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign,ProgressHandler onprogress) throws Exception
    {
        
        String glue = "";
        
        Bag b=new Bag();
        Bag t=new Bag();
        t.caption=relation + "(";
       
        
        for(int i=0;i<parameters.size();i++)
        {
            
           Vertex v=varassign.get(parameters.get(i));
           
            
           t.caption += glue + v.Label;
           glue = ",";
           
        }
        t.caption+=")";
        
           Boolean res;
            res=Evaluate(s,varassign,onprogress);
            if(res){
                for(int i=0;i<parameters.size();i++)
                {
                    Vertex v=varassign.get(parameters.get(i));
                    t.Nodes.add(v);

                }
            }
                
           
        b.Nodes.addAll(t.Nodes);
        b.ChildBags.add(t);
        return b;
    }

       @Override
    public GamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,GameGraph game,
            Double x, Double y) {
        
         GamePosition parent=new GamePosition();
         parent.Coordinates.add(x);
         parent.Coordinates.add(y);
       y=y+2;
         
        String phi="\u2205";
         parent.Label=this.toString()+ ", { ";
         if(varassign.isEmpty()){
            parent.Label+= phi;
        }
        else{
               String glue="";
                for (Map.Entry<String,Vertex> entry : varassign.entrySet()) {
                    String key = entry.getKey();
                    Vertex value = entry.getValue();
                    parent.Label+= glue+ "(" + key +"," +value.Label + ")";
                    glue=",";
                }
             
             
         
        }
        parent.Label+= " }";
         return parent;
    }

    @Override
    public Set<String> Variables() throws Exception {
         Set<String> result=new HashSet<>();
        for(String s: parameters){
            result.add(s);
        }
        return result;
    }

      @Override
    public String Substitute(HashMap<String, String> replace) throws Exception {
        for(int i=0;i<parameters.size();i++ ){
            if(replace.containsKey(parameters.get(i) ) ){
               parameters.setElementAt(replace.get(parameters.get(i)), i) ;
            }
        }
        return toString();
                
    }

}
