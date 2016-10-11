/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.structure.*;
import gralog.automaton.*;
import gralog.rendering.Vector2D;

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
        return "(" + regexp1.toString() + ")(" + regexp2.toString() + ")";
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
        for (Vertex v : a.getVertices())
            if (((State) v).finalState) {
                for (Vertex w : b.getVertices())
                    if (((State) w).startState) {
                        Transition t = a.createEdge((State) v, (State) w);
                        t.Symbol = ""; // epsilon transition
                        a.addEdge(t);
                    }
            }

        // disable final states of A and initial states of B
        for (Vertex v : a.getVertices())
            ((State) v).finalState = false;
        for (Vertex w : b.getVertices())
            ((State) w).startState = false;

        // make a the union of a and b
        a.getVertices().addAll(b.getVertices());
        a.getEdges().addAll(b.getEdges());
        // clear b
        b.getVertices().clear();
        b.getEdges().clear();

        return a;
    }

}
