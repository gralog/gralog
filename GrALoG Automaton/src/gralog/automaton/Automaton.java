/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton;

import gralog.structure.*;
import gralog.plugins.*;

import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author viktor
 */
@StructureDescription(
    name="Automaton",
    text="",
    url="https://en.wikipedia.org/wiki/Automata_theory"
)
@XmlName(name="automaton")
public class Automaton extends Structure<State, Transition> {
    
    @Override
    public State CreateVertex() {
        return new State();
    }
    
    @Override
    public Transition CreateEdge() {
        return new Transition();
    }
 
    
    public HashSet<State> EpsilonHull(Set<State> start) {
        HashSet<State> result = new HashSet<State>();
        HashSet<State> lastiteration = new HashSet<State>();
        HashSet<State> currentiteration = new HashSet<State>();
        
        result.addAll(start);
        lastiteration.addAll(start);
        
        while(!lastiteration.isEmpty()) {
            
            for(Edge e : getEdges())
                if(e instanceof Transition)
                    if(((Transition)e).Symbol.equals("")) // epsilon transition
                        if(lastiteration.contains(e.source))
                            if(!result.contains(e.target))
                                currentiteration.add((State)e.target);
            
            result.addAll(currentiteration);
            HashSet<State> temp = lastiteration;
            lastiteration = currentiteration;
            currentiteration = temp;
            currentiteration.clear();
        }
        
        return result;
    }
    
}
