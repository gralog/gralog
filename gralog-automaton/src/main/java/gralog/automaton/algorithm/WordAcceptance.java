/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.algorithm;

import gralog.algorithm.*;
import gralog.automaton.*;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "Word Acceptance",
    text = "",
    url = "https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton#Example"
)
public class WordAcceptance extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter("Word", "");
    }

    public Object run(Automaton s, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        return accepts(s, sp.parameter) ? "true" : "false";
    }

    public boolean accepts(Automaton automaton, String str) {

        HashSet<State> currentStates = new HashSet<>();
        for (Vertex v : automaton.getVertices())
            if (v instanceof State)
                if (((State) v).startState)
                    currentStates.add((State) v);
        currentStates = automaton.epsilonHull(currentStates);

        HashSet<State> nextStates = new HashSet<>();
        for (int i = 0; i < str.length(); i++) {
            String stri = "" + str.charAt(i);
            for (State s : currentStates)
                for (Edge e : s.getConnectedEdges())
                    if (e.getSource() == s)
                        if (e instanceof Transition)
                            if (((Transition) e).symbol.equals(stri))
                                nextStates.add((State) e.getTarget());
            nextStates = automaton.epsilonHull(nextStates);

            HashSet<State> temp = currentStates;
            currentStates = nextStates;
            nextStates = temp;
            nextStates.clear();
        }

        for (State s : currentStates)
            if (s.finalState)
                return true;
        return false;
    }
}
