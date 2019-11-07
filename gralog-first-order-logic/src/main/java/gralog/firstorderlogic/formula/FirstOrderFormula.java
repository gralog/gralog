/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import gralog.finitegame.structure.*;
import gralog.rendering.Vector2D;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class FirstOrderFormula {

    public static class GameGraphResult {

        GameGraphResult(FiniteGamePosition pos, int h) {
            position = pos;
            height = h;
        }
        public final FiniteGamePosition position;
        public final int height;
    }

    protected static final int X_OFFSET = 9;

    public abstract Subformula evaluateProver(Structure s,
        HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception;

    /**
     * Constructs the game graph and stores it in game. Returns an object containing the root position and its height.
     * @param s The structure
     * @param varassign The variable assignment used to construct the label of the root.
     * @param game The game to be constructed.
     * @param coor Coordinates of the root.
     * @return An object containing the root position and its height.
     */
    public abstract GameGraphResult constructGameGraph(Structure s,
        HashMap<String, Vertex> varassign, FiniteGame game,
        Vector2D coor);

    public abstract Set<String> variables() throws Exception;

    public abstract String substitute(HashMap<String, String> replace) throws Exception;

    public abstract boolean evaluate(Structure s,
        HashMap<String, Vertex> varassign, ProgressHandler onprogress) throws Exception;

    public static String variableAssignmentToString(Map<String, Vertex> assignment) {
        if (assignment.isEmpty())
            return " { }";
        return " { " + assignment.entrySet().stream()
            .map((e) -> e.getKey() + "↦" + e.getValue().id)
            .collect(Collectors.joining(", "))
            + " }";
    }

    @Override
    public String toString() {
        return toString(FormulaPosition.Quantifier, FormulaEndPosition.AT_END);
    }

    public abstract String toString(FormulaPosition pos, FormulaEndPosition endPos);

    // For the toString method, we track the position in the formula in order
    // to produce a string with a minimum number of parentheses.
    public enum FormulaPosition {
        Not, OrLeft, OrRight, AndLeft, AndRight, Quantifier
    }

    // We also track if we are in the middle of a disjunction/conjunction or not
    // in order to save parentheses for the quantifiers (quantifiers reach as
    // far to the right as possible).
    public enum FormulaEndPosition {
        MIDDLE, AT_END
    }
}
