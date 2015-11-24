/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.structure.Vertex;
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
    public Automaton ThompsonConstruction() {
        
        Automaton a = regexp.ThompsonConstruction();
        
        // Determine new positions for the states
        Double aMaxX = a.MaximumCoordinate(0);
        Double aMaxY = a.MaximumCoordinate(1);

        // set new positions
        Vector<Double> aOffset = new Vector<Double>();
        aOffset.add(50d);
        aOffset.add(50d);
        a.Move(aOffset);

        
        // create new, common start- and final-state
        State s = a.CreateVertex();
        s.StartState = true;
        s.Coordinates.add(0d);
        s.Coordinates.add(aMaxY/2d + 50d);
        
        State t = a.CreateVertex();
        t.FinalState = true;
        t.Coordinates.add(aMaxX + 100d);
        t.Coordinates.add(aMaxY/2d + 50d);

        // Connect the new start and final states with epsilon transitions
        Transition st = a.CreateEdge();
        st.Symbol = ""; // epsilon transition
        st.source = s;
        st.target = t;
        a.AddEdge(st);
        Vector<Double> st1 = new Vector<Double>();
        st1.add(50d);
        st1.add(0d);
        Vector<Double> st2 = new Vector<Double>();
        st2.add(aMaxX + 50d);
        st2.add(0d);
        st.Coordinates.add(st1);
        st.Coordinates.add(st2);

        Transition ts = a.CreateEdge();
        ts.Symbol = ""; // epsilon transition
        ts.source = t;
        ts.target = s;
        a.AddEdge(ts);        
        Vector<Double> ts1 = new Vector<Double>();
        ts1.add(aMaxX + 50d);
        ts1.add(aMaxY + 100d);
        Vector<Double> ts2 = new Vector<Double>();
        ts2.add(50d);
        ts2.add(aMaxY + 100d);
        ts.Coordinates.add(ts1);
        ts.Coordinates.add(ts2);        
        
        // Connect the old start and final states to the new start and final states
        for(Vertex v : a.getVertices()) {
            if(((State)v).StartState) {
                Transition e = a.CreateEdge();
                e.Symbol = ""; // epsilon transition
                e.source = s;
                e.target = v;
                a.AddEdge(e);
            }
            if(((State)v).FinalState) {
                Transition e = a.CreateEdge();
                e.Symbol = ""; // epsilon transition
                e.source = v;
                e.target = t;
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
