/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.progresshandler.ProgressHandler;
import gralog.rendering.GralogColor;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import java.util.Set;

import java.util.HashSet;
import java.util.Map;
import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
import gralog.rendering.Vector2D;

/**
 *
 */
public class FirstOrderForall extends FirstOrderFormula {

    String variable;
    FirstOrderFormula subformula1;

    public FirstOrderForall(String variable, FirstOrderFormula subformula1) {
        this.variable = variable;
        this.subformula1 = subformula1;
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        Vertex oldvalue = varassign.get(variable);
        boolean result = true;

        Set<Vertex> V = s.getVertices();
        for (Vertex v : V) {
            varassign.put(variable, v);

            GralogColor bak = v.fillColor;
            v.fillColor = GralogColor.GREEN;
            onprogress.onProgress(s);

            result = subformula1.evaluate(s, varassign, onprogress);

            v.fillColor = bak;

            if (!result)
                break;
        }
        onprogress.onProgress(s);

        varassign.remove(variable);
        if (oldvalue != null)
            varassign.put(variable, oldvalue);

        return result;
    }

    @Override
    public String toString() {
        String forall = "\u2200";
        return forall + variable + ". " + subformula1.toString();
    }

    @Override
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {

        Boolean result = true;
        Vertex oldvalue = varassign.get(variable);
        String assignment = new String();
        for (String str : varassign.keySet()) {
            assignment += " [ " + str + " | " + varassign.get(str).label + " ] ";
        }

        Set<Vertex> V = s.getVertices();
        Bag b = new Bag();
        for (Vertex v : V) {
            varassign.put(variable, v);
            Bag t = subformula1.evaluateProver(s, varassign, onprogress);
            if (!t.eval) {
                result = false;
            }
            t.assignment = assignment + " [ " + variable + " | " + v.label + " ] ";
            t.caption = subformula1.toString();
            b.ChildBags.add(t);
        }

        b.eval = result;
        varassign.remove(variable);
        if (oldvalue != null)
            varassign.put(variable, oldvalue);

        return b;
    }

    @Override
    public FiniteGamePosition constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            CoordinateClass coor) {
        Vertex oldvalue = varassign.get(variable);
        FiniteGamePosition parent = new FiniteGamePosition();
        String phi = "\u2205";
        String forall = "\u2200";

        parent.coordinates = new Vector2D(coor.x, coor.y);

        parent.label = "( " + forall + variable + "  (" + subformula1.toString() + ")";
        parent.label += " , { ";

        if (varassign.isEmpty()) {
            parent.label += phi;
            coor.x = coor.x + 2;
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

        //forall : player 1 position;
        parent.player1Position = true;

        game.addVertex(parent);

        Set<Vertex> V = s.getVertices();
        for (Vertex v : V) {
            varassign.put(variable, v);
            CoordinateClass temp = new CoordinateClass();
            temp.x = coor.x + 7;
            temp.y = coor.y;
            FiniteGamePosition gp = subformula1.constructGameGraph(s, varassign, game, temp);
            coor.y = temp.y + 1;
            game.addVertex(gp);
            game.addEdge(game.createEdge(parent, gp));
            //set label for this vertex
            gp.label = "(" + subformula1.toString() + ") , { ";
            String glue = "";
            for (Map.Entry<String, Vertex> entry : varassign.entrySet()) {
                String key = entry.getKey();
                Vertex value = entry.getValue();
                gp.label += glue + "(" + key + "," + value.label + ")";
                glue = ",";
            }

            gp.label += " }";

        }
        varassign.remove(variable);
        if (oldvalue != null)
            varassign.put(variable, oldvalue);
        return parent;
    }

    @Override
    public Set<String> variables() throws Exception {
        Set<String> result = new HashSet<>();
        result.add(variable);
        result.addAll(subformula1.variables());
        return result;

    }

    @Override
    public String substitute(HashMap<String, String> replace) throws Exception {
        if (replace.containsKey(variable)) {
            variable = replace.get(variable);
        }

        return "\\forall " + variable + " . " + subformula1.substitute(replace);
    }
}
