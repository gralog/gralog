/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.Set;
import gralog.firstorderlogic.structure.*;
import java.util.Map;

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
        String exists="\u2203";
        return exists + variable + "  (" + subformula1.toString() + ")";
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
    @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign) throws Exception
    {   
        
        Vertex oldvalue = varassign.get(variable);
        Bag b=new Bag();
        Set<Vertex> V = s.getVertices();
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            Bag t=subformula1.EvaluateProver(s, varassign);
            Boolean res=subformula1.Evaluate(s, varassign);
            if(res){
                t.Nodes.add(v);
                b.Nodes.add(v);
            }
            t.caption=" [ " + variable + " | " + v.Label + " ] " + subformula1.toString();

            
            b.ChildBags.add(t);
        }
       
        varassign.remove(variable);
        if(oldvalue != null)
            varassign.put(variable, oldvalue);
        
        return b;
    }
    
    @Override
    public GamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,GameGraph game,
            Double x, Double y) {
        // verifier move
         System.out.println("in exists ");
      
        Vertex oldvalue = varassign.get(variable);
        //create parent 
         GamePosition parent=new GamePosition();
        String phi="\u2205";
        String exists="\u2203";
        parent.Label="( "+ exists + variable + "  (" + subformula1.toString() + ")";
        parent.Label += " , { ";
        
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
       
        //exists : player 0 position;
        parent.Player1Position=false;
        
        parent.Coordinates.add(x);
        parent.Coordinates.add(y);
        
        game.AddVertex(parent);
        
        Set<Vertex> V = s.getVertices();
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            GamePosition gp=subformula1.ConstructGameGraph(s,varassign,game, x+7, y);
            y = y+2;
            System.out.println("in exists " + toString());  
            System.out.println("y= "+ y) ;
            game.AddVertex(gp);
            game.AddEdge(game.CreateEdge(parent,gp));
            //set label for this vertex
            gp.Label="(" + subformula1.toString()+ ") , { ";
            String glue="";
                for (Map.Entry<String,Vertex> entry : varassign.entrySet()) {
                    String key = entry.getKey();
                    Vertex value = entry.getValue();
                    gp.Label+= glue+ "(" + key +"," +value.Label + ")";
                    glue=",";
                }
             
             gp.Label+= " }";
             
            
        } 
         varassign.remove(variable);
        if(oldvalue != null)
            varassign.put(variable, oldvalue);
    
        return parent;
    }

}
