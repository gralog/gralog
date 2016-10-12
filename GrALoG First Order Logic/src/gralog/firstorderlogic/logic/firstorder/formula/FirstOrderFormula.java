/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
import java.util.Set;

/**
 *
 */
abstract public class FirstOrderFormula {

    abstract public Bag evaluateProver(Structure s,
            HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception;

    abstract public FiniteGamePosition constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            CoordinateClass coor);

    abstract public Set<String> variables() throws Exception;

    abstract public String substitute(HashMap<String, String> replace) throws Exception;

    abstract public boolean evaluate(Structure s,
            HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception;
}
