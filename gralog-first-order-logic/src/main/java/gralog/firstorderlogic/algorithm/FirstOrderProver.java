/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import gralog.preferences.Preferences;

import java.util.HashMap;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "First Order Logic Prover",
    text = "An interactive solver for first-order logic queries",
    url = "https://en.wikipedia.org/wiki/First-order_logic")
public class FirstOrderProver extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new FirstOrderProverParameters(Preferences.getString(
            this.getClass(), "formula", "!x. ?y. E(x,y)"));
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) throws Exception {
        FirstOrderProverParameters sp = (FirstOrderProverParameters) (p);
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        onprogress.onProgress(s);

        FirstOrderFormula phi;
        try {
            phi = FirstOrderParser.parseString(sp.parameter);
        } catch (Exception ex) {
            return ex.getMessage();
        }

        Bag rootBag = phi.evaluateProver(s, new HashMap<>(), onprogress);
        rootBag.caption = phi.toString();
        return rootBag;
    }
}
