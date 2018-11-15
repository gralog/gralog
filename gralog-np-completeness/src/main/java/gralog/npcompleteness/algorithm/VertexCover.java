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
    name = "Vertex Cover",
    text = "Finds a minimum Vertex Cover",
    url = "https://en.wikipedia.org/wiki/Vertex_cover"
)
public class VertexCover extends Algorithm {

    protected boolean findVertexCover(UndirectedGraph s, int k,
        Set<Vertex> vertexcover) {
        boolean allEdgesCovered = true;
        for (Edge e : s.getEdges())
            if (!vertexcover.contains(e.getSource())
                && !vertexcover.contains(e.getTarget()))
                allEdgesCovered = false;

        if (allEdgesCovered)
            return true;
        if (k < 1)
            return false;

        for (Edge e : s.getEdges()) {
            if (!vertexcover.contains(e.getSource())
                && !vertexcover.contains(e.getTarget())) {
                vertexcover.add(e.getSource());
                if (findVertexCover(s, k - 1, vertexcover))
                    return true;
                vertexcover.remove(e.getSource());

                vertexcover.add(e.getTarget());
                if (findVertexCover(s, k - 1, vertexcover))
                    return true;
                vertexcover.remove(e.getTarget());

                break;
            }
        }

        return false;
    }

    public Object run(UndirectedGraph s, AlgorithmParameters p,
        Set<Object> selection, ProgressHandler onprogress) throws Exception {
        for (int k = 1;; k++) {
            Set<Vertex> vertexCover = new HashSet<>();
            if (findVertexCover(s, k, vertexCover))
                return vertexCover;
        }
    }
}
