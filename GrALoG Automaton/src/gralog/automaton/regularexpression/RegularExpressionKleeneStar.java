/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import gralog.structure.*;
import java.util.Vector;

/**
 *
 * @author viktor
 */
public class RegularExpressionKleeneStar extends RegularExpression {
        
    RegularExpression regexp = null;
    
    public RegularExpressionKleeneStar(RegularExpression regexp) {
        this.regexp = regexp;
    }
    
    @Override
    public String toString() {
        return "(" + regexp.toString() + ")*";
    }

    @Override
    public Automaton ThompsonConstruction(Double scale) {
        
        Automaton a = regexp.ThompsonConstruction(scale);
        
        // Determine new positions for the states
        Double aMaxX = a.MaximumCoordinate(0);
        Double aMaxY = a.MaximumCoordinate(1);

        // set new positions
        Vector<Double> aOffset = new Vector<Double>();
        aOffset.add(scale);
        aOffset.add(scale);
        a.Move(aOffset);

        
        // create new, common start- and final-state
        State s = a.CreateVertex();
        s.StartState = true;
        s.Coordinates.add(0d);
        s.Coordinates.add(aMaxY/2d + scale);
        
        State t = a.CreateVertex();
        t.FinalState = true;
        t.Coordinates.add(aMaxX + 2d*scale);
        t.Coordinates.add(aMaxY/2d + scale);

        // Connect the new start and final states with epsilon transitions
        Transition st = a.CreateEdge(s,t);
        st.Symbol = ""; // epsilon transition
        a.AddEdge(st);
        st.intermediatePoints.add(new EdgeIntermediatePoint(scale, 0d));
        st.intermediatePoints.add(new EdgeIntermediatePoint(aMaxX + scale, 0d));

        Transition ts = a.CreateEdge(t,s);
        ts.Symbol = ""; // epsilon transition
        a.AddEdge(ts);
        ts.intermediatePoints.add(new EdgeIntermediatePoint(aMaxX + scale, aMaxY + 2d*scale));
        ts.intermediatePoints.add(new EdgeIntermediatePoint(scale, aMaxY + 2d*scale));
        
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
