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
import java.util.Set;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
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
    public String toString()
    {
        String not="\u00AC";
        return not + " (" + subformula1.toString() + ")";
    }
    
    @Override
    public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception
    {
        return !subformula1.Evaluate(s, varassign, onprogress);
    }
     @Override
    public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign,ProgressHandler onprogress) throws Exception
    {
        
        Bag b=new Bag();
       Bag sep=new Bag();
       sep.caption="NOT";
       b.ChildBags.add(sep);
        
        String assignment=new String();
        for(String str : varassign.keySet()){
            assignment+=" [ " + str + " | " + varassign.get(str).Label + " ] ";
        }
        Boolean res;
        res = subformula1.Evaluate(s, varassign,onprogress);
        Bag b1=subformula1.EvaluateProver(s, varassign,onprogress);
        String not="\u00AC";
        b1.caption=assignment+" ("+ subformula1.toString()+" )";
        if(res)
            sep.Nodes.addAll(b1.Nodes);
                
        sep.ChildBags.add( b1);
        
        b.Nodes.addAll(sep.Nodes);
        return b;
    }

    @Override
    public FiniteGamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,FiniteGame game,
            CoordinateClass coor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<String> Variables() throws Exception {
        Set<String> result=new HashSet<>();
        result.addAll(subformula1.Variables());
        return result;
    }

    @Override
    public String Substitute(HashMap<String, String> replace) throws Exception {
        return " \neg " +subformula1.Substitute(replace); 
    }
}
