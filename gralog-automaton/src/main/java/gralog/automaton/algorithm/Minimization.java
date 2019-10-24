/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.algorithm;


import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmDescription;
import gralog.algorithm.AlgorithmParameters;
import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Edge;
import gralog.structure.Vertex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

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
        ArrayList<State> states = new ArrayList<>();
        HashMap<State, Integer> stateIndex = new HashMap<>();
        for (Vertex v : automaton.getVertices()) {
            states.add((State) v);
            stateIndex.put((State) v, n++);
        }

        // Create Initial Table
        ArrayList<ArrayList<Boolean>> table = new ArrayList<>();
        for (int y = 0; y < n; y++) { // iterate over table-rows
            table.add(new ArrayList<>());
            for (int x = 0; x < n; x++) { // iterate over row-cells
                table.get(y).add(!Objects.equals(
                        states.get(y).finalState,
                        states.get(x).finalState)); // xor
            }
        }

        // Refine Table
        for (int i = 0; i < n; i++) { // n times do
            for (int y = 0; y < n; y++) { // iterate over table-rows
                for (int x = 0; x < y; x++) { // iterate over row-cells
                    if (!table.get(y).get(x)) {
                        for (Edge tx : automaton.getEdges()) {
                            if (tx.getSource() != states.get(x)) {
                                continue;
                            }

                            for (Edge ty : automaton.getEdges()) {
                                if (ty.getSource() != states.get(y)) {
                                    continue;
                                }
                                if (!((Transition) ty).symbol.equals(((Transition) tx).symbol)) {
                                    continue;
                                }

                                // x and y have distinguishable "symbol"-successors
                                // hence x and y are distinguishable
                                if (table.get(stateIndex.get(tx.getTarget())).get(stateIndex.get(ty.getTarget()))) {
                                    table.get(x).set(y, true);
                                    table.get(y).set(x, true);
                                    break;
                                }

                            }
                        }
                    }
                }
            }
        }

        // Build Resulting Automaton
        Automaton result = new Automaton();

        // Build State Set
        HashMap<State, State> nerodeRelationEquivalenceClass = new HashMap<>();
        HashMap<State, Integer> nerodeRelationEquivalenceClassSize = new HashMap<>();
        for (int y = 0; y < n; y++) {
            State equivalent = null;
            for (int x = y - 1; x >= 0 && equivalent == null; x--) {
                if (!table.get(y).get(x)) {
                    equivalent = nerodeRelationEquivalenceClass.get(states.get(x));
                }
            }

            if (equivalent == null) {
                equivalent = (State) result.createVertex();
                result.addVertex(equivalent);
                equivalent.setCoordinates(0d, 0d); // initial coordinate (0,0)
                nerodeRelationEquivalenceClassSize.put(equivalent, 0);
            }

            // add current coordinates to coordinates of <equivalent> state
            equivalent.setCoordinates(
                equivalent.getCoordinates().getX() + states.get(y).getCoordinates().getX(),
                equivalent.getCoordinates().getY() + states.get(y).getCoordinates().getY()
            );
            nerodeRelationEquivalenceClass.put(states.get(y), equivalent);
            nerodeRelationEquivalenceClassSize.put(equivalent, nerodeRelationEquivalenceClassSize.get(equivalent) + 1);
        }

        // divide coordinates by number of equivalent states
        // i.e. resulting state is in the center of the equivalent states
        for (Vertex v : result.getVertices()) {
            v.setCoordinates(
                v.getCoordinates().getX() / nerodeRelationEquivalenceClassSize.get((State) v),
                v.getCoordinates().getY() / nerodeRelationEquivalenceClassSize.get((State) v)
            );
        }

        // Set Start-State
        for (int y = 0; y < n; y++) {
            if (states.get(y).startState) {
                nerodeRelationEquivalenceClass.get(states.get(y)).startState = true;
            }
        }

        // Set Final States
        for (int y = 0; y < n; y++) {
            nerodeRelationEquivalenceClass.get(states.get(y)).finalState = states.get(y).finalState;
        }

        // Set Transitions
        HashMap<State, String> transitionDefined = new HashMap<>();
        for (Edge e : automaton.getEdges()) {
            Transition t = (Transition) e;
            State resultSource = nerodeRelationEquivalenceClass.get(t.getSource());

            if (!transitionDefined.containsKey(resultSource)) {
                transitionDefined.put(resultSource, "");
            }

            String foo = transitionDefined.get(resultSource);
            if (!foo.contains("" + t.symbol)) {
                transitionDefined.put(resultSource, "" + t.symbol);
                Transition resultTransition = (Transition) result.createEdge(null);
                resultTransition.symbol = t.symbol;
                resultTransition.setSource(nerodeRelationEquivalenceClass.get(t.getSource()));
                resultTransition.setTarget(nerodeRelationEquivalenceClass.get(t.getTarget()));
                result.addEdge(resultTransition);
            }
        }

        return result;
    }
}
