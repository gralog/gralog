/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.finitegame.structure.*;
import gralog.firstorderlogic.algorithm.CoordinateClass;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import java.util.HashMap;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.rendering.Vector2D;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Hv
 */
public class FirstOrderAnd extends FirstOrderFormula {

    FirstOrderFormula subformula1;
    FirstOrderFormula subformula2;

    public FirstOrderAnd(FirstOrderFormula subformula1,
            FirstOrderFormula subformula2) {
        this.subformula1 = subformula1;
        this.subformula2 = subformula2;
    }

    @Override
    public String toString() {

        String and = "\u2227";
        return "(" + subformula1.toString() + and + subformula2.toString() + ")";
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        if (!subformula1.evaluate(s, varassign, onprogress))
            return false;
        return subformula2.evaluate(s, varassign, onprogress);
    }

    @Override
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        Bag b = new Bag();
        Bag sep = new Bag();
        sep.caption = "AND";
        b.ChildBags.add(sep);

        Bag b1 = subformula1.evaluateProver(s, varassign, onprogress);
        String assignment = new String();
        for (String str : varassign.keySet()) {
            assignment += " [ " + str + " | " + varassign.get(str).label + " ] ";
        }
        b1.assignment = assignment;
        b1.caption = subformula1.toString();
        sep.ChildBags.add(b1);

        Bag b2 = subformula2.evaluateProver(s, varassign, onprogress);
        b2.assignment = assignment;
        b2.caption = subformula2.toString();
        sep.ChildBags.add(b2);
        b.eval = (b1.eval && b2.eval);
        return b;
    }

    @Override
    public FiniteGamePosition constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            CoordinateClass coor) {
        FiniteGamePosition parent = new FiniteGamePosition();
        String phi = "\u2205";
        String and = "\u2227";
        parent.label = "(" + subformula1.toString() + and + subformula2.toString() + ")";
        parent.label += " , { ";
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

        //and : player 1 position;
        parent.player1Position = true;

        parent.coordinates = new Vector2D(coor.x, coor.y);
        game.addVertex(parent);
        CoordinateClass temp = new CoordinateClass();
        temp.x = coor.x + 7;
        temp.y = coor.y;
        FiniteGamePosition c1 = subformula1.constructGameGraph(s, varassign, game, temp);

        coor.y = temp.y + 1;
        game.addVertex(c1);

        game.addEdge(game.createEdge(parent, c1));
        temp.x = coor.x + 7;
        temp.y = coor.y;
        FiniteGamePosition c2 = subformula2.constructGameGraph(s, varassign, game, temp);
        coor.y = temp.y + 1;
        game.addVertex(c2);
        game.addEdge(game.createEdge(parent, c2));
        return parent;

    }

    @Override
    public Set<String> variables() throws Exception {
        Set<String> result = new HashSet<>();
        result.addAll(subformula1.variables());
        result.addAll(subformula2.variables());
        return result;
    }

    @Override
    public String substitute(HashMap<String, String> replace) throws Exception {
        return subformula1.substitute(replace) + " \\wedge " + subformula1.substitute(replace);
    }
}
