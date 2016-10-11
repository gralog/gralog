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
        name = "Dominating Set",
        text = "Finds a minimum Dominating Set",
        url = "https://en.wikipedia.org/wiki/Dominating_set"
)
public class DominatingSet extends Algorithm {

    protected boolean findDominatingSet(UndirectedGraph s, int k,
            Set<Vertex> domset, Set<Vertex> undominated) {
        if (undominated.isEmpty())
            return true;
        if (k < 1)
            return false;

        Vertex minNeigh = null;
        for (Vertex v : undominated)
            if (minNeigh == null || (minNeigh.getConnectedEdges().size() > v.getConnectedEdges().size()))
                minNeigh = v;

        Set<Vertex> Candidates = new HashSet<>();
        for (Edge e : minNeigh.getConnectedEdges()) {
            Candidates.add(e.getSource());
            Candidates.add(e.getTarget());
        }

        for (Vertex candidate : Candidates) {
            if (domset.contains(candidate))
                continue;

            Set<Vertex> candidateNeigh = new HashSet<>();
            for (Edge e : candidate.getConnectedEdges()) {
                candidateNeigh.add(e.getSource());
                candidateNeigh.add(e.getTarget());
            }

            Set<Vertex> NextUnDom = new HashSet<>();
            for (Vertex u : undominated)
                if (!candidateNeigh.contains(u))
                    NextUnDom.add(u);

            domset.add(candidate);
            if (findDominatingSet(s, k - 1, domset, NextUnDom))
                return true;
            domset.remove(candidate);
        }

        return false;
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {
        for (int k = 1;; k++) {
            Set<Vertex> V = new HashSet<>();
            for (Vertex v : s.getVertices())
                V.add(v);

            Set<Vertex> DomSet = new HashSet<>();
            if (findDominatingSet(s, k, DomSet, V))
                return DomSet;
        }
    }
}
