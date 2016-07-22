/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.progresshandler.ProgressHandler;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import gralog.rendering.GralogColor;
import java.util.HashMap;
import java.util.Set;
import gralog.firstorderlogic.structure.*;
import java.util.HashSet;
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
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception
    {
        Vertex oldvalue = varassign.get(variable);
        boolean result = false;
        
        Set<Vertex> V = s.getVertices();
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            
            GralogColor bak = v.FillColor;
            v.FillColor = GralogColor.red;
            onprogress.OnProgress(s);
            
            result = subformula1.Evaluate(s, varassign, onprogress);
            
            v.FillColor = bak;

            if(result)
                break;
        }
        onprogress.OnProgress(s);
        
        varassign.remove(variable);
        if(oldvalue != null)
            varassign.put(variable, oldvalue);
        
        return result;
    }
    @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign,ProgressHandler onprogress) throws Exception
    {   
        
        Vertex oldvalue = varassign.get(variable);
        Bag b=new Bag();
        Set<Vertex> V = s.getVertices();
        String assignment=new String();
        for(String str : varassign.keySet()){
            assignment+=" [ " + str + " | " + varassign.get(str).Label + " ] ";
        }
        
        for(Vertex v : V)
        {
            varassign.put(variable, v);
            Bag t=subformula1.EvaluateProver(s, varassign,onprogress);
            Boolean res=subformula1.Evaluate(s, varassign,onprogress);
            if(res){
                
                b.Nodes.add(v);
            }
            t.caption=assignment + " [ " + variable + " | " + v.Label + " ] " + subformula1.toString();

            
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
        Vertex oldvalue = varassign.get(variable);
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

    @Override
    public Set<String> Variables() throws Exception {
        Set<String> result=new HashSet<>();
        result.add(variable);
        result.addAll(subformula1.Variables());
        return result;
    }

     @Override
    public String Substitute(HashMap<String,String> replace) throws Exception {
        if(replace.containsKey(variable)){
            variable=replace.get(variable);
        }
       
        return "\\exists " + variable + " . " + subformula1.Substitute(replace);
    }

}
