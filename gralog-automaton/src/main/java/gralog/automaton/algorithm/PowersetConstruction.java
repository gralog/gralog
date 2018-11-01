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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;


/**
 *
 */
@AlgorithmDescription(
    name = "Rabin-Scott Powerset Construction",
    text = "",
    url = "https://en.wikipedia.org/wiki/Powerset_construction"
)
public class PowersetConstruction extends Algorithm {

    public Object run(Automaton a, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) throws Exception {
        Automaton result = new Automaton();
        PowersetConstructionTreeNode tree = new PowersetConstructionTreeNode(null, null, null);

        Set<State> setQ0 = new HashSet<>();
        Collection<State> setQ = a.getVertices();
        Set<Transition> delta = (Set<Transition>) a.getEdges();
        for (Vertex v : setQ) {
            if (v instanceof State && ((State) v).startState) {
                setQ0.add((State) v);
            }
        }
        setQ0 = a.epsilonHull(setQ0);
        State q0 = tree.getContentForSet(a, result, setQ0);
        q0.startState = true;

        Set<Character> alphabet = new HashSet<>();
        for (Edge e : delta) {
            if (e instanceof Transition) {
                String s = ((Transition) e).symbol;
                for (int i = 0; i < s.length(); i++) {
                    alphabet.add(s.charAt(i));
                }
            }
        }

        Queue<Set<State>> queue = new LinkedList<>();
        queue.add(setQ0);
        Set<State> knownStates = new HashSet<>();
        knownStates.add(q0);
        while (!queue.isEmpty()) {
            Set<State> front = queue.remove();

            for (Character c : alphabet) {
                Set<State> cSuccessor = new HashSet<>();
                for (Edge e : delta) {
                    if (e instanceof Transition && front.contains(e.getSource())
                            && ((Transition) e).symbol.equals(c.toString())) {
                        cSuccessor.add((State) e.getTarget());
                    }
                }
                cSuccessor = a.epsilonHull(cSuccessor);

                State src = tree.getContentForSet(a, result, front);
                State dst = tree.getContentForSet(a, result, cSuccessor);

                if (!knownStates.contains(dst)) {
                    for (State cSuccessorElement : cSuccessor) {
                        if (cSuccessorElement.finalState) {
                            dst.finalState = true;
                        }
                    }
                    queue.add(cSuccessor);
                    knownStates.add(dst);
                }

                Transition trans = result.createEdge(src, dst);
                trans.symbol = c.toString();
                result.addEdge(trans);
            }
        }

        return result;
    }
}
