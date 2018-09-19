/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
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
    name = "Independent Set",
    text = "Finds a maximum Independent Set",
    url = "https://en.wikipedia.org/wiki/Independent_set_(graph_theory)"
)
public class IndependentSet extends Algorithm {

    protected boolean findIndependentSet(UndirectedGraph s, int k,
        Set<Vertex> independentset, Set<Vertex> candidates) {
        if (k < 1)
            return true;
        if (candidates.isEmpty())
            return false;

        for (Vertex candidate : candidates) {
            Set<Vertex> candidateNeigh = new HashSet<>();
            for (Edge e : candidate.getIncidentEdges()) {
                candidateNeigh.add(e.getSource());
                candidateNeigh.add(e.getTarget());
            }

            // nextCandidates = candidates \ N[candidate]
            Set<Vertex> nextCandidates = new HashSet<>();
            for (Vertex u : candidates)
                if (!candidateNeigh.contains(u))
                    nextCandidates.add(u);

            independentset.add(candidate);
            if (findIndependentSet(s, k - 1, independentset, nextCandidates))
                return true;
            independentset.remove(candidate);
        }

        return false;
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        Set<Vertex> result = new HashSet<>();
        for (int k = 1;; k++) {
            Set<Vertex> V = new HashSet<>();
            for (Vertex v : s.getVertices())
                V.add(v);

            Set<Vertex> independentSet = new HashSet<>();
            if (!findIndependentSet(s, k, independentSet, V))
                break;

            result.clear();
            result.addAll(independentSet);
        }
        return result;
    }
}
