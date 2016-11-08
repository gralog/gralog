/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.modalmucalculus.algorithm;

import gralog.modallogic.*;
import gralog.modalmucalculus.formula.*;
import gralog.modalmucalculus.parser.*;
import gralog.modalmucalculus.structure.*;

import gralog.algorithm.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import gralog.properties.Properties;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
        name = "Modal μ-Calculus to Parity Game",
        text = "Creates a parity game from a modal μ-calculus formula and a Kripke Structure",
        url = "https://en.wikipedia.org/wiki/Modal_%CE%BC-calculus"
)
public class ModalMuCalculusToParityGame extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter(
                "Formula",
                Properties.getString(this.getClass(), "formula", "νX. μY. (P  ∧ □X) ∨ □Y"),
                new ModalMuCalculusSyntaxChecker(),
                ModalMuCalculusSyntaxChecker.explanation()
        );
    }

    public Object run(KripkeStructure s, AlgorithmParameters p,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Properties.setString(this.getClass(), "formula", sp.parameter);

        ModalMuCalculusParser parser = new ModalMuCalculusParser();
        ModalMuCalculusFormula phi = parser.parseString(sp.parameter);
        ModalMuCalculusFormula nnf = phi.NegationNormalForm();

        ParityGame result = new ParityGame();
        Double w = nnf.formulaWidth();
        Double h = nnf.formulaDepth();
        Map<World, Map<ModalMuCalculusFormula, ParityGamePosition>> positionIndex = new HashMap<>();
        Map<String, ModalMuCalculusFormula> variableDefinitionPosition = new HashMap<>();

        nnf.createParityGamePositions(3d, 0d, 0d, Math.max(w, h), Math.max(w, h), s, result, 0, positionIndex);
        nnf.createParityGameTransitions(s, result, positionIndex, variableDefinitionPosition);

        return result;
    }
}
