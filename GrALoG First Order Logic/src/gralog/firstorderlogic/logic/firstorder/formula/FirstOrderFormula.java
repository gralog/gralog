/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.structure.*;
import java.util.HashMap;
import gralog.firstorderlogic.prover.TreeDecomposition.Bag;
import gralog.firstorderlogic.structure.*;

/**
 *
 * @author Hv
 */
abstract public class FirstOrderFormula {
    
    abstract public boolean Evaluate(Structure s, HashMap<String, Vertex> varassign) throws Exception;
    abstract public Bag EvaluateProver(Structure s, HashMap<String, Vertex> varassign) throws Exception;
    abstract public GamePosition ConstructGameGraph(Structure s, HashMap<String, Vertex> varassign,GameGraph game,
            Double x, Double y);
}
