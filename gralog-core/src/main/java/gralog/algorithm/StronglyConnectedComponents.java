/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.preferences.Preferences;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.util.*;

/**
 *
 */
@AlgorithmDescription(
        name = "Strongly Connected Components",
        text = "Finds the strongly connected components of a (mixed) graph",
        url = "https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm"
)
public class StronglyConnectedComponents extends Algorithm {

    public static void tarjanStrongComponents(Structure s,
                                              HashMap<Vertex, Integer> componentOfVertex,
                                              ArrayList<ArrayList<Vertex>> verticesInComponent,
                                              HashSet<Vertex> removedVertices) {
        int numScc = 0;
        int index = 0;
        Stack<Vertex> tarStack = new Stack<>();
        HashSet<Vertex> onStack = new HashSet<>();

        HashMap<Vertex, Integer> dfs = new HashMap<>();
        HashMap<Vertex, Integer> lowlink = new HashMap<>();
        HashMap<Vertex, Vertex> parent = new HashMap<>();

        HashMap<Vertex, ArrayList<Vertex>> children = new HashMap<>(); // children in dfs tree
        HashMap<Vertex, Integer> childIterationPos = new HashMap<>(); // children-iteration position

        Collection<Vertex> vertices = s.getVertices();
        vertices.removeAll(removedVertices);
        for (Vertex v : vertices) {
            if (dfs.containsKey(v)) // already processed
                continue;

            dfs.put(v, index);
            lowlink.put(v, index);
            index++;

            ArrayList<Vertex> vChildren = new ArrayList<>();
            for (Edge e : v.getIncidentEdges())
                if (e.getSource() == v)
                    if (!removedVertices.contains(e.getTarget()))
                        vChildren.add(e.getTarget());
                else if (!e.isDirected)
                    if (!removedVertices.contains(e.getSource()))
                        vChildren.add(e.getSource());
            children.put(v, vChildren);
            childIterationPos.put(v, 0); // iteration starts at id 0

            tarStack.push(v);
            parent.put(v, null);
            onStack.add(v);
            Vertex current = v;

            while (current != null) {
                Integer currentChildIdx = childIterationPos.get(current);
                if (currentChildIdx < children.get(current).size()) { // <current> has more children
                    Vertex child = children.get(current).get(currentChildIdx);
                    childIterationPos.put(current, currentChildIdx + 1);
                    if (!dfs.containsKey(child)) { // child wasn't processed yet
                        parent.put(child, current);

                        ArrayList<Vertex> grandChildren = new ArrayList<>();
                        for (Edge e : child.getIncidentEdges())
                            if (e.getSource() == child)
                                if (!removedVertices.contains(e.getTarget()))
                                    grandChildren.add(e.getTarget());
                            else if (!e.isDirected)
                                if (!removedVertices.contains(e.getSource()))
                                    grandChildren.add(e.getSource());
                        children.put(child, grandChildren);

                        childIterationPos.put(child, 0); // iteration (on child's children) starts at id 0
                        dfs.put(child, index);
                        lowlink.put(child, index);
                        index++;
                        tarStack.push(child);
                        onStack.add(child);
                        current = child; // recursion
                    } else if (onStack.contains(child)) {
                        lowlink.put(current, Math.min(lowlink.get(current), dfs.get(child)));
                    }
                } else { // collect current scc and go up one recursive call
                    if (lowlink.get(current).equals(dfs.get(current))) {
                        Vertex top = null;
                        ArrayList<Vertex> scc = new ArrayList<>();
                        while (top != current) {
                            top = tarStack.pop();
                            onStack.remove(top);
                            scc.add(top);
                            componentOfVertex.put(top, numScc);
                        }
                        verticesInComponent.add(scc);
                        numScc++;
                    }

                    Vertex newLast = parent.get(current); //Go up one recursive call
                    if (newLast != null)
                        lowlink.put(newLast, Math.min(lowlink.get(newLast), lowlink.get(current)));
                    current = newLast;
                }
            } // while(last != null)
        } // for (Vertex i : V)
    }

    @Override
    public AlgorithmParameters getParameters(Structure structure, Highlights highlights) {
        ArrayList<Object> obtainedObjects = highlights.getFilteredByType(Vertex.class);
        ArrayList<Vertex> staringVertices = new ArrayList<>();
        for (Object o : obtainedObjects){
            staringVertices.add((Vertex)o);
        }

        return new StronglyConnectedComponentsParameters(staringVertices);
    }


    public Object run(Structure s, StronglyConnectedComponentsParameters parameters, Set<Object> selection,
                      ProgressHandler onprogress) throws Exception {
        HashSet<Vertex> removedVertices = new HashSet<>(parameters.vertices);
        HashMap<Vertex, Integer> componentOfVertex = new HashMap<>();
        ArrayList<ArrayList<Vertex>> verticesInComponent = new ArrayList<>();
        tarjanStrongComponents(s, componentOfVertex, verticesInComponent, removedVertices);
        return new HashSet<>(verticesInComponent);
    }
}
