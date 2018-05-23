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
    name = "Shortest Path",
    text = "Finds a shortest path between two selected vertice, using Dijkstra's Algorithm",
    url = "https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm"
)
public class ShortestPath extends Algorithm {

    public static void dijkstraShortestPath(Structure s, Vertex start,
        Vertex target,
        HashMap<Vertex, Vertex> predecessor,
        HashMap<Vertex, Edge> edgeFromPredecessor,
        HashMap<Vertex, Double> distances) {
        HashSet<Vertex> Q = new HashSet<>();
        Q.addAll(s.getVertices());
        predecessor.put(start, null);
        edgeFromPredecessor.put(start, null);
        distances.put(start, 0d);

        while (!Q.isEmpty()) {
            Vertex u = null;
            for (Vertex uit : Q)
                if (distances.containsKey(uit))
                    if (u == null || (distances.get(uit) < distances.get(u)))
                        u = uit;
            if (u == null) // happens if there are unreachable vertices
                break;

            Q.remove(u);
            if (u == target) // found desired shortest path
                break;

            Double distu = distances.get(u);

            for (Edge e : u.getIncidentEdges()) {
                if (e.getSource() != u && e.isDirected) // incoming (directed) edge
                    continue;

                Vertex other = e.getTarget();
                if (other == u)
                    other = e.getSource();

                if (!Q.contains(other)) // shortest path already found
                    continue;

                Double alt = distu + e.weight;
                if ((!distances.containsKey(other)) || (alt < distances.get(other))) {
                    distances.put(other, alt);
                    predecessor.put(other, u);
                    edgeFromPredecessor.put(other, e);
                }
            }
        }
    }

    public static void dijkstraShortestPath(Structure s, Vertex start,
        HashMap<Vertex, Vertex> predecessor,
        HashMap<Vertex, Edge> edgeFromPredecessor,
        HashMap<Vertex, Double> distances) {
        ShortestPath.dijkstraShortestPath(s, start, null, predecessor, edgeFromPredecessor, distances);
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
        ProgressHandler onprogress) throws Exception {
        for (Edge e : (Set<Edge>) s.getEdges())
            if (e.weight < 0d)
                throw new Exception("Dijkstra's Algorithm requires positive edge weights");

        HashMap<Vertex, Vertex> predecessor = new HashMap<>();
        HashMap<Vertex, Edge> edgeFromPredecessor = new HashMap<>();
        HashMap<Vertex, Double> distances = new HashMap<>();

        Vertex v1 = null, v2 = null;
        if (selection != null)
            for (Object o : selection)
                if (o instanceof Vertex) {
                    if (v1 == null)
                        v1 = (Vertex) o;
                    else
                        v2 = (Vertex) o;
                    if (v2 != null)
                        break;
                }

        if (v1 == null)
            v1 = (Vertex) ((s.getVertices().toArray())[0]);

        ShortestPath.dijkstraShortestPath(s, v1, v2, predecessor, edgeFromPredecessor, distances);

        HashSet<Edge> selectedEdges = new HashSet<>();
        if (v2 == null)
            selectedEdges.addAll(edgeFromPredecessor.values());
        else
            for (Vertex r = v2; r != v1; r = predecessor.get(r))
                selectedEdges.add(edgeFromPredecessor.get(r));
        selectedEdges.remove(null);

        return selectedEdges;
    }
}
