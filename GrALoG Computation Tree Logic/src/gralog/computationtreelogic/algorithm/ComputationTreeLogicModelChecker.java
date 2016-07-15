/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.computationtreelogic.algorithm;

import gralog.modallogic.*;
import gralog.computationtreelogic.formula.*;
import gralog.computationtreelogic.parser.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashSet;
import java.util.Set;


/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Computation-Tree-Logic Model-Checking",
  text="",
  url="https://en.wikipedia.org/wiki/Computation_tree_logic"
)
public class ComputationTreeLogicModelChecker extends Algorithm
{
    
    @Override
    public AlgorithmParameters GetParameters(Structure s)
    {
        return new StringAlgorithmParameter("A X (P \\wedge Q)");
    }
    
    public Object Run(KripkeStructure s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        ComputationTreeLogicParser parser = new ComputationTreeLogicParser();
        ComputationTreeLogicFormula phi = parser.parseString(sp.parameter);
        HashSet<World> result = phi.Interpretation(s);
        
        return result;
    }
}
