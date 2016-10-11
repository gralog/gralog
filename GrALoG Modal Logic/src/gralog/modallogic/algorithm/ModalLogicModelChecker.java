/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.modallogic.algorithm;

import gralog.modallogic.*;
import gralog.modallogic.formula.*;
import gralog.modallogic.parser.*;

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
        name = "Modal Logic Model-Checking",
        text = "",
        url = "https://en.wikipedia.org/wiki/Modal_logic"
)
public class ModalLogicModelChecker extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter("[](P \\wedge Q)");
    }

    public Object run(KripkeStructure s, AlgorithmParameters p,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {

        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);

        ModalLogicParser parser = new ModalLogicParser();
        ModalLogicFormula phi = parser.parseString(sp.parameter);
        HashSet<World> result = phi.interpretation(s);

        return result;
    }
}
