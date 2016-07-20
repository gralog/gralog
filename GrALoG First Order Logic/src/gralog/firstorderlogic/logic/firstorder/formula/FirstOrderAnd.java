/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.firstorderlogic.structure.*;
import java.util.Map;
import java.util.Set;
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
        String and="\u2227";
        return "(" + subformula1.toString() + and + subformula2.toString() + ")";
    }
    @Override
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign) throws Exception
    {
        if(!subformula1.Evaluate(s, varassign))
            return false;
        return subformula2.Evaluate(s, varassign);
    }
        @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign) throws Exception
    {
        
        Bag b=new Bag();
        Bag sep=new Bag();
        sep.caption="AND";
        b.ChildBags.add(sep);
        Bag b1=subformula1.EvaluateProver(s, varassign);
            
        b1.caption=subformula1.toString();
        
        sep.ChildBags.add(b1);
        
        Bag b2=subformula2.EvaluateProver(s, varassign);
        b2.caption=subformula2.toString();
        
        sep.ChildBags.add(b2);
        if(Evaluate(s,varassign)){
            sep.Nodes.addAll(b1.Nodes);
            sep.Nodes.addAll(b2.Nodes);
        }
        b.Nodes.addAll(sep.Nodes);
        return b;
    }
    
    @Override
    public GamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,GameGraph game,
            Double x, Double y) {
        // falsifier move
       System.out.println("int and");
      
       
        //create parent 
         GamePosition parent=new GamePosition();
        String phi="\u2205";
       
        String and="\u2227";
        parent.Label= "(" + subformula1.toString() + and + subformula2.toString() + ")";
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

        //and : player 1 position;
        parent.Player1Position=true;
        
        parent.Coordinates.add(x);
        parent.Coordinates.add(y);
        game.AddVertex(parent);
        
        GamePosition c1=subformula1.ConstructGameGraph(s, varassign, game,x+7,y);
        y=y+2;
        System.out.println("int and" + toString());
        System.out.println("y= "+ y) ;
      System.out.println("int and" + toString());
        game.AddVertex(c1);
        
        game.AddEdge(game.CreateEdge(parent,c1));
        GamePosition c2=subformula2.ConstructGameGraph(s, varassign, game,x+7,y);
        y=y+2;
      System.out.println("int and" + toString());
        System.out.println("y= "+ y) ;
        game.AddVertex(c2);
        game.AddEdge(game.CreateEdge(parent,c2));
        return parent;
    }


   

}

