/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import gralog.algorithm.*;
import gralog.automaton.*;
import gralog.plugins.Description;
import gralog.structure.*;
import java.util.HashSet;

/**
 *
 * @author viktor
 */
@Description(
  name="Word Acceptance",
  text="",
  url="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton#Example"
)
public class WordAcceptance extends Algorithm {

    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new GenericAlgorithmParameter<String>("");
    }
    
    public Object Run(Automaton s, AlgorithmParameters p) {
        GenericAlgorithmParameter<String> sp = (GenericAlgorithmParameter<String>)(p);
        return Accepts(s, sp.parameter) ? "true" : "false";
    }
    
    public boolean Accepts(Automaton automaton, String str) {
        
        HashSet<State> CurrentStates = new HashSet<State>();
        for(Vertex v : automaton.getVertices())
            if(v instanceof State)
                if(((State)v).StartState)
                    CurrentStates.add((State)v);
        CurrentStates = automaton.EpsilonHull(CurrentStates);

        
        HashSet<State> NextStates = new HashSet<State>();
        for(int i = 0; i < str.length(); i++)
        {
            for(State s : CurrentStates)
            {
                for(Edge e : automaton.getEdges())
                    if(e.source == s)
                        if(e instanceof Transition)
                            if(((Transition)e).Symbol.equals(""+str.charAt(i)))
                                NextStates.add((State)((Transition)e).target);
            }
            NextStates = automaton.EpsilonHull(NextStates);
            
            HashSet<State> Temp = CurrentStates;
            CurrentStates = NextStates;
            NextStates = Temp;
            NextStates.clear();
        }
            
        
        for(State s : CurrentStates)
            if(s.FinalState)
                return true;
        return false;
    }
    
}
