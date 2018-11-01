/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.regularexpression;

import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.rendering.Vector2D;
import gralog.structure.Vertex;

/**
 *
 */
public class RegularExpressionConcatenation extends RegularExpression {

    RegularExpression regexp1 = null;
    RegularExpression regexp2 = null;

    public RegularExpressionConcatenation(RegularExpression regexp1,
        RegularExpression regexp2) {
        this.regexp1 = regexp1;
        this.regexp2 = regexp2;
    }

    @Override
    public String toString() {
        return regexp1.toString() + regexp2.toString();
    }

    @Override
    public Automaton thompsonConstruction(double scale) {

        Automaton a = regexp1.thompsonConstruction(scale);
        Automaton b = regexp2.thompsonConstruction(scale);

        // Determine new positions for the states
        double aMaxX = a.maximumCoordinate(0);
        double aMaxY = a.maximumCoordinate(1);
        double bMaxY = b.maximumCoordinate(1);

        // Set the new positions of the states
        Vector2D aOffset = new Vector2D(
            0d, bMaxY > aMaxY ? (bMaxY - aMaxY) / 2d : 0d
        );
        a.move(aOffset);

        Vector2D bOffset = new Vector2D(
            aMaxX + scale,
            aMaxY > bMaxY ? (aMaxY - bMaxY) / 2d : 0d
        );
        b.move(bOffset);

        // Connect the final states of A to initial states of B
        for (Vertex v : a.getVertices()) {
            if (((State) v).finalState) {
                for (Vertex w : b.getVertices()) {
                    if (((State) w).startState) {
                        Transition t = a.createEdge((State) v, (State) w);
                        t.symbol = ""; // epsilon transition
                        a.addEdge(t);
                    }
                }
            }
        }

        // disable final states of A and initial states of B
        for (Vertex v : a.getVertices()) {
            ((State) v).finalState = false;
        }
        for (Vertex w : b.getVertices()) {
            ((State) w).startState = false;
        }

        // make a the union of a and b
        a.addVertices(b.getVertices());
        a.addEdges(b.getEdges());

        b.clear();

        return a;
    }

}
