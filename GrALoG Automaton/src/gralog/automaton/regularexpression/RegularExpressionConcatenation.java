/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.structure.*;
import gralog.automaton.*;
import java.util.Vector;

/**
 *
 * @author viktor
 */
public class RegularExpressionConcatenation extends RegularExpression {
    
    RegularExpression regexp1 = null;
    RegularExpression regexp2 = null;
    
    public RegularExpressionConcatenation(RegularExpression regexp1, RegularExpression regexp2) {
        this.regexp1 = regexp1;
        this.regexp2 = regexp2;
    }
    
    @Override
    public String toString() {
        return "(" + regexp1.toString() + ")(" + regexp2.toString() + ")";
    }

    @Override
    public Automaton ThompsonConstruction() {
        
        Automaton a = regexp1.ThompsonConstruction();
        Automaton b = regexp2.ThompsonConstruction();

        // Determine new positions for the states
        Double aMaxX = a.MaximumCoordinate(0);
        Double aMaxY = a.MaximumCoordinate(1);
        Double bMaxY = b.MaximumCoordinate(1);

        // Set the new positions of the states
        Vector<Double> aOffset = new Vector<Double>();
        aOffset.add(0d);
        aOffset.add(bMaxY > aMaxY ? (bMaxY - aMaxY)/2d : 0d);
        a.Move(aOffset);
        
        Vector<Double> bOffset = new Vector<Double>();
        bOffset.add(aMaxX + 50d);
        bOffset.add(aMaxY > bMaxY ? (aMaxY - bMaxY)/2d : 0d);
        b.Move(bOffset);
        
        // Connect the final states of A to initial states of B
        for(Vertex v : a.getVertices())
            if(((State)v).FinalState) {
                for(Vertex w : b.getVertices())
                    if(((State)w).StartState) {
                        Transition t = a.CreateEdge();
                        t.Symbol = ""; // epsilon transition
                        t.source = v;
                        t.target = w;
                        a.AddEdge(t);
                    }
            }
        
        // disable final states of A and initial states of B
        for(Vertex v : a.getVertices())
            ((State)v).FinalState = false;
        for(Vertex w : b.getVertices())
            ((State)w).StartState = false;
        
        // make a the union of a and b
        a.getVertices().addAll(b.getVertices());
        a.getEdges().addAll(b.getEdges());
        // clear b
        b.getVertices().clear();
        b.getEdges().clear();
        
        return a;
    }
    
}
