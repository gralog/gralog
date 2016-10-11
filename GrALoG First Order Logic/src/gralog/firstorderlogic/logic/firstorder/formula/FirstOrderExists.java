/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
import gralog.firstorderlogic.prover.TreeDecomposition.*;

import gralog.progresshandler.ProgressHandler;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import gralog.rendering.GralogColor;
import gralog.rendering.Vector2D;
import java.util.HashMap;
import java.util.Set;

import java.util.HashSet;
import java.util.Map;

/**
 *
 */
public class FirstOrderExists extends FirstOrderFormula {

    String variable;
    FirstOrderFormula subformula1;

    public FirstOrderExists(String variable, FirstOrderFormula subformula1) {
        this.variable = variable;
        this.subformula1 = subformula1;
    }

    @Override
    public String toString() {
        String exists = "\u2203";
        return exists + variable + " " + subformula1.toString();
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        Vertex oldvalue = varassign.get(variable);
        boolean result = false;

        Set<Vertex> V = s.getVertices();
        for (Vertex v : V) {
            varassign.put(variable, v);

            GralogColor bak = v.fillColor;
            v.fillColor = GralogColor.RED;
            onprogress.onProgress(s);

            result = subformula1.evaluate(s, varassign, onprogress);

            v.fillColor = bak;

            if (result)
                break;
        }
        onprogress.onProgress(s);

        varassign.remove(variable);
        if (oldvalue != null)
            varassign.put(variable, oldvalue);

        return result;
    }

    @Override
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        Boolean result = false;
        Vertex oldvalue = varassign.get(variable);
        Bag b = new Bag();
        Set<Vertex> V = s.getVertices();
        String assignment = new String();
        for (String str : varassign.keySet()) {
            assignment += " [ " + str + " | " + varassign.get(str).label + " ] ";
        }

        for (Vertex v : V) {
            varassign.put(variable, v);
            Bag t = subformula1.evaluateProver(s, varassign, onprogress);
            if (t.eval) {
                b.Nodes.add(v);
                result = true;
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
        String exists = "\u2203";
        parent.label = "( " + exists + variable + "  (" + subformula1.toString() + ")";
        parent.label += " , { ";
        parent.coordinates = new Vector2D(coor.x, coor.y);

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

        //exists : player 0 position;
        parent.player1Position = false;
        game.addVertex(parent);

        Set<Vertex> V = s.getVertices();
        for (Vertex v : V) {
            CoordinateClass temp = new CoordinateClass();
            temp.x = coor.x + 7;
            temp.y = coor.y;
            varassign.put(variable, v);
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

        return "\\exists " + variable + " . " + subformula1.substitute(replace);
    }
}
