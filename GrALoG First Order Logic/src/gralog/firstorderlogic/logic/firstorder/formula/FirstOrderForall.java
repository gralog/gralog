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
import gralog.finitegame.structure.*;
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
    public String toString(FormulaPosition pos) {
        String result = "∀" + variable + ". "
                        + subformula1.toString(FormulaPosition.Quantifier);
        if (pos == FormulaPosition.OrLeft || pos == FormulaPosition.AndLeft)
            return "(" + result + ")";
        return result;
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
            if (t.eval)
                b.Nodes.add(v);
            else
                result = false;
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
    public GameGraphResult constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            Vector2D coor) {
        Vertex oldvalue = varassign.get(variable);
        FiniteGamePosition parent = new FiniteGamePosition();

        parent.coordinates = coor;
        parent.label = toString() + ", "
                       + FirstOrderFormula.variableAssignmentToString(varassign);
        // "forall", so this is a player 1 position.
        parent.player1Position = true;
        game.addVertex(parent);

        Set<Vertex> V = s.getVertices();
        int yOffset = 0;
        for (Vertex v : V) {
            varassign.put(variable, v);
            GameGraphResult gp = subformula1.constructGameGraph(
                    s, varassign, game, new Vector2D(coor.getX() + xOffset, coor.getY() + yOffset));
            yOffset += gp.height + 1;
            game.addVertex(gp.position);
            game.addEdge(game.createEdge(parent, gp.position));
            // Set label for this vertex.
            gp.position.label = subformula1.toString() + ", "
                                + FirstOrderFormula.variableAssignmentToString(varassign);
        }
        varassign.remove(variable);
        if (oldvalue != null)
            varassign.put(variable, oldvalue);
        return new GameGraphResult(parent, yOffset);
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
