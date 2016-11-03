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
import java.util.Map;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
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
    public String toString(FormulaPosition pos) {
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
    public FiniteGamePosition constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            CoordinateClass coor) {

        FiniteGamePosition parent = new FiniteGamePosition();
        parent.coordinates = new Vector2D(coor.x, coor.y);

        coor.y = coor.y + 1;
        String phi = "\u2205";
        parent.label = this.toString() + ", { ";
        if (varassign.isEmpty()) {
            parent.label += phi;
        }
        else {
            String glue = "";
            for (Map.Entry<String, Vertex> entry : varassign.entrySet()) {
                String key = entry.getKey();
                Vertex value = entry.getValue();
                parent.label += glue + "(" + key + "," + value.label + ")";
                glue = ",";
            }
        }
        parent.label += " }";

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
        return parent;
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
