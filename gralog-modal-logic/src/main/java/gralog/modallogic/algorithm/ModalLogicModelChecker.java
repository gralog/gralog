/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modallogic.algorithm;

import gralog.modallogic.parser.ModalLogicSyntaxChecker;
import gralog.modallogic.*;
import gralog.modallogic.formula.*;
import gralog.modallogic.parser.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import gralog.preferences.Preferences;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "Modal Logic Model-Checking",
    text = "",
    url = "https://en.wikipedia.org/wiki/Modal_logic")
public class ModalLogicModelChecker extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return new StringAlgorithmParameter(
            "Formula",
            Preferences.getString(this.getClass(), "formula", "□(P ∧ Q)"),
            new ModalLogicSyntaxChecker(),
            ModalLogicSyntaxChecker.explanation());
    }

    public Object run(KripkeStructure s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {

        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        ModalLogicFormula phi = ModalLogicParser.parseString(sp.parameter);
        HashSet<World> result = phi.interpretation(s);

        return result;
    }
}
