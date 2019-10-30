/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.progresshandler.ProgressHandler;
import gralog.rendering.GralogColor;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.util.Collection;
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

        Collection<Vertex> V = s.getVertices();
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
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        String result = "∀" + variable + ". "
            + subformula1.toString(FormulaPosition.Quantifier, FormulaEndPosition.AT_END);
        if (endPos == FormulaEndPosition.MIDDLE)
            return "(" + result + ")";
        return result;
    }

    @Override
    public Subformula evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {

        Boolean result = true;
        Vertex oldvalue = varassign.get(variable);


        Collection<Vertex> V = s.getVertices();
        Subformula b = new Subformula();
        for (Vertex v : V) {
            varassign.put(variable, v);
            Subformula t = subformula1.evaluateProver(s, varassign, onprogress);
            if (t.value)
                b.validVertices.add(v);
            else
                result = false;
            t.assignment = new HashMap<>(varassign);
            t.subformula = subformula1.toString();
            b.children.add(t);
        }

        b.value = result;
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
        FiniteGamePosition parent = game.addVertex();

        parent.setCoordinates(coor);
        parent.label = toString() + ", "
            + FirstOrderFormula.variableAssignmentToString(varassign);
        // "forall", so this is a player 1 position.
        parent.player1Position = true;


        Collection<Vertex> V = s.getVertices();
        int yOffset = 0;
        for (Vertex v : V) {
            varassign.put(variable, v);
            GameGraphResult gp = subformula1.constructGameGraph(
                s, varassign, game, new Vector2D(coor.getX() + X_OFFSET, coor.getY() + yOffset));
            yOffset += gp.height + 1;
            game.addEdge(parent, gp.position);
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
