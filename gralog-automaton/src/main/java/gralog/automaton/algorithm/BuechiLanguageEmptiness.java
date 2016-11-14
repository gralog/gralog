/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.algorithm;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.automaton.*;
import gralog.progresshandler.ProgressHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@AlgorithmDescription(
    name = "Language-Emptiness",
    text = "Tests whether the language defined by the Büchi Automaton is empty or not.",
    url = "https://en.wikipedia.org/wiki/B%C3%BCchi_automaton#Algorithms"
)

public class BuechiLanguageEmptiness extends Algorithm {

    public String languageEmptiness(BuechiAutomaton s) {
        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();

        StronglyConnectedComponents.tarjanStrongComponents(s, componentOfVertex, verticesInComponent);

        HashMap<State, HashMap<Vertex, Vertex>> startStateReach = new HashMap();
        HashMap<State, HashMap<Vertex, Edge>> startStateReachEdges = new HashMap();
        for (Vertex v : s.getVertices())
            if (v instanceof State)
                if (((State) v).startState) {
                    HashMap<Vertex, Vertex> reach = new HashMap<>();
                    HashMap<Vertex, Edge> reachEdges = new HashMap<>();
                    BreadthFirstSearchTree.breadthFirstSearch(v, reach, reachEdges);
                    startStateReach.put((State) v, reach);
                    startStateReachEdges.put((State) v, reachEdges);
                }

        for (ArrayList<Vertex> component : verticesInComponent) {
            // only proceed with components that contain a final state
            State componentFinalState = null;
            for (Vertex v : component)
                if (v instanceof State)
                    if (((State) v).finalState)
                        componentFinalState = (State) v;
            if (componentFinalState == null)
                continue;

            // only proceed with components that contain a cycle
            boolean componentHasCycle = false;
            if (component.size() > 1)
                componentHasCycle = true;
            else {
                Vertex v = component.get(0);
                for (Edge e : v.getConnectedEdges())
                    if (e.getSource() == v && e.getTarget() == v)
                        componentHasCycle = true;
            }
            if (!componentHasCycle)
                continue;

            // only proceed with components that are reachable from a start state
            State componentStartState = null;
            for (State start : startStateReach.keySet())
                if (startStateReach.get(start).containsKey(componentFinalState))
                    componentStartState = start;
            if (componentStartState == null)
                continue;

            // select a path from the start state to the final state
            String path = "";
            HashMap<Vertex, Vertex> componentStartStateReach = startStateReach.get(componentStartState);
            HashMap<Vertex, Edge> componentStartStateReachEdges = startStateReachEdges.get(componentStartState);
            for (Vertex it = componentFinalState; it != null; it = componentStartStateReach.get(it)) {
                Transition e = (Transition) componentStartStateReachEdges.get(it);
                if (e != null)
                    path = e.symbol + path;
            }

            // find a cycle that contains the final state
            HashMap<Vertex, Vertex> finalStateReach = new HashMap<>();
            HashMap<Vertex, Edge> finalStateReachEdges = new HashMap<>();
            BreadthFirstSearchTree.breadthFirstSearch(componentFinalState, finalStateReach, finalStateReachEdges);

            Vertex lastCycleMember = null;
            Transition lastCycleEdge = null;

            for (Edge e : componentFinalState.getConnectedEdges()) {
                Vertex other = e.getSource();
                if (other == componentFinalState)
                    other = e.getTarget();

                if ((e.getSource() == other && e.getTarget() == componentFinalState)
                    || (e.getSource() == componentFinalState && e.getTarget() == other && !e.isDirected))
                    if (finalStateReach.containsKey(other)) {
                        lastCycleMember = other;
                        lastCycleEdge = (Transition) e;
                        break;
                    }
            }

            // TODO: Check if lastCycleEdge can be null.
            String cycle = lastCycleEdge.symbol;
            for (Vertex it = lastCycleMember; it != null; it = finalStateReach.get(it)) {
                Transition e = (Transition) finalStateReachEdges.get(it);
                if (e != null)
                    cycle = e.symbol + cycle;
            }

            return path + "(" + cycle + ")ω";
        }

        return null;
    }

    public Object run(BuechiAutomaton s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) {
        String result = languageEmptiness(s);
        if (result == null)
            return "Language is empty";
        else
            return result;
    }
}
