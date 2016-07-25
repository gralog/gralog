/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;


import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;

/**
 *
 * @author Hv
 */
public class FirstOrderOr extends FirstOrderFormula
{
    FirstOrderFormula subformula1;
    FirstOrderFormula subformula2;
    
    public FirstOrderOr(FirstOrderFormula subformula1, FirstOrderFormula subformula2)
    {
        this.subformula1 = subformula1;
        this.subformula2 = subformula2;
    }
     @Override
    public String toString()
    {
        String or="\u2228";
        return "(" + subformula1.toString() + or + subformula2.toString() + ")";
    }
    @Override
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception
    {
        if(subformula1.Evaluate(s, varassign, onprogress))
            return true;
        return subformula2.Evaluate(s, varassign, onprogress);
    }
    @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign,ProgressHandler onprogress) throws Exception
    {
        
       Bag b=new Bag();
       Bag sep=new Bag();
       sep.caption="OR";
     String assignment=new String();
        for(String str : varassign.keySet()){
            assignment+=" [ " + str + " | " + varassign.get(str).Label + " ] ";
        }
       b.ChildBags.add(sep); 
       Bag b1=subformula1.EvaluateProver(s, varassign,onprogress);
       b1.caption=assignment+subformula1.toString();
        
        sep.ChildBags.add(b1);
        if(subformula1.Evaluate(s, varassign,onprogress)){
            sep.Nodes.addAll(b1.Nodes);
        }
        
        Bag b2=subformula2.EvaluateProver(s, varassign,onprogress);
        b2.caption=assignment+subformula2.toString();
        sep.ChildBags.add(b2);
         if(subformula2.Evaluate(s, varassign,onprogress)){
            sep.Nodes.addAll(b2.Nodes);
        }
         b.Nodes.addAll(sep.Nodes);
        return b;
    }
    
    @Override
    public FiniteGamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,FiniteGame game,
        CoordinateClass coor) {
        
        FiniteGamePosition parent=new FiniteGamePosition();
        String phi="\u2205";
         String or="\u2228";
        parent.Label= "(" + subformula1.toString() + or + subformula2.toString() + ")";
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
    
        //or : player 0 position;
        parent.Player1Position=false;
        parent.Coordinates.add(coor.x);
        parent.Coordinates.add(coor.y);
        game.AddVertex(parent);
        CoordinateClass temp=new CoordinateClass();
        temp.x=coor.x+7;
        temp.y=coor.y;
           FiniteGamePosition c1=subformula1.ConstructGameGraph(s, varassign, game,temp);
        coor.y=temp.y+1;
        game.AddVertex(c1);
        game.AddEdge(game.CreateEdge(parent,c1));
        temp.x=coor.x+7;
        temp.y=coor.y;
        FiniteGamePosition c2=subformula2.ConstructGameGraph(s, varassign, game,temp);
        coor.y=temp.y+1;
        game.AddVertex(c2);
        game.AddEdge(game.CreateEdge(parent,c2));
     
        return parent;
    }

    @Override
    public Set<String> Variables() throws Exception {
         Set<String> result=new HashSet<>();
        result.addAll(subformula1.Variables());
        result.addAll(subformula2.Variables());
        return result;
    }

    @Override
    public String Substitute(HashMap<String, String> replace) throws Exception {
        return subformula1.Substitute(replace) + " \\vee "  + subformula1.Substitute(replace);
    }
    
}
