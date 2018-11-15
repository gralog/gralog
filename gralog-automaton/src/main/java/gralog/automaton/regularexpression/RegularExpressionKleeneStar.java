/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.regularexpression;

import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.rendering.Vector2D;
import gralog.structure.EdgeIntermediatePoint;
import gralog.structure.Vertex;


/**
 *
 */
public class RegularExpressionKleeneStar extends RegularExpression {

    RegularExpression regexp = null;

    public RegularExpressionKleeneStar(RegularExpression regexp) {
        this.regexp = regexp;
    }

    @Override
    public String toString() {
        if (regexp instanceof RegularExpressionLetter) {
            return regexp.toString() + "*";
        }
        return "(" + regexp.toString() + ")*";
    }

    @Override
    public Automaton thompsonConstruction(double scale) {

        Automaton a = regexp.thompsonConstruction(scale);

        // Determine new positions for the states
        double aMaxX = a.maximumCoordinate(0);
        double aMaxY = a.maximumCoordinate(1);

        // set new positions
        Vector2D aOffset = new Vector2D(scale, scale);
        a.move(aOffset);

        // create new, common start- and final-state
        State s = a.addVertex();
        s.startState = true;
        s.setCoordinates(0d, aMaxY / 2d + scale);

        State t = a.addVertex();
        t.finalState = true;
        t.setCoordinates(
            aMaxX + 2d * scale,
            aMaxY / 2d + scale
        );

        // Connect the new start and final states with epsilon transitions
        Transition st = a.createEdge(s, t);
        st.symbol = ""; // epsilon transition
        a.addEdge(st);
        st.intermediatePoints.add(new EdgeIntermediatePoint(scale, 0d));
        st.intermediatePoints.add(new EdgeIntermediatePoint(aMaxX + scale, 0d));

        Transition ts = a.createEdge(t, s);
        ts.symbol = ""; // epsilon transition
        a.addEdge(ts);
        ts.intermediatePoints.add(new EdgeIntermediatePoint(aMaxX + scale, aMaxY + 2d * scale));
        ts.intermediatePoints.add(new EdgeIntermediatePoint(scale, aMaxY + 2d * scale));

        // Connect the old start and final states to the new start and final states
        for (Vertex v : a.getVertices()) {
            if (((State) v).startState) {
                Transition e = a.createEdge(s, (State) v);
                e.symbol = ""; // epsilon transition
                a.addEdge(e);
            }
            if (((State) v).finalState) {
                Transition e = a.createEdge((State) v, t);
                e.symbol = ""; // epsilon transition
                a.addEdge(e);
            }
            ((State) v).finalState = false;
            ((State) v).startState = false;
        }

        return a;
    }
}
