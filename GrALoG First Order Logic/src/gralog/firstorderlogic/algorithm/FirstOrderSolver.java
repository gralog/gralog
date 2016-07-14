/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashMap;
import java.util.Set;


/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="First Order Logic Solver",
  text="A Solver for First-Order Logic Queries",
  url="https://en.wikipedia.org/wiki/First-order_logic"
)

public class FirstOrderSolver extends Algorithm {
    
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new StringAlgorithmParameter("\\forall x.\\exists y. E(x,y)");
    }
    
    public Object Run(Structure s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception {
        
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi = parser.parseString(sp.parameter);
        
        HashMap<String, Vertex> varassign = new HashMap<String, Vertex>();
        return phi.Evaluate(s, varassign, onprogress) ? "true" : "false";
    }
    
}
