/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.modalmucalculus.algorithm;

import gralog.modalmucalculus.parser.ModalMuCalculusSyntaxChecker;
import gralog.modallogic.*;
import gralog.modalmucalculus.formula.*;
import gralog.modalmucalculus.parser.*;
import gralog.modalmucalculus.structure.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import gralog.preferences.Preferences;
import gralog.rendering.Vector2D;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "Modal μ-Calculus to Parity Game",
    text = "Creates a parity game from a modal μ-calculus formula and a Kripke Structure",
    url = "https://en.wikipedia.org/wiki/Modal_%CE%BC-calculus")
public class ModalMuCalculusToParityGame extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return new StringAlgorithmParameter(
            "Formula",
            Preferences.getString(this.getClass(), "formula", "νX. μY. (P  ∧ □X) ∨ □Y"),
            new ModalMuCalculusSyntaxChecker(),
            ModalMuCalculusSyntaxChecker.explanation()
        );
    }

    public Object run(KripkeStructure s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        ModalMuCalculusFormula phi = ModalMuCalculusParser.parseString(sp.parameter);
        ModalMuCalculusFormula nnf = phi.negationNormalForm();

        ParityGame result = new ParityGame();
        Double w = nnf.formulaWidth();
        Double h = nnf.formulaDepth();
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> positionIndex = new HashMap<>();
        Map<String, ModalMuCalculusFormula> variableDefinitionPosition = new HashMap<>();

        nnf.createParityGamePositions(3d,
            new Vector2D(0d, 0d), new Vector2D(Math.max(w, h), Math.max(w, h)),
            s, result, 0, positionIndex);
        nnf.createParityGameTransitions(s, result, positionIndex, variableDefinitionPosition);

        return result;
    }
}
