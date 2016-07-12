/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modalmucalculus.algorithm;

import gralog.modallogic.*;
import gralog.modalmucalculus.*;
import gralog.modalmucalculus.formula.*;
import gralog.modalmucalculus.parser.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashSet;


/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Modal μ-Calculus to Parity Game",
  text="Creates a parity game from a modal μ-calculus formula",
  url="https://en.wikipedia.org/wiki/Modal_%CE%BC-calculus"
)
public class ModalMuCalculusModelChecker extends Algorithm {
    
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new StringAlgorithmParameter("");
    }
    
    public Object Run(KripkeStructure s, AlgorithmParameters p, ProgressHandler onprogress) throws Exception {
        
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        ModalMuCalculusParser parser = new ModalMuCalculusParser();
        ModalMuCalculusFormula phi = parser.parseString(sp.parameter);
        //HashSet<World> result = phi.Interpretation(s);
        
        return null; //result;
    }
    
    
}
