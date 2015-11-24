/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import java.util.Vector;

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
    public Automaton ThompsonConstruction() {
        Automaton a = new Automaton();
        
        State s = a.CreateVertex();
        s.StartState = true;
        s.Coordinates = new Vector<Double>();
        s.Coordinates.add(0d);
        s.Coordinates.add(0d);
        a.AddVertex(s);
        
        State t = s;
        for(int i = 0; i < string.length(); i++)
        {
            s = t;
            
            t = a.CreateVertex();
            t.Coordinates.add(50d * i + 50d);
            t.Coordinates.add(0d);
            a.AddVertex(t);
        
            Transition e = a.CreateEdge();
            e.source = s;
            e.target = t;
            e.Symbol = ""+string.charAt(i);
            a.AddEdge(e);
        }
        t.FinalState = true;

        return a;
    }

}
