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
  name="Clique",
  text="Finds a maximum Clique",
  url="https://en.wikipedia.org/wiki/Clique_problem"
)
public class Clique extends Algorithm {
     
    protected boolean FindClique(UndirectedGraph s, int k, Set<Vertex> clique, Set<Vertex> candidates)
    {
        if(k < 1)
            return true;
        if(candidates.isEmpty())
            return false;
        
        for(Vertex candidate : candidates)
        {
            if(clique.contains(candidate))
                continue;
            
            Set<Vertex> nextCandidates = new HashSet<>();
            for(Edge e : candidate.getConnectedEdges()) // candidates ∩ N[candidate]
            {
                if(candidates.contains(e.getSource()))
                    nextCandidates.add(e.getSource());
                if(candidates.contains(e.getTarget()))
                    nextCandidates.add(e.getTarget());
            }
            
            clique.add(candidate);
            if(FindClique(s, k-1, clique, nextCandidates))
                return true;
            clique.remove(candidate);
        }
        
        return false;
    }
    
    public Object Run(UndirectedGraph s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        Set<Vertex> result = new HashSet<>();
        for(int k = 1; ; k++)
        {
            Set<Vertex> V = new HashSet<>();
            for(Vertex v : s.getVertices())
                V.add(v);
            
            Set<Vertex> Clique = new HashSet<>();
            if(!FindClique(s, k, Clique, V))
                break;
            
            result.clear();
            result.addAll(Clique);
        }
        return result;
    }        
}
