/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.rendering.VectorND;
import gralog.automaton.*;
import gralog.progresshandler.*;

import java.util.*;

/**
 *
 * @author viktor
 */

@AlgorithmDescription(
  name="Minimization",
  text="",
  url="https://en.wikipedia.org/wiki/DFA_minimization"
)
public class Minimization extends Algorithm {
    
   
    public Object Run(Automaton s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) {
        return Minimize((Automaton)s);
    }
    
    
    public static Automaton Minimize(Automaton automaton)
    {
        int n = 0;
        Vector<State> States = new Vector<State>();
        HashMap<State,Integer> StateIndex = new HashMap<State,Integer>();
        for(Vertex v : automaton.getVertices())
        {
            States.add((State)v);
            StateIndex.put((State)v, n++);
        }


        
        // Create Initial Table
        Vector<Vector<Boolean>> Table = new Vector<Vector<Boolean>>();
        for(int y = 0; y < n; y++) // iterate over table-rows
        {
            Table.add(new Vector<Boolean>());
            for(int x = 0; x < n; x++) // iterate over row-cells
                Table.get(y).add(States.get(y).FinalState != States.get(x).FinalState); // xor
        }
        
        
        
        // Refine Table
        for(int i = 0; i < n; i++) // n times do
            for(int y = 0; y < n; y++) // iterate over table-rows
                for(int x = 0; x < y; x++) // iterate over row-cells
                    if(Table.get(y).get(x) == false) {
                    
                        for(Edge tx : automaton.getEdges()) {
                            if(tx.getSource() != States.get(x))
                                continue;
                            
                            for(Edge ty : automaton.getEdges()) {
                                if(ty.getSource() != States.get(y))
                                    continue;
                                if(!((Transition)ty).Symbol.equals(((Transition)tx).Symbol))
                                    continue;
        
                                // x and y have distinguishable "symbol"-successors
                                // hence x and y are distinguishable
                                if(Table.get(StateIndex.get(tx.getTarget())).get(StateIndex.get(ty.getTarget())) == true)
                                {
                                    Table.get(x).set(y, true);
                                    Table.get(y).set(x, true);
                                    break;
                                }
        
                            }
                        }
                    }

                    
        
        // Build Resulting Automaton
        Automaton result = new Automaton();
        
        // Build State Set
        HashMap<State,State> NerodeRelation_EquivalenceClass = new HashMap<State,State>();
        HashMap<State,Integer> NerodeRelation_EquivalenceClassSize = new HashMap<State,Integer>();
        for(int y = 0; y < n; y++)
        {
            State equivalent = null;
            for(int x = y-1; x >= 0 && equivalent == null; x--)
                if(Table.get(y).get(x) == false)
                    equivalent = NerodeRelation_EquivalenceClass.get(States.get(x));

            if(equivalent == null)
            {
                equivalent = (State)result.CreateVertex();
                result.AddVertex(equivalent);
                equivalent.Coordinates = new VectorND(); // initial coordinate (0,0)
                equivalent.Coordinates.add(0.0d);
                equivalent.Coordinates.add(0.0d);
                NerodeRelation_EquivalenceClassSize.put(equivalent, 0);
            }
            
            // add current coordinates to coordinates of <equivalent> state
            equivalent.Coordinates.set(0, equivalent.Coordinates.get(0) + States.get(y).Coordinates.get(0));
            equivalent.Coordinates.set(1, equivalent.Coordinates.get(1) + States.get(y).Coordinates.get(1));
            NerodeRelation_EquivalenceClass.put(States.get(y), equivalent);
            NerodeRelation_EquivalenceClassSize.put(equivalent, NerodeRelation_EquivalenceClassSize.get(equivalent)+1);
        }

        // divide coordinates by number of equivalent states
        // i.e. resulting state is in the center of the equivalent states
        for(Vertex v : result.getVertices()) {
            v.Coordinates.set(0, v.Coordinates.get(0) / NerodeRelation_EquivalenceClassSize.get((State)v));
            v.Coordinates.set(1, v.Coordinates.get(1) / NerodeRelation_EquivalenceClassSize.get((State)v));
        }
        
        // Set Start-State
        for(int y = 0; y < n; y++)
            if(States.get(y).StartState)
                NerodeRelation_EquivalenceClass.get(States.get(y)).StartState = true;
            
        // Set Final States
        for(int y = 0; y < n; y++)
            NerodeRelation_EquivalenceClass.get(States.get(y)).FinalState = States.get(y).FinalState;
        
        // Set Transitions
        HashMap<State, String> TransitionDefined = new HashMap<State, String>();
        for(Edge e : automaton.getEdges())
        {
            Transition t = (Transition)e;
            State resultSource = NerodeRelation_EquivalenceClass.get(t.getSource());
            
            if(!TransitionDefined.containsKey(resultSource))
	        TransitionDefined.put(resultSource, "");
	        
	    String foo = TransitionDefined.get(resultSource);
	    if(foo.indexOf(""+t.Symbol) < 0)
	    {
	        TransitionDefined.put(resultSource, ""+t.Symbol);
	        Transition resultTransition = (Transition)result.CreateEdge();
	        resultTransition.Symbol = t.Symbol;
	        resultTransition.setSource(  NerodeRelation_EquivalenceClass.get(t.getSource())  );
	        resultTransition.setTarget(  NerodeRelation_EquivalenceClass.get(t.getTarget())  );
	        result.AddEdge(resultTransition);
	    }
        }
        
        
        return result;
    }
    
}





