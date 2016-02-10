/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.automaton.*;
import gralog.progresshandler.ProgressHandler;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Rabin-Scott Powerset Construction",
  text="",
  url="https://en.wikipedia.org/wiki/Powerset_construction"
)
public class PowersetConstruction extends Algorithm {
    
    public Object Run(Automaton a, AlgorithmParameters p, ProgressHandler onprogress) throws Exception {
        /*throw new Exception("this algorithm isn't implemented yet.\n"
                + "it is only a test of the exception-handling of the GUI");
        */
        
        Automaton result = new Automaton();
        PowersetConstructionTreeNode tree = new PowersetConstructionTreeNode(null,null,null);
        
        Set<Character> Alphabet = new HashSet<Character>();
        Alphabet.add('a');
        Alphabet.add('b');
        
        Set<State> Q0 = new HashSet<State>();
        Set<Vertex> Q = a.getVertices();
        Set<Edge> delta = a.getEdges();
        for(Vertex v : Q)
            if(v instanceof State && ((State)v).StartState)
                Q0.add((State)v);
        Q0 = a.EpsilonHull(Q0);
        State q0 = tree.getContentForSet(a, result, Q0);
        q0.StartState = true;

        
        Queue<Set<State>> queue = new LinkedList<Set<State>>();
        queue.add(Q0);
        Set<State> knownStates = new HashSet<State>();
        knownStates.add(q0);
        while(!queue.isEmpty())
        {
            Set<State> front = queue.remove();
            
            for(Character c : Alphabet)
            {
                Set<State> cSuccessor = new HashSet<State>();
                for(Edge e : delta)
                    if(e instanceof Transition && front.contains(e.source) && ((Transition)e).Symbol.equals(c.toString()))
                        cSuccessor.add((State)e.target);
                cSuccessor = a.EpsilonHull(cSuccessor);
                
                State src = tree.getContentForSet(a, result, front);
                State dst = tree.getContentForSet(a, result, cSuccessor);
                
                
                if(!knownStates.contains(dst))
                {
                    for(State cSuccessorElement : cSuccessor)
                        if(cSuccessorElement.FinalState)
                            dst.FinalState = true;
                    queue.add(cSuccessor);
                    knownStates.add(dst);
                }
                
                
                Transition trans = result.CreateEdge(src, dst);
                trans.Symbol = c.toString();
                result.AddEdge(trans);
                
            }
            
        }
        
        
        return result;
    }
    
}
