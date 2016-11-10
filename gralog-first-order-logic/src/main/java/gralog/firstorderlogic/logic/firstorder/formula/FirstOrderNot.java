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
public class FirstOrderNot extends FirstOrderFormula {

    FirstOrderFormula subformula1;

    public FirstOrderNot(FirstOrderFormula subformula1) {
        this.subformula1 = subformula1;
    }

    @Override
    public String toString(FormulaPosition pos, FormulaEndPosition endPos) {
        return "¬" + subformula1.toString(FormulaPosition.Not, endPos);
    }

    @Override
    public boolean evaluate(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {
        return !subformula1.evaluate(s, varassign, onprogress);
    }

    @Override
    public Bag evaluateProver(Structure s, HashMap<String, Vertex> varassign,
            ProgressHandler onprogress) throws Exception {

        Bag b = new Bag();
        String assignment = new String();
        for (String str : varassign.keySet()) {
            assignment += " [ " + str + " | " + varassign.get(str).label + " ] ";
        }

        Bag b1 = subformula1.evaluateProver(s, varassign, onprogress);
        b1.assignment = assignment;
        b1.caption = " (" + subformula1.toString() + " )";
        b.eval = !b1.eval;
        b.ChildBags.add(b1);
        return b;
    }

    @Override
    public GameGraphResult constructGameGraph(Structure s,
            HashMap<String, Vertex> varassign, FiniteGame game,
            Vector2D coor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<String> variables() throws Exception {
        Set<String> result = new HashSet<>();
        result.addAll(subformula1.variables());
        return result;
    }

    @Override
    public String substitute(HashMap<String, String> replace) throws Exception {
        return " \neg " + subformula1.substitute(replace);
    }
}
