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
        HashSet<State> result = new HashSet<>();
        HashSet<State> lastiteration = new HashSet<>();
        HashSet<State> currentiteration = new HashSet<>();
        
        result.addAll(start);
        lastiteration.addAll(start);
        
        while(!lastiteration.isEmpty()) {
            
            for(State s : lastiteration)
                for(Edge e : s.getConnectedEdges())
                    if(e instanceof Transition)
                        if(((Transition)e).Symbol.equals("")) // epsilon transition
                            if(!result.contains((State)e.getTarget()))
                                currentiteration.add((State)e.getTarget());
            
            result.addAll(currentiteration);
            HashSet<State> temp = lastiteration;
            lastiteration = currentiteration;
            currentiteration = temp;
            currentiteration.clear();
        }
        
        return result;
    }
    
}
