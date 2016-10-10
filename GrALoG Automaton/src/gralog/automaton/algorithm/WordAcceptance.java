/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import gralog.algorithm.*;
import gralog.automaton.*;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Word Acceptance",
  text="",
  url="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton#Example"
)
public class WordAcceptance extends Algorithm {

    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new StringAlgorithmParameter("");
    }
    
    public Object Run(Automaton s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) {
        StringAlgorithmParameter sp = (StringAlgorithmParameter)(p);
        return Accepts(s, sp.parameter) ? "true" : "false";
    }
    
    public boolean Accepts(Automaton automaton, String str) {
        
        HashSet<State> CurrentStates = new HashSet<>();
        for(Vertex v : automaton.getVertices())
            if(v instanceof State)
                if(((State)v).StartState)
                    CurrentStates.add((State)v);
        CurrentStates = automaton.EpsilonHull(CurrentStates);

        HashSet<State> NextStates = new HashSet<>();
        for(int i = 0; i < str.length(); i++)
        {
            String stri = ""+str.charAt(i);
            for(State s : CurrentStates)
                for(Edge e : s.getConnectedEdges())
                    if(e.getSource() == s)
                        if(e instanceof Transition)
                            if(((Transition)e).Symbol.equals(stri))
                                NextStates.add((State)e.getTarget());
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
