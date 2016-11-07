/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
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
 */
@AlgorithmDescription(
        name = "Modal Logic Model-Checking",
        text = "",
        url = "https://en.wikipedia.org/wiki/Modal_logic"
)
public class ModalLogicModelChecker extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter("Formula", "□(P ∧ Q)");
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
