/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.firstorderlogic.view.FirstOrderSyntaxChecker;
import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.HashMap;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
        name = "First Order Logic Solver",
        text = "A Solver for First-Order Logic Queries",
        url = "https://en.wikipedia.org/wiki/First-order_logic"
)

public class FirstOrderSolver extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter("Formula", "!x. ?y. E(x,y)", new FirstOrderSyntaxChecker());
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
            ProgressHandler onprogress) throws Exception {

        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);

        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi = parser.parseString(sp.parameter);

        HashMap<String, Vertex> varassign = new HashMap<>();
        return phi.evaluate(s, varassign, onprogress) ? "true" : "false";
    }
}
