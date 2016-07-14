/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.jgraphtbridge.algorithm;

import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import gralog.structure.Edge;

import gralog.algorithm.Algorithm;
import gralog.algorithm.AlgorithmParameters;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.Structure;

/**
 *
 * @author viktor
 */
public abstract class JGraphTAlgorithm extends Algorithm
{
    
        public abstract Object JGraphTRun(org.jgrapht.Graph<gralog.structure.Vertex, org.jgrapht.graph.DefaultEdge> g,
                AlgorithmParameters ap, Set<Object> selection, ProgressHandler onprogress) throws Exception;
        
        public Object Run(gralog.structure.DirectedGraph s, AlgorithmParameters ap, Set<Object> selection, ProgressHandler onprogress) throws Exception
        {
            org.jgrapht.DirectedGraph<gralog.structure.Vertex, org.jgrapht.graph.DefaultEdge> jgraph
                    = new org.jgrapht.graph.SimpleDirectedGraph<>(org.jgrapht.graph.DefaultEdge.class);
            
            Set<gralog.structure.Vertex> Vs = s.getVertices();
            for(gralog.structure.Vertex v : Vs)
                jgraph.addVertex(v);
            
            Set<gralog.structure.Edge> Es = s.getEdges();
            for(gralog.structure.Edge e : Es)
                jgraph.addEdge(e.getSource(), e.getTarget());
            
            return JGraphTRun(jgraph, ap, selection, onprogress);
        }
    
    
}
