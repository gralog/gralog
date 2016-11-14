/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.jgraphtbridge.algorithm;

import java.util.Set;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmParameters;
import gralog.progresshandler.ProgressHandler;

/**
 *
 */
public abstract class JGraphTAlgorithm extends Algorithm {

    public abstract Object jGraphTRun(
        org.jgrapht.Graph<gralog.structure.Vertex, org.jgrapht.graph.DefaultEdge> g,
        AlgorithmParameters ap, Set<Object> selection,
        ProgressHandler onprogress) throws Exception;

    public Object run(gralog.structure.DirectedGraph s, AlgorithmParameters ap,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        org.jgrapht.DirectedGraph<gralog.structure.Vertex, org.jgrapht.graph.DefaultEdge> jgraph
            = new org.jgrapht.graph.SimpleDirectedGraph<>(org.jgrapht.graph.DefaultEdge.class);

        Set<gralog.structure.Vertex> V = s.getVertices();
        for (gralog.structure.Vertex v : V)
            jgraph.addVertex(v);

        Set<gralog.structure.Edge> E = s.getEdges();
        for (gralog.structure.Edge e : E)
            jgraph.addEdge(e.getSource(), e.getTarget());

        return jGraphTRun(jgraph, ap, selection, onprogress);
    }
}
