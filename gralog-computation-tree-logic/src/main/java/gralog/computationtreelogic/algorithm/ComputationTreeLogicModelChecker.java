/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.computationtreelogic.algorithm;


import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmDescription;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.computationtreelogic.formula.ComputationTreeLogicFormula;

import gralog.computationtreelogic.parser.ComputationTreeLogicParser;
import gralog.computationtreelogic.parser.ComputationTreeLogicSyntaxChecker;
import gralog.modallogic.KripkeStructure;
import gralog.modallogic.World;
import gralog.preferences.Preferences;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Highlights;
import gralog.structure.Structure;

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
    public gralog.algorithm.AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return new StringAlgorithmParameter(
            "Formula",
            Preferences.getString(this.getClass(), "formula", "A X (P \\wedge Q)"),
            new ComputationTreeLogicSyntaxChecker(),
            ComputationTreeLogicSyntaxChecker.explanation());
    }

    public Object run(KripkeStructure s, gralog.algorithm.AlgorithmParameters p,
                      Set<Object> selection, ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        ComputationTreeLogicFormula phi = ComputationTreeLogicParser.parseString(sp.parameter);
        HashSet<World> result = phi.interpretation(s);

        return result;
    }
}
