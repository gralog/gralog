/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.SamplePlugin.algorithm;

import gralog.SamplePlugin.logic.firstorder.formula.*;
import gralog.SamplePlugin.logic.firstorder.parser.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashSet;


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
        return new StringAlgorithmParameter("");
    }
    
    public Object Run(Structure s, AlgorithmParameters p, ProgressHandler onprogress) throws Exception {
        
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi = parser.parseString(sp.parameter);
        
        return phi.toString();
    }
    
}
