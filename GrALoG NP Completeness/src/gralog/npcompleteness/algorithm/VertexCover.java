/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.algorithm;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.ProgressHandler;
import java.util.Set;
import java.util.HashSet;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Vertex Cover",
  text="Finds a minimum Vertex Cover",
  url="https://en.wikipedia.org/wiki/Vertex_cover"
)
public class VertexCover extends Algorithm {
     
    protected boolean FindVertexCover(UndirectedGraph s, int k, Set<Vertex> vertexcover)
    {
        boolean allEdgesCovered = true;
        for(Edge e : s.getEdges())
            if(!vertexcover.contains(e.getSource())
            && !vertexcover.contains(e.getTarget()))
                allEdgesCovered = false;
        
        if(allEdgesCovered)
            return true;
        if(k < 1)
            return false;

        
        for(Edge e : s.getEdges())
        {
            if(!vertexcover.contains(e.getSource())
            && !vertexcover.contains(e.getTarget()))
            {
                vertexcover.add(e.getSource());
                if(FindVertexCover(s,k-1,vertexcover))
                    return true;
                vertexcover.remove(e.getSource());
                
                vertexcover.add(e.getTarget());
                if(FindVertexCover(s,k-1,vertexcover))
                    return true;
                vertexcover.remove(e.getTarget());
                
                break;
            }
        }
        
        return false;
    }
    
    public Object Run(UndirectedGraph s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        for(int k = 1; ; k++)
        {
            Set<Vertex> VertexCover = new HashSet<>();
            if(FindVertexCover(s, k, VertexCover))
                return VertexCover;
        }
    }        
}
