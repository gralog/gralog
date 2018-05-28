/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.structure.*;
import gralog.progresshandler.ProgressHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "Depth-First Search-Tree",
    text = "Constructs a Depth-First Search-Tree",
    url = "https://en.wikipedia.org/wiki/Depth-first_search"
)
public class DepthFirstSearchTree extends Algorithm {

    public static void depthFirstSearch(Vertex v,
        HashMap<Vertex, Vertex> predecessor,
        HashMap<Vertex, Edge> edgeFromPredecessor) {
        for (Edge e : v.getIncidentEdges()) {
            if (e.getSource() != v && e.isDirected) // incoming (directed) edge
                continue;

            Vertex other = e.getTarget();
            if (other == v)
                other = e.getSource();

            if (predecessor.containsKey(other)) // successor already in the tree
                continue;

            predecessor.put(other, v);
            edgeFromPredecessor.put(other, e);
            depthFirstSearch(other, predecessor, edgeFromPredecessor);
        }
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) {
        HashMap<Vertex, Vertex> predecessor = new HashMap<>();
        HashMap<Vertex, Edge> edgeFromPredecessor = new HashMap<>();
        Vertex v = null;
        if (selection != null)
            for (Object o : selection)
                if (o instanceof Vertex)
                    v = (Vertex) o;
        if (v == null)
            v = (Vertex) ((s.getVertices().toArray())[0]);
        predecessor.put(v, null);
        edgeFromPredecessor.put(v, null);
        depthFirstSearch(v, predecessor, edgeFromPredecessor);

        HashSet<Edge> tree = new HashSet<>();
        tree.addAll(edgeFromPredecessor.values());
        tree.remove(null);
        return tree;
    }

}
