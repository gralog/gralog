/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.firstorderlogic.prover.TreeDecomposition.*;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashMap;
import java.util.Set;

import java.util.HashSet;
import gralog.finitegame.structure.*;
import gralog.rendering.Vector2D;
import java.util.List;

/**
 *
 */
public class FirstOrderRelation extends FirstOrderFormula {

    String relation;
    List<String> parameters;

    public FirstOrderRelation(String relation, List<String> parameters) {
        this.relation = relation;
        this.parameters = parameters;
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        String result = "";
        String glue = "";

        for (String p : parameters) {
            result += glue + p;
            glue = ",";
        }

        return relation + "(" + result + ")";
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        switch (parameters.size()) {
            case 1:
                Vertex v = varassign.get(parameters.get(0));
                return relation.equals(v.label);

            case 2:

                Vertex from = varassign.get(parameters.get(0));
                Vertex to = varassign.get(parameters.get(1));

                if (relation.equals("equals")) {
                    return from.equals(to);
                }

                Set<Edge> E = from.getConnectedEdges();
                for (Edge e : E) {
                    if (e.getSource() == from && e.getTarget() == to // same direction
                        || (!e.isDirected) && e.getSource() == to && e.getTarget() == from) // opposite direction, but undirected edge
                    {
                        if (relation.equals("E") // generic query - matches any edge!
                            || relation.equals(e.label)) // specific query - matches current edge?
                            return true;
                    }
                }
                break;

            default:
                throw new Exception("cannot evaluate relation with 0 or >2 parameters");
        }

        return false;
    }

    @Override
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        Bag b = new Bag();
        b.eval = evaluate(s, varassign, onprogress);
        return b;
    }

    @Override
    public GameGraphResult constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            Vector2D coor) {
        FiniteGamePosition parent = new FiniteGamePosition();

        parent.coordinates = coor;
        parent.label = toString() + ", "
                       + FirstOrderFormula.variableAssignmentToString(varassign);

        Boolean res = false;
        Vertex from = varassign.get(parameters.get(0));
        Vertex to = varassign.get(parameters.get(1));

        Set<Edge> E = from.getConnectedEdges();
        for (Edge e : E) {
            if (e.getSource() == from && e.getTarget() == to // same direction
                || (!e.isDirected) && e.getSource() == to && e.getTarget() == from) // opposite direction, but undirected edge
            {
                if (relation.equals("E") // generic query - matches any edge!
                    || relation.equals(e.label)) // specific query - matches current edge?
                    res = true;
            }
        }
        parent.player1Position = res;
        return new GameGraphResult(parent, 1);
    }

    @Override
    public Set<String> variables() throws Exception {
        return new HashSet<>(parameters);
    }

    @Override
    public String substitute(HashMap<String, String> replace) throws Exception {
        for (int i = 0; i < parameters.size(); i++) {
            if (replace.containsKey(parameters.get(i))) {
                parameters.set(i, replace.get(parameters.get(i)));
            }
        }
        return toString();
    }
}
