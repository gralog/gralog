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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

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

    protected static String variableAssignmentToString(Map<String, Vertex> assignment) {
        if (assignment.isEmpty())
            return "{ }";
        return "{ " + assignment.entrySet().stream()
                .map((e) -> e.getKey() + "↦" + e.getValue().label)
                .collect(Collectors.joining(", "))
               + " }";
    }

    @Override
    public String toString() {
        return toString(FormulaPosition.Quantifier);
    }

    abstract public String toString(FormulaPosition pos);

    // For the toString method, we track the position in the formula in order
    // to produce a string with a minimum number of parantheses.
    public enum FormulaPosition {
        Not, OrLeft, OrRight, AndLeft, AndRight, Quantifier
    }
}
