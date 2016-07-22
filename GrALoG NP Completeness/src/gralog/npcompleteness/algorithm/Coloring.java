/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.npcompleteness.algorithm;

import gralog.structure.*;
import gralog.rendering.GralogColor;
import gralog.algorithm.*;
import gralog.progresshandler.ProgressHandler;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Coloring",
  text="Finds a Coloring with a minimum number of colors",
  url="https://en.wikipedia.org/wiki/Graph_coloring"
)
public class Coloring extends Algorithm
{

    protected boolean FindColoring(UndirectedGraph s, int k, HashMap<Vertex, Integer> clique)
    {
        Vertex minChoicesVertex = null;
        int minChoicesCount = Integer.MAX_VALUE;
        for(Vertex candidate : s.getVertices())
        {
            if(clique.containsKey(candidate)) // already colored
                continue;

            int candidateChoices = 0;
            for(int i = 0; i < k; i++)
            {
                boolean NeighborHasColorI = false;
                for(Edge e : candidate.getConnectedEdges())
                {
                    Vertex neighbor = candidate == e.getSource() ? e.getTarget() : e.getSource();
                    if(clique.containsKey(neighbor)
                    && clique.get(neighbor) == i)
                    {
                        NeighborHasColorI = true;
                        break;
                    }
                }
                
                if(!NeighborHasColorI)
                    candidateChoices++;
            }

            if(candidateChoices < minChoicesCount)
            {
                minChoicesCount = candidateChoices;
                minChoicesVertex = candidate;
            }
        }
        
        if(minChoicesVertex == null) // all vertices are colored
            return true;

        
        for(int i = 0; i < k; i++)
        {
            boolean NeighborHasColorI = false;
            for(Edge e : minChoicesVertex.getConnectedEdges())
            {
                Vertex neighbor = minChoicesVertex == e.getSource() ? e.getTarget() : e.getSource();
                if(clique.containsKey(neighbor)
                && clique.get(neighbor) == i)
                {
                    NeighborHasColorI = true;
                    break;
                }
            }

            if(!NeighborHasColorI)
            {
                clique.put(minChoicesVertex, i);
                if(FindColoring(s, k, clique))
                    return true;
            }
        }
            
        clique.remove(minChoicesVertex);
        return false;
    }
    
    public Object Run(UndirectedGraph s, AlgorithmParameters p, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        HashMap<Vertex, Integer> result = new HashMap<>();
        
        Set<Vertex> clique = Clique.FindMaximumClique(s);
        int fixedColor = 0;
        for(Vertex v : clique)
            result.put(v, fixedColor++);

        int k = clique.size();
        for(; ; k++)
        {
            if(FindColoring(s, k, result))
                break;
            result.clear();
        }

        ArrayList<GralogColor> defaultColors = new ArrayList<>();
        defaultColors.add(new GralogColor(0xFF0000)); // red
        defaultColors.add(new GralogColor(0x00FF00)); // green
        defaultColors.add(new GralogColor(0x0000FF)); // blue
        defaultColors.add(new GralogColor(0xFFFF00));
        defaultColors.add(new GralogColor(0xFF00FF));
        defaultColors.add(new GralogColor(0x00FFFF));
        defaultColors.add(new GralogColor(0x000000)); // black
        defaultColors.add(new GralogColor(0xFFFFFF)); // white

        if(k <= defaultColors.size())
            for(Vertex v : s.getVertices())
                v.FillColor = defaultColors.get(result.get(v));
        
        return null;
    }        
}
