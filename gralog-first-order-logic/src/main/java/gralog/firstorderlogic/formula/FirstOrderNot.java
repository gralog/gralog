/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
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
    public Subformula evaluateProver(Structure s, HashMap<String, Vertex> varassign,
        ProgressHandler onprogress) throws Exception {

        Subformula b1 = subformula1.evaluateProver(s, varassign, onprogress);
        b1.assignment = new HashMap<>(varassign);
        b1.subformula = " (" + subformula1.toString() + " )";

        Subformula b = new Subformula();
        b.value = !b1.value;
        b.children.add(b1);
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
