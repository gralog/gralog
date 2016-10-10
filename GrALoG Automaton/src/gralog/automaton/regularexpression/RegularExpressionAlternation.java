/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import gralog.rendering.Vector2D;
import gralog.structure.Vertex;
import java.util.Vector;

/**
 *
 * @author viktor
 */
public class RegularExpressionAlternation extends RegularExpression {
    
    RegularExpression regexp1 = null;
    RegularExpression regexp2 = null;
    
    public RegularExpressionAlternation(RegularExpression regexp1, RegularExpression regexp2) {
        this.regexp1 = regexp1;
        this.regexp2 = regexp2;
    }
    
    @Override
    public String toString() {
        return "(" + regexp1.toString() + ")+(" + regexp2.toString() + ")";
    }
    
    @Override
    public Automaton ThompsonConstruction(Double scale) {
        Automaton a = regexp1.ThompsonConstruction(scale);
        Automaton b = regexp2.ThompsonConstruction(scale);
        
        // Determine new positions for the states
        Double aMaxX = a.MaximumCoordinate(0);
        Double aMaxY = a.MaximumCoordinate(1);
        Double bMaxX = b.MaximumCoordinate(0);
        Double bMaxY = b.MaximumCoordinate(1);
        
        // Set the new positions of the states
        Vector2D aOffset = new Vector2D(
                (bMaxX > aMaxX ? (bMaxX - aMaxX)/2d : 0d) + scale, 0d);
        a.Move(aOffset);
        
        Vector2D bOffset = new Vector2D(
                (aMaxX > bMaxX ? (aMaxX - bMaxX)/2d : 0d) + scale,
                (aMaxY > bMaxY ? (2*aMaxY - bMaxY) : bMaxY) + scale
        );
        b.Move(bOffset);

        
        // create new, common start- and final-state
        State s = a.CreateVertex();
        s.StartState = true;
        s.Coordinates = new Vector2D(0d, Math.max(aMaxY, bMaxY) + scale/2.0d);
        
        State t = a.CreateVertex();
        t.FinalState = true;
        t.Coordinates = new Vector2D(
                Math.max(aMaxX, bMaxX) + scale*2d,
                Math.max(aMaxY, bMaxY) + scale/2d
        );

        
        // make a the union of a and b
        a.getVertices().addAll(b.getVertices());
        a.getEdges().addAll(b.getEdges());
        // clear b
        b.getVertices().clear();
        b.getEdges().clear();
        
        
        // Connect the old start and final states to the new start and final states
        for(Vertex v : a.getVertices()) {
            if(((State)v).StartState) {
                Transition e = a.CreateEdge(s,(State)v);
                e.Symbol = ""; // epsilon transition
                a.AddEdge(e);
            }
            if(((State)v).FinalState) {
                Transition e = a.CreateEdge((State)v,t);
                e.Symbol = ""; // epsilon transition
                a.AddEdge(e);
            }
            ((State)v).FinalState = false;
            ((State)v).StartState = false;
        }
        
        
        a.AddVertex(s);
        a.AddVertex(t);
        return a;
    }
}
