/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.progresshandler.ProgressHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Depth-First Search-Tree",
  text="Constructs a Depth-First Search-Tree",
  url="https://en.wikipedia.org/wiki/Depth-first_search"
)
public class DepthFirstSearchTree extends Algorithm
{
    public static void DepthFirstSearch(Vertex v, HashMap<Vertex, Vertex> predecessor, HashMap<Vertex,Edge> edgeFromPredecessor)
    {
        for(Edge e : v.getConnectedEdges())
        {
            if(e.getSource() != v && e.isDirected) // incoming (directed) edge
                continue;
            
            Vertex other = e.getTarget();
            if(other == v)
                other = e.getSource();

            if(predecessor.containsKey(other)) // successor already in the tree
                continue;
            
            predecessor.put(other, v);
            edgeFromPredecessor.put(other, e);
            DepthFirstSearch(other, predecessor, edgeFromPredecessor);
        }
    }
    
    public Object Run(Structure s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress)
    {
        HashMap<Vertex, Vertex> predecessor = new HashMap<>();
        HashMap<Vertex, Edge> edgeFromPredecessor = new HashMap<>();
        Vertex v = null;
        if(selection != null)
            for(Object o : selection)
                if(o instanceof Vertex)
                    v = (Vertex)o;
        if(v == null)
            v = (Vertex)((s.getVertices().toArray())[0]);
        predecessor.put(v,null);
        edgeFromPredecessor.put(v, null);
        DepthFirstSearch(v, predecessor, edgeFromPredecessor);

        HashSet<Edge> tree = new HashSet<>();
        tree.addAll(edgeFromPredecessor.values());
        tree.remove(null);
        return tree;
    }
    
}
