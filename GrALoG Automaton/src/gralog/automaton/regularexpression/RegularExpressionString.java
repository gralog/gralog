/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import gralog.rendering.Vector2D;

/**
 *
 * @author viktor
 */
public class RegularExpressionString extends RegularExpression {

    String string;

    public RegularExpressionString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public Automaton thompsonConstruction(double scale) {
        Automaton a = new Automaton();

        State s = a.createVertex();
        s.startState = true;
        s.coordinates = new Vector2D(0d, 0d);
        a.addVertex(s);

        State t = s;
        for (int i = 0; i < string.length(); i++) {
            s = t;

            t = a.createVertex();
            t.coordinates = new Vector2D(scale * i + scale, 0d);
            a.addVertex(t);

            Transition e = a.createEdge();
            e.setSource(s);
            e.setTarget(t);
            e.Symbol = "" + string.charAt(i);
            a.addEdge(e);
        }
        t.finalState = true;

        return a;
    }
}
