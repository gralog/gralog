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
import java.util.ArrayDeque;
import java.util.Set;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Breadth-First Search-Tree",
  text="Constructs a Breadth-First Search-Tree",
  url="https://en.wikipedia.org/wiki/Breadth-first_search"
)
public class BreadthFirstSearchTree extends Algorithm
{
    public static void BreadthFirstSearch(Vertex start, HashMap<Vertex, Vertex> predecessor, HashMap<Vertex, Edge> edgeFromPredecessor)
    {
        ArrayDeque<Vertex> queue = new ArrayDeque<>();
        queue.addLast(start);
        predecessor.put(start, null);
        edgeFromPredecessor.put(start, null);
        
        while(!queue.isEmpty())
        {
            Vertex v = queue.removeFirst();
            
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
                queue.addLast(other);
            }
        }
    }
    
    public Object Run(Structure s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress)
    {
        if(selection == null || selection.size() != 1)
            return null;
        
        HashMap<Vertex, Vertex> predecessor = new HashMap<>();
        HashMap<Vertex, Edge> edgeFromPredecessor = new HashMap<>();
        Vertex v = (Vertex)((selection.toArray())[0]);
        BreadthFirstSearch(v, predecessor, edgeFromPredecessor);
        
        HashSet<Edge> tree = new HashSet<>();
        tree.addAll(edgeFromPredecessor.values());
        tree.remove(null);
        return tree;
    }
    
}
