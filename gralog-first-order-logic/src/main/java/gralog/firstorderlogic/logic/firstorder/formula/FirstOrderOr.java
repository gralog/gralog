/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.logic.firstorder.formula;

import gralog.firstorderlogic.prover.TreeDecomposition.*;
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
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {

        Bag b = new Bag();
        Bag sep = new Bag();
        sep.caption = "OR";
        String assignment = "";
        for (String str : varassign.keySet()) {
            assignment += " [ " + str + " | " + varassign.get(str).label + " ] ";
        }
        b.childBags.add(sep);
        Bag b1 = subformula1.evaluateProver(s, varassign, onprogress);
        b1.assignment = assignment;
        b1.caption = subformula1.toString();
        sep.childBags.add(b1);

        Bag b2 = subformula2.evaluateProver(s, varassign, onprogress);
        b2.assignment = assignment;
        b2.caption = subformula2.toString();
        sep.childBags.add(b2);
        b.eval = (b1.eval || b2.eval);
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
        // "or", so this is a player 0 position.
        parent.player1Position = false;
        game.addVertex(parent);

        GameGraphResult c1 = subformula1.constructGameGraph(
            s, varassign, game, new Vector2D(coor.getX() + X_OFFSET, coor.getY()));
        game.addVertex(c1.position);

        game.addEdge(game.createEdge(parent, c1.position));

        GameGraphResult c2 = subformula2.constructGameGraph(
            s, varassign, game, new Vector2D(coor.getX() + X_OFFSET, coor.getY() + c1.height + 1));
        game.addVertex(c2.position);

        game.addEdge(game.createEdge(parent, c2.position));

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
