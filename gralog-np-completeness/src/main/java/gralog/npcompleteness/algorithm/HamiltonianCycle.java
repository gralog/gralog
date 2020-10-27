/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.algorithm;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmDescription;
import gralog.algorithm.AlgorithmParameters;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Edge;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@AlgorithmDescription(
        name = "Hamiltonian Cycle",
        text = "Finds a Hamilton Cycle",
        url = "https://en.wikipedia.org/wiki/Hamiltonian_path_problem")
public class HamiltonianCycle extends Algorithm {
    static int k = 0;
    static boolean findHamiltonianCycle(Structure s,
                                        Set<Vertex> unusedVertices,
                                        Set<Edge> partialCycle,
                                        Vertex firstVertex,
                                        Vertex lastVertex) {
        // finish the cycle: the last edge is stil missing
        if (unusedVertices.isEmpty()){
            for (Edge edge : lastVertex.getOutgoingEdges()) {
                if ((edge.getTarget() == firstVertex) || (!edge.isDirected() && edge.getSource() == firstVertex)) {
                    partialCycle.add(edge);
                    return true;
                }
            }
            return false;
        }

        Vertex prevLastVertex = null;
        for (Edge edge : lastVertex.getOutgoingEdges()) {
            Vertex addedVertex = null;
            if (edge.isDirected() && unusedVertices.contains(edge.getTarget()))
                addedVertex = edge.getTarget();

            // addedVertex = the other end of edge
            if (!edge.isDirected()) {
                if (edge.getTarget() == lastVertex) {
                    if (unusedVertices.contains(edge.getSource()))
                        addedVertex = edge.getSource();
                } else if (unusedVertices.contains(edge.getTarget()))
                    addedVertex = edge.getTarget();
            }

            if (addedVertex == null) // all successors of lastVertex are already used
                continue;

            unusedVertices.remove(addedVertex);
            partialCycle.add(edge);
            prevLastVertex = lastVertex;
            lastVertex = addedVertex;

            if (findHamiltonianCycle(s, unusedVertices, partialCycle, firstVertex, lastVertex))
                return true;

            // roll back
            lastVertex = prevLastVertex;
            partialCycle.remove(edge);
            unusedVertices.add(addedVertex);
        }
        return false;
    }

    public static Set<Edge> findHamiltonianCycle(Structure s) {
        if (s.getVertices().isEmpty())
            return new HashSet<Edge>();
        Set<Edge> result = new HashSet<>();
        Set<Vertex> unusedVertices = new HashSet<>();
        unusedVertices.addAll(s.getVertices());
        // get the initial vertex
        Vertex firstVertex = null;
        for (Object v : s.getVertices()){
            firstVertex = (Vertex) v;
            unusedVertices.remove(v);
            break;
        }
        Set<Edge> partialCycle = new HashSet<>();
        findHamiltonianCycle(s, unusedVertices, partialCycle, firstVertex, firstVertex);
        result.clear();
        result.addAll(partialCycle);
        return result;
    }

    public Object run(Structure s, AlgorithmParameters p,
                      Set<Object> selection, ProgressHandler onprogress) throws Exception {
        Set<Edge> result = findHamiltonianCycle(s);

        if (result.isEmpty())
            return ("There is no Hamiltonian cycle in this graph.");
        else
            return result;
    }
}
