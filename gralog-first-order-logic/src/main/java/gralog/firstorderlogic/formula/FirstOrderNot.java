/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.algorithm.DepthFirstSearchTree;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Edge;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;
import gralog.finitegame.structure.*;
import gralog.rendering.Vector2D;

/**
 *
 */
public class FirstOrderNot extends FirstOrderFormula {

    FirstOrderFormula subformula1;

    public FirstOrderNot(FirstOrderFormula subformula1) {
        this.subformula1 = subformula1;
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        return "¬" + subformula1.toString(FormulaPosition.Not, endPos);
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {
        return !subformula1.evaluate(s, varassign, onprogress);
    }

    @Override
    public Subformula evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {

        Subformula b1 = subformula1.evaluateProver(s, varassign, onprogress);
        b1.assignment = new HashMap<>(varassign);
        b1.subformula = subformula1.toString();

        Subformula b = new Subformula();
        b.value = !b1.value;
        b.children.add(b1);
        return b;
    }

    /**
     * Recursively makes DFS starting from v and changes v.player1Position of every visited position.
     * @param v {@link FiniteGamePosition} to start from
     */
    protected static void DFSchangePlayer1Position(FiniteGamePosition v) {

        // change v.player1Position
        if (v.player1Position)
            v.player1Position = false;
        else
            v.player1Position = true;

        HashSet<FiniteGamePosition> visited = new HashSet<>();
        for (Edge e : v.getOutgoingEdges()) {

            FiniteGamePosition other = (FiniteGamePosition) e.getTarget();
            if (other == v) // selfloop
                continue;

            if (visited.contains(other)) // successor already in the tree
                continue;

            visited.add(other);
            DFSchangePlayer1Position(other);

        }
    }

    @Override
    public GameGraphResult constructGameGraph(Structure s,
        HashMap<String, Vertex> varassign, FiniteGame game,
        Vector2D coor) {
        GameGraphResult w = subformula1.constructGameGraph(
                s, varassign, game, new Vector2D(coor.getX(), coor.getY()));

        // iterate over all vertices of the subtree and change their player1Position values
        // (note that the game graph is a tree rather than a DAG because a position is an appearance of a subformula,
        // not a subformula)
        HashMap<Vertex, Vertex> predecessor = null;
        HashMap<Vertex, Edge> moveFromPredecessor = null;
        DepthFirstSearchTree.depthFirstSearch(w.position, predecessor, moveFromPredecessor);

        return w;
    }

    @Override
    public Set<String> variables() throws Exception {
        Set<String> result = new HashSet<>();
        result.addAll(subformula1.variables());
        return result;
    }

    @Override
    public String substitute(HashMap<String, String> replace) throws Exception {
        return " \neg " + subformula1.substitute(replace);
    }
}
