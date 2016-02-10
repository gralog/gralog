/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.regularexpression;

import gralog.automaton.*;
import gralog.rendering.VectorND;
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
    public Automaton ThompsonConstruction(Double scale) {
        Automaton a = new Automaton();
        
        State s = a.CreateVertex();
        s.StartState = true;
        s.Coordinates = new VectorND();
        s.Coordinates.add(0d);
        s.Coordinates.add(0d);
        a.AddVertex(s);
        
        State t = s;
        for(int i = 0; i < string.length(); i++)
        {
            s = t;
            
            t = a.CreateVertex();
            t.Coordinates.add(scale*i + scale);
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
