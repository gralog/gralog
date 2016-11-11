/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.computationtreelogic.algorithm;

import gralog.modallogic.*;
import gralog.computationtreelogic.formula.*;
import gralog.computationtreelogic.parser.*;

import gralog.algorithm.*;
import gralog.preferences.Preferences;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
        name = "Computation-Tree-Logic Model-Checking",
        text = "",
        url = "https://en.wikipedia.org/wiki/Computation_tree_logic"
)
public class ComputationTreeLogicModelChecker extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter(
                "Formula",
                Preferences.getString(this.getClass(), "formula", "A X (P \\wedge Q)"),
                new ComputationTreeLogicSyntaxChecker(),
                ComputationTreeLogicSyntaxChecker.explanation());
    }

    public Object run(KripkeStructure s, AlgorithmParameters p,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        ComputationTreeLogicFormula phi = ComputationTreeLogicParser.parseString(sp.parameter);
        HashSet<World> result = phi.interpretation(s);

        return result;
    }
}
