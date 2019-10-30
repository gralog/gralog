/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.formula;

import gralog.progresshandler.ProgressHandler;
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
public class FirstOrderOr extends FirstOrderFormula {

    FirstOrderFormula subformula1;
    FirstOrderFormula subformula2;

    public FirstOrderOr(FirstOrderFormula subformula1,
        FirstOrderFormula subformula2) {
        this.subformula1 = subformula1;
        this.subformula2 = subformula2;
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        String result = subformula1.toString(FormulaPosition.OrLeft, FormulaEndPosition.MIDDLE) + " ∨ "
            + subformula2.toString(FormulaPosition.OrRight, endPos);
        switch (pos) {
            case Not:
            case AndLeft:
            case AndRight:
            case OrRight:
                return "(" + result + ")";
            default:
                return result;
        }
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {
        if (subformula1.evaluate(s, varassign, onprogress))
            return true;
        return subformula2.evaluate(s, varassign, onprogress);
    }

    @Override
    public Subformula evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {

        Subformula b = new Subformula();

        Subformula b1 = subformula1.evaluateProver(s, varassign, onprogress);
        b1.assignment = new HashMap<>(varassign);
        b1.subformula = subformula1.toString();
        b.children.add(b1);

        Subformula b2 = subformula2.evaluateProver(s, varassign, onprogress);
        b2.assignment = b1.assignment;
        b2.subformula = subformula2.toString();
        b.children.add(b2);
        b.value = (b1.value || b2.value);
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
        // "or", so this is a player 0 position.
        parent.player1Position = false;

        GameGraphResult c1 = subformula1.constructGameGraph(
            s, varassign, game, new Vector2D(coor.getX() + X_OFFSET, coor.getY()));
        game.addEdge(parent, c1.position);

        GameGraphResult c2 = subformula2.constructGameGraph(
            s, varassign, game, new Vector2D(coor.getX() + X_OFFSET, coor.getY() + c1.height + 1));
        game.addEdge(parent, c2.position);

        return new GameGraphResult(parent, c1.height + c2.height + 1);
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
        return subformula1.substitute(replace) + " \\vee " + subformula1.substitute(replace);
    }
}
