/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
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
 */
@AlgorithmDescription(
    name = "Coloring",
    text = "Finds a Coloring with a minimum number of colors",
    url = "https://en.wikipedia.org/wiki/Graph_coloring"
)
public class Coloring extends Algorithm {

    protected boolean findColoring(UndirectedGraph s, int k,
        HashMap<Vertex, Integer> clique) {
        Vertex minChoicesVertex = null;
        int minChoicesCount = Integer.MAX_VALUE;
        for (Vertex candidate : s.getVertices()) {
            if (clique.containsKey(candidate)) // already colored
                continue;

            int candidateChoices = 0;
            for (int i = 0; i < k; i++) {
                boolean NeighborHasColorI = false;
                for (Edge e : candidate.getIncidentEdges()) {
                    Vertex neighbor = candidate == e.getSource() ? e.getTarget() : e.getSource();
                    if (clique.containsKey(neighbor)
                        && clique.get(neighbor) == i) {
                        NeighborHasColorI = true;
                        break;
                    }
                }

                if (!NeighborHasColorI)
                    candidateChoices++;
            }

            if (candidateChoices < minChoicesCount) {
                minChoicesCount = candidateChoices;
                minChoicesVertex = candidate;
            }
        }

        if (minChoicesVertex == null) // all vertices are colored
            return true;

        for (int i = 0; i < k; i++) {
            boolean NeighborHasColorI = false;
            for (Edge e : minChoicesVertex.getIncidentEdges()) {
                Vertex neighbor = minChoicesVertex == e.getSource() ? e.getTarget() : e.getSource();
                if (clique.containsKey(neighbor)
                    && clique.get(neighbor) == i) {
                    NeighborHasColorI = true;
                    break;
                }
            }

            if (!NeighborHasColorI) {
                clique.put(minChoicesVertex, i);
                if (findColoring(s, k, clique))
                    return true;
            }
        }

        clique.remove(minChoicesVertex);
        return false;
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        HashMap<Vertex, Integer> result = new HashMap<>();

        Set<Vertex> clique = Clique.findMaximumClique(s);
        int fixedColor = 0;
        for (Vertex v : clique)
            result.put(v, fixedColor++);

        int k = clique.size();
        for (;; k++) {
            if (findColoring(s, k, result))
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

        if (k <= defaultColors.size()) {
            for (Vertex v : s.getVertices())
                v.fillColor = defaultColors.get(result.get(v));
        }
        else{
            for (Vertex v : s.getVertices())
                v.label = result.get(v).toString();
        }
        return k + " colors";
    }
}
