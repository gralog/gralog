/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.npcompleteness.algorithm;

import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.ProgressHandler;
import java.util.Set;
import java.util.HashSet;

/**
 *
 */
@AlgorithmDescription(
    name = "Dominating Set",
    text = "Finds a minimum Dominating Set",
    url = "https://en.wikipedia.org/wiki/Dominating_set")
public class DominatingSet extends Algorithm {

    protected boolean findDominatingSet(UndirectedGraph s, int k,
        Set<Vertex> domset, Set<Vertex> undominated) {
        if (undominated.isEmpty())
            return true;
        if (k < 1)
            return false;

        Vertex minNeigh = null;
        for (Vertex v : undominated)
            if (minNeigh == null || (minNeigh.getIncidentEdges().size() > v.getIncidentEdges().size()))
                minNeigh = v;

        Set<Vertex> candidates = new HashSet<>();
        for (Edge e : minNeigh.getIncidentEdges()) {
            candidates.add(e.getSource());
            candidates.add(e.getTarget());
        }

        for (Vertex candidate : candidates) {
            if (domset.contains(candidate))
                continue;

            Set<Vertex> candidateNeigh = new HashSet<>();
            for (Edge e : candidate.getIncidentEdges()) {
                candidateNeigh.add(e.getSource());
                candidateNeigh.add(e.getTarget());
            }

            Set<Vertex> nextUnDom = new HashSet<>();
            for (Vertex u : undominated)
                if (!candidateNeigh.contains(u))
                    nextUnDom.add(u);

            domset.add(candidate);
            if (findDominatingSet(s, k - 1, domset, nextUnDom))
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
                if (!selection.contains(v))
                    V.add(v);

            Set<Vertex> domSet = new HashSet<>();
            if (findDominatingSet(s, k, domSet, V))
                return domSet;
        }
    }
}
