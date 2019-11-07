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
    name = "Maximum Clique",
    text = "Finds a maximum Clique",
    url = "https://en.wikipedia.org/wiki/Clique_problem")
public class Clique extends Algorithm {

    static boolean findClique(UndirectedGraph s, int k, Set<Vertex> clique,
        Set<Vertex> candidates) {
        if (k < 1)
            return true;
        if (candidates.isEmpty())
            return false;

        for (Vertex candidate : candidates) {
            if (clique.contains(candidate))
                continue;

            Set<Vertex> nextCandidates = new HashSet<>();
            for (Edge e : candidate.getIncidentEdges()) { // candidates ∩ N[candidate]
                if (candidates.contains(e.getSource()))
                    nextCandidates.add(e.getSource());
                if (candidates.contains(e.getTarget()))
                    nextCandidates.add(e.getTarget());
            }

            clique.add(candidate);
            if (findClique(s, k - 1, clique, nextCandidates))
                return true;
            clique.remove(candidate);
        }

        return false;
    }

    public static Set<Vertex> findMaximumClique(UndirectedGraph s) {
        Set<Vertex> result = new HashSet<>();
        for (int k = 1;; k++) {
            Set<Vertex> V = new HashSet<>();
            for (Vertex v : s.getVertices())
                V.add(v);

            Set<Vertex> clique = new HashSet<>();
            if (!findClique(s, k, clique, V))
                break;

            result.clear();
            result.addAll(clique);
        }
        return result;
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        return findMaximumClique(s);
    }
}
