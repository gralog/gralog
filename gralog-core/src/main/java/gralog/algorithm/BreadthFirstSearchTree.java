/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.progresshandler.ProgressHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
    name = "Breadth-First Search-Tree",
    text = "Constructs a Breadth-First Search-Tree",
    url = "https://en.wikipedia.org/wiki/Breadth-first_search"
)
public class BreadthFirstSearchTree extends Algorithm {

    public static void breadthFirstSearch(Vertex start,
        HashMap<Vertex, Vertex> predecessor,
        HashMap<Vertex, Edge> edgeFromPredecessor) {
        ArrayDeque<Vertex> queue = new ArrayDeque<>();
        queue.addLast(start);
        predecessor.put(start, null);
        edgeFromPredecessor.put(start, null);

        while (!queue.isEmpty()) {
            Vertex v = queue.removeFirst();

            for (Edge e : v.getConnectedEdges()) {
                if (e.getSource() != v && e.isDirected) // incoming (directed) edge
                    continue;

                Vertex other = e.getTarget();
                if (other == v)
                    other = e.getSource();

                if (predecessor.containsKey(other)) // successor already in the tree
                    continue;
                predecessor.put(other, v);
                edgeFromPredecessor.put(other, e);
                queue.addLast(other);
            }
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
        breadthFirstSearch(v, predecessor, edgeFromPredecessor);

        HashSet<Edge> tree = new HashSet<>();
        tree.addAll(edgeFromPredecessor.values());
        tree.remove(null);
        return tree;
    }

}
