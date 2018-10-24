/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.jgraphtbridge.algorithm;

import gralog.algorithm.AlgorithmDescription;
import gralog.structure.Vertex;
import gralog.algorithm.AlgorithmParameters;
import gralog.progresshandler.ProgressHandler;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 *
 */
@AlgorithmDescription(
    name = "JGraphT Bridge Example",
    text = "Test of the Bridge to JGraphT",
    url = "http://jgrapht.org/"
)

public class JGraphTBridgeExample extends JGraphTAlgorithm {

    @Override
    public Object jGraphTRun(Graph<Vertex, DefaultEdge> g,
        AlgorithmParameters ap, Set<Object> selection,
        ProgressHandler onprogress) throws Exception {
        int i = 0;
        for (Vertex v : g.vertexSet())
            i++;
        return "" + i + " nodes (computed through JGraphTBridge)";
    }
}
