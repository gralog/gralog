/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.parser.IntSyntaxChecker;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.preferences.Preferences;
import gralog.rendering.Vector2D;
import gralog.structure.*;

/**
 *
 */
@GeneratorDescription(
    name = "Path",
    text = "Generates a Path-Graph",
    url = "https://en.wikipedia.org/wiki/Path_graph"
)
//TODO additional parameter: directed or undirected, edge weights	??
public class Path extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
            "Number of vertices",
            Preferences.getInteger(this.getClass(), "vertices", 5).toString(),//this defValue plays no role
            new IntSyntaxChecker(1, Integer.MAX_VALUE),
            "");
    }

    @Override
    public Structure generate(AlgorithmParameters p) {
        int n = Integer.parseInt(((StringAlgorithmParameter) p).parameter);
        Preferences.setInteger(this.getClass(), "vertices", n);

        Structure result;

        if (p.directed)
            result = new DirectedGraph();
        else
            result = new UndirectedGraph();

        Vertex first = result.addVertex();

        first.setCoordinates(0,0);
        Vertex last = first;
        for (int i=1; i<n; i++) {
        	Vertex next = result.addVertex();
        	next.setCoordinates(3*i,0);
        	result.addEdge(last,next);
        	last = next;
        }

        return result;
    }
}
