/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import java.util.Arrays;
import java.util.List;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.CycleParameters;
import gralog.algorithm.PathParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.RandomGraphParameters;
import gralog.parser.IntSyntaxChecker;
import gralog.preferences.Preferences;
import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;

/**
 *
 */
@GeneratorDescription(
        name = "Path",
        text = "Generates a Path-Graph",
        url = "https://en.wikipedia.org/wiki/Path_graph"
)
//TODO: additional parameter: directed or undirected, edge weights??
public class Path extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        String n = Preferences.getInteger(this.getClass(), "Number of vertices", 5).toString();
        String directed = Preferences.getBoolean(this.getClass(), "directed", true).toString();
        List<String> initialValues = Arrays.asList(n,directed);
        return new PathParameters(initialValues);

    }

    @Override
    public Structure generate(AlgorithmParameters param) {
        int n = Integer.parseInt(((PathParameters)param).parameters.get(0));
        Preferences.setInteger(this.getClass(), "pathVertexNumber", n);
        boolean directed = Boolean.parseBoolean(((PathParameters)param).parameters.get(1));
        Preferences.setBoolean(this.getClass(), "directed", directed);

        Structure result;

        if (directed)
            result = new DirectedGraph();
        else
            result = new UndirectedGraph();

        Vertex first = result.addVertex();

        first.setCoordinates(0, 0);
        Vertex last = first;
        for (int i = 1; i < n; i++) {
            Vertex next = result.addVertex();
            next.setCoordinates(3 * i, 0);
            result.addEdge(last, next);
            last = next;
        }

        return result;
    }
}
