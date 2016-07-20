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
import gralog.modalmucalculus.structure.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Modal μ-Calculus to Parity Game",
  text="Creates a parity game from a modal μ-calculus formula and a Kripke Structure",
  url="https://en.wikipedia.org/wiki/Modal_%CE%BC-calculus"
)
public class ModalMuCalculusToParityGame extends Algorithm {
    
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new StringAlgorithmParameter("\\nu X. \\mu Y.(P \\wedge []X) \\vee []Y");
    }
    
    public Object Run(KripkeStructure s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        
        ModalMuCalculusParser parser = new ModalMuCalculusParser();
        ModalMuCalculusFormula phi = parser.parseString(sp.parameter);
        ModalMuCalculusFormula nnf = phi.NegationNormalForm();

        ParityGame result = new ParityGame();
        Double w = nnf.FormulaWidth();
        Double h = nnf.FormulaDepth();
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> positionIndex = new HashMap<>();
        Map<String, ModalMuCalculusFormula> variableDefinitionPosition = new HashMap<>();
        
        nnf.CreateParityGamePositions(3d, 0d, 0d, Math.max(w, h), Math.max(w, h), s, result, 0, positionIndex);
        nnf.CreateParityGameTransitions(s, result, positionIndex, variableDefinitionPosition);
        
        return result;
    }
    
    
}
