/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import gralog.rendering.Vector2D;
import gralog.structure.Vertex;

/**
 *
 */
public class RegularExpressionAlternation extends RegularExpression {

    RegularExpression left = null;
    RegularExpression right = null;

    public RegularExpressionAlternation(RegularExpression left,
            RegularExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        String r = right.toString();
        if (right instanceof RegularExpressionAlternation)
            r = "(" + r + ")";
        return left.toString() + " | " + r;
    }

    @Override
    public Automaton thompsonConstruction(double scale) {
        Automaton a = left.thompsonConstruction(scale);
        Automaton b = right.thompsonConstruction(scale);

        // Determine new positions for the states
        double aMaxX = a.maximumCoordinate(0);
        double aMaxY = a.maximumCoordinate(1);
        double bMaxX = b.maximumCoordinate(0);
        double bMaxY = b.maximumCoordinate(1);

        // Set the new positions of the states
        Vector2D aOffset = new Vector2D(
                (bMaxX > aMaxX ? (bMaxX - aMaxX) / 2d : 0d) + scale, 0d);
        a.move(aOffset);

        Vector2D bOffset = new Vector2D(
                (aMaxX > bMaxX ? (aMaxX - bMaxX) / 2d : 0d) + scale,
                (aMaxY > bMaxY ? (2 * aMaxY - bMaxY) : bMaxY) + scale
        );
        b.move(bOffset);

        // create new, common start- and final-state
        State s = a.createVertex();
        s.startState = true;
        s.coordinates = new Vector2D(0d, Math.max(aMaxY, bMaxY) + scale / 2.0d);

        State t = a.createVertex();
        t.finalState = true;
        t.coordinates = new Vector2D(
                Math.max(aMaxX, bMaxX) + scale * 2d,
                Math.max(aMaxY, bMaxY) + scale / 2d
        );

        // make a the union of a and b
        a.getVertices().addAll(b.getVertices());
        a.getEdges().addAll(b.getEdges());
        // clear b
        b.getVertices().clear();
        b.getEdges().clear();

        // Connect the old start and final states to the new start and final states
        for (Vertex v : a.getVertices()) {
            if (((State) v).startState) {
                Transition e = a.createEdge(s, (State) v);
                e.Symbol = ""; // epsilon transition
                a.addEdge(e);
            }
            if (((State) v).finalState) {
                Transition e = a.createEdge((State) v, t);
                e.Symbol = ""; // epsilon transition
                a.addEdge(e);
            }
            ((State) v).finalState = false;
            ((State) v).startState = false;
        }

        a.addVertex(s);
        a.addVertex(t);
        return a;
    }
}
