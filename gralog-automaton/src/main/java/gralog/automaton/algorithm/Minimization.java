/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.algorithm;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.rendering.Vector2D;
import gralog.automaton.*;
import gralog.progresshandler.*;

import java.util.*;

/**
 *
 */
@AlgorithmDescription(
        name = "Minimization",
        text = "",
        url = "https://en.wikipedia.org/wiki/DFA_minimization"
)
public class Minimization extends Algorithm {

    public Object run(Automaton s, AlgorithmParameters p, Set<Object> selection,
            ProgressHandler onprogress) {
        return minimize((Automaton) s);
    }

    public static Automaton minimize(Automaton automaton) {
        int n = 0;
        ArrayList<State> States = new ArrayList<>();
        HashMap<State, Integer> StateIndex = new HashMap<>();
        for (Vertex v : automaton.getVertices()) {
            States.add((State) v);
            StateIndex.put((State) v, n++);
        }

        // Create Initial Table
        ArrayList<ArrayList<Boolean>> Table = new ArrayList<>();
        for (int y = 0; y < n; y++) // iterate over table-rows
        {
            Table.add(new ArrayList<>());
            for (int x = 0; x < n; x++) // iterate over row-cells
                Table.get(y).add(States.get(y).finalState != States.get(x).finalState); // xor
        }

        // Refine Table
        for (int i = 0; i < n; i++) // n times do
            for (int y = 0; y < n; y++) // iterate over table-rows
                for (int x = 0; x < y; x++) // iterate over row-cells
                    if (Table.get(y).get(x) == false) {

                        for (Edge tx : automaton.getEdges()) {
                            if (tx.getSource() != States.get(x))
                                continue;

                            for (Edge ty : automaton.getEdges()) {
                                if (ty.getSource() != States.get(y))
                                    continue;
                                if (!((Transition) ty).Symbol.equals(((Transition) tx).Symbol))
                                    continue;

                                // x and y have distinguishable "symbol"-successors
                                // hence x and y are distinguishable
                                if (Table.get(StateIndex.get(tx.getTarget())).get(StateIndex.get(ty.getTarget())) == true) {
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
        HashMap<State, State> NerodeRelation_EquivalenceClass = new HashMap<>();
        HashMap<State, Integer> NerodeRelation_EquivalenceClassSize = new HashMap<>();
        for (int y = 0; y < n; y++) {
            State equivalent = null;
            for (int x = y - 1; x >= 0 && equivalent == null; x--)
                if (Table.get(y).get(x) == false)
                    equivalent = NerodeRelation_EquivalenceClass.get(States.get(x));

            if (equivalent == null) {
                equivalent = (State) result.createVertex();
                result.addVertex(equivalent);
                equivalent.coordinates = new Vector2D(0d, 0d); // initial coordinate (0,0)
                NerodeRelation_EquivalenceClassSize.put(equivalent, 0);
            }

            // add current coordinates to coordinates of <equivalent> state
            equivalent.coordinates = new Vector2D(
                    equivalent.coordinates.getX() + States.get(y).coordinates.getX(),
                    equivalent.coordinates.getY() + States.get(y).coordinates.getY()
            );
            NerodeRelation_EquivalenceClass.put(States.get(y), equivalent);
            NerodeRelation_EquivalenceClassSize.put(equivalent, NerodeRelation_EquivalenceClassSize.get(equivalent) + 1);
        }

        // divide coordinates by number of equivalent states
        // i.e. resulting state is in the center of the equivalent states
        for (Vertex v : result.getVertices()) {
            v.coordinates = new Vector2D(
                    v.coordinates.getX() / NerodeRelation_EquivalenceClassSize.get((State) v),
                    v.coordinates.getY() / NerodeRelation_EquivalenceClassSize.get((State) v)
            );
        }

        // Set Start-State
        for (int y = 0; y < n; y++)
            if (States.get(y).startState)
                NerodeRelation_EquivalenceClass.get(States.get(y)).startState = true;

        // Set Final States
        for (int y = 0; y < n; y++)
            NerodeRelation_EquivalenceClass.get(States.get(y)).finalState = States.get(y).finalState;

        // Set Transitions
        HashMap<State, String> TransitionDefined = new HashMap<>();
        for (Edge e : automaton.getEdges()) {
            Transition t = (Transition) e;
            State resultSource = NerodeRelation_EquivalenceClass.get(t.getSource());

            if (!TransitionDefined.containsKey(resultSource))
                TransitionDefined.put(resultSource, "");

            String foo = TransitionDefined.get(resultSource);
            if (!foo.contains("" + t.Symbol)) {
                TransitionDefined.put(resultSource, "" + t.Symbol);
                Transition resultTransition = (Transition) result.createEdge();
                resultTransition.Symbol = t.Symbol;
                resultTransition.setSource(NerodeRelation_EquivalenceClass.get(t.getSource()));
                resultTransition.setTarget(NerodeRelation_EquivalenceClass.get(t.getTarget()));
                result.addEdge(resultTransition);
            }
        }

        return result;
    }
}
