/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;

import java.util.*;

import gralog.finitegame.structure.*;
import gralog.rendering.Vector2D;

import static gralog.dialog.DialogParser.ANSI_RESET;


/**
 * A relation in first-order logic.
 *
 * The relation "=" has special treatment: It is valid in every graph, it always
 * means equality of vertices, and it will always be shown in infix notation.
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
        if (relation.equals("="))
            return parameters.get(0) + " = " + parameters.get(1);
        else
            return relation + "(" + String.join(",", parameters) + ")";
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {
        switch (parameters.size()) {
            case 1:
                Vertex v = varassign.get(parameters.get(0));
                return v.label.matches(".*\\b" + relation + "\\b.*");

            case 2:

                Vertex from = varassign.get(parameters.get(0));
                Vertex to = varassign.get(parameters.get(1));

                if (relation.equals("="))
                    return from.equals(to);

                Set<Edge> E = from.getIncidentEdges();
                for (Edge e : E) {
                    if (e.getSource() == from && e.getTarget() == to // same direction
                        || (!e.isDirected) && e.getSource() == to && e.getTarget() == from) {
                        // opposite direction, but undirected edge
                        if (relation.equals("E") // generic query - matches any edge!
                            || relation.equals(e.label)) { // specific query - matches current edge?
                            return true;
                        }
                    }
                }
                break;

            default:
                throw new Exception("cannot evaluate relation with 0 or >2 parameters");
        }

        return false;
    }

    @Override
    public Subformula evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {
        Subformula b = new Subformula();
        b.value = evaluate(s, varassign, onprogress);
        return b;
    }

    @Override
    public GameGraphResult constructGameGraph(Structure s,
        HashMap<String, Vertex> varassign, FiniteGame game,
        Vector2D coor) {
        FiniteGamePosition parent = game.addVertex();

        parent.setCoordinates(coor);
        parent.label = toString() + ", "
            + FirstOrderFormula.variableAssignmentToString(varassign);

        Boolean res = false;
        if (parameters.size() == 1){
            Vertex v = varassign.get(parameters.get(0));
            if (v.label.matches(".*\\b" + relation + "\\b.*"))
                res = true;
            else
                res = false;
        }
        else {
            Vertex from = varassign.get(parameters.get(0));
            Vertex to = varassign.get(parameters.get(1));

            Set<Edge> E = from.getIncidentEdges();
            for (Edge e : E) {
                if (e.getSource() == from && e.getTarget() == to // same direction
                        || (!e.isDirected) && e.getSource() == to && e.getTarget() == from) {
                    // opposite direction, but undirected edge
                    if (relation.equals("E") // generic query - matches any edge!
                            || relation.equals(e.label)) { // specific query - matches current edge?
                        res = true;
                    }
                }
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
