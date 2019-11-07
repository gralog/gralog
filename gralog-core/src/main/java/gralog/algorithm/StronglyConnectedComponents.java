/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.DeepCopy;
import gralog.preferences.Preferences;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;

import java.sql.SQLOutput;
import java.util.*;

import static gralog.algorithm.ShortestPath.ANSI_RED;
import static gralog.algorithm.ShortestPath.ANSI_RESET;

/**
 * On undirected graphs, computes connected components, on directed graphs, computes strongly connected
 * components via Tarjan's algorithm.
 */
@AlgorithmDescription(
        name = "(Strongly) Connected Components",
        text = "Finds the (strongly) connected components of a (mixed) graph",
        url = "https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm"
)
public class StronglyConnectedComponents extends Algorithm {

    /**
     * Finds the component of v.
     * @param s
     * @param v
     * @param componentOfVertex
     * @param comp
     * @param removedVertices
     */
    private static void component(UndirectedGraph s, Vertex v, int numComponent,
                                  HashMap<Vertex, Integer> componentOfVertex,
                                  HashSet<Vertex> comp,
                                  HashSet<Vertex> removedVertices) {
        componentOfVertex.put(v, numComponent);
        comp.add(v);
        for (Vertex w : v.getNeighbours()){
            if (removedVertices.contains(w))
                continue;
            component(s, w, numComponent, componentOfVertex, comp, removedVertices);
        }
    }
    public static void components(UndirectedGraph s,
                                  HashMap<Vertex, Integer> componentOfVertex,
                                  HashSet<HashSet<Vertex>> verticesInComponent,
                                  HashSet<Vertex> removedVertices){
        Collection<Vertex> vertices = s.getVertices();
        int numComponent = 0;
        for (Vertex v : vertices){
            if (removedVertices.contains(v))
                continue;
            if (componentOfVertex.containsKey(v)) // already visited
                continue;
            HashSet<Vertex> comp = new HashSet<>();
            component(s, v, numComponent, componentOfVertex, comp, removedVertices);
            numComponent++;
            verticesInComponent.add(comp);
        }
    }
    /**
     * Computes strongly connected components of the structure represented by a coloring Vertex -> Integer and
     * a set of vertex sets of the components. Hereby the vertices given in the parameter removedVertices are ignored
     * as if they were removed.
     * @param s The structure
     * @param componentOfVertex (used for output) A mapping Vertex -> Integer. If not empty,
     *                          the existent entries are not removed.
     * @param verticesInComponent (used for output) A set of vertex sets of components. If not empty,
     *      *                          the existent entries are not removed.
     * @param removedVertices vertices to be ignored
     */
    public static void tarjanStrongComponents(Structure s,
                                              HashMap<Vertex, Integer> componentOfVertex,
                                              HashSet<HashSet<Vertex>> verticesInComponent,
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
        for (Vertex v : vertices) {
            if (removedVertices.contains(v))
                continue;
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
                        HashSet<Vertex> scc = new HashSet<>();
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
        HashSet<HashSet<Vertex>> components = new HashSet<>();
        tarjanStrongComponents(s, componentOfVertex, components, removedVertices);
        if (removedVertices.size() > 0)
            return new VertexToInteger(componentOfVertex, true);
        else
            return new VertexToInteger(componentOfVertex, false);
    }
}
