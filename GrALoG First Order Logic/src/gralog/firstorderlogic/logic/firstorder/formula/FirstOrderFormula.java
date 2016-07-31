/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
import java.util.Set;
/**
 *
 * @author Hv
 */
abstract public class FirstOrderFormula {
    
   abstract public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign,ProgressHandler onprogress) throws Exception;
    abstract public FiniteGamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,FiniteGame game,CoordinateClass coor);
    abstract public Set<String> Variables() throws Exception;
    abstract public String Substitute(HashMap<String,String> replace)throws Exception; 

    abstract public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception;
    
}
