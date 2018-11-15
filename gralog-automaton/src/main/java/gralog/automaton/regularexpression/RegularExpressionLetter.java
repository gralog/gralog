/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.regularexpression;

import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;

/**
 *
 */
public class RegularExpressionLetter extends RegularExpression {

    String string;

    public RegularExpressionLetter(Character letter) {
        this.string = "" + letter;
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public Automaton thompsonConstruction(double scale) {
        Automaton a = new Automaton();

        State s = a.addVertex();
        s.startState = true;
        s.setCoordinates(0d, 0d);

        State t = s;
        for (int i = 0; i < string.length(); i++) {
            s = t;

            t = a.addVertex();
            t.setCoordinates(scale * i + scale, 0d);

            Transition e = a.createEdge(null);
            e.setSource(s);
            e.setTarget(t);
            e.symbol = "" + string.charAt(i);
            a.addEdge(e);
        }
        t.finalState = true;

        return a;
    }
}
