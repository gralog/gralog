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
    
    public Object Run(Automaton a, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        Automaton result = new Automaton();
        PowersetConstructionTreeNode tree = new PowersetConstructionTreeNode(null,null,null);
        
       
        Set<State> Q0 = new HashSet<>();
        Set<Vertex> Q = a.getVertices();
        Set<Edge> delta = a.getEdges();
        for(Vertex v : Q)
            if(v instanceof State && ((State)v).StartState)
                Q0.add((State)v);
        Q0 = a.EpsilonHull(Q0);
        State q0 = tree.getContentForSet(a, result, Q0);
        q0.StartState = true;


        Set<Character> Alphabet = new HashSet<>();
        for(Edge e : delta)
            if(e instanceof Transition)
            {
                String s = ((Transition)e).Symbol;
                for(int i = 0; i < s.length(); i++)
                    Alphabet.add(s.charAt(i));
            }
        
        
        Queue<Set<State>> queue = new LinkedList<>();
        queue.add(Q0);
        Set<State> knownStates = new HashSet<>();
        knownStates.add(q0);
        while(!queue.isEmpty())
        {
            Set<State> front = queue.remove();
            
            for(Character c : Alphabet)
            {
                Set<State> cSuccessor = new HashSet<>();
                for(Edge e : delta)
                {
                    if(e instanceof Transition && front.contains(e.getSource()) && ((Transition)e).Symbol.equals(c.toString()))
                        cSuccessor.add((State)e.getTarget());
                }
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
