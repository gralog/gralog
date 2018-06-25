/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton;

import gralog.preferences.Configuration;
import gralog.structure.*;
import gralog.plugins.*;

import java.util.Set;
import java.util.HashSet;

/**
 *
 */
@StructureDescription(
    name = "Automaton",
    text = "",
    url = "https://en.wikipedia.org/wiki/Automata_theory")
@XmlName(name = "automaton")
public class Automaton extends Structure<State, Transition> {

    @Override
    public State createVertex() {
        return new State();
    }

    @Override
    public State createVertex(Configuration config) {
        //TODO:
        return new State(config);
    }

    @Override
    public Transition createEdge() {
        return new Transition();
    }

    @Override
    public Transition createEdge(Configuration config) {
        return new Transition(config);
    }

    public HashSet<State> epsilonHull(Set<State> start) {
        HashSet<State> result = new HashSet<>();
        HashSet<State> lastiteration = new HashSet<>();
        HashSet<State> currentiteration = new HashSet<>();

        result.addAll(start);
        lastiteration.addAll(start);

        while (!lastiteration.isEmpty()) {

            for (State s : lastiteration)
                for (Edge e : s.getIncidentEdges())
                    if (e instanceof Transition)
                        if (((Transition) e).symbol.equals("")) // epsilon transition
                            if (!result.contains((State) e.getTarget()))
                                currentiteration.add((State) e.getTarget());

            result.addAll(currentiteration);
            HashSet<State> temp = lastiteration;
            lastiteration = currentiteration;
            currentiteration = temp;
            currentiteration.clear();
        }

        return result;
    }
}
