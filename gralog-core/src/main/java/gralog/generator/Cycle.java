/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import java.util.Arrays;
import java.util.List;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.CycleParameters;
import gralog.algorithm.GridParameters;
import gralog.algorithm.PathParameters;
import gralog.parser.IntSyntaxChecker;
import gralog.preferences.Preferences;
import gralog.structure.DirectedGraph;
import gralog.structure.UndirectedGraph;
import gralog.structure.Structure;
import gralog.structure.Vertex;

/**
 *
 */
@GeneratorDescription(
        name = "Cycle",
        text = "Generates a Cyclic-Graph",
        url = "https://en.wikipedia.org/wiki/Cycle_(graph_theory)"
)
public class Cycle extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        String n = Preferences.getInteger(this.getClass(), "Number of vertices", 5).toString();
        String directed = Preferences.getBoolean(this.getClass(), "directed", true).toString();

        List<String> initialValues = Arrays.asList(n,directed);
        return new CycleParameters(initialValues);
    }

    @Override
    public Structure generate(AlgorithmParameters p) {
    	int n = Integer.parseInt(((CycleParameters)p).parameters.get(0));
        Preferences.setInteger(this.getClass(), "Number of vertices", n);
        boolean directed = Boolean.parseBoolean(((CycleParameters)p).parameters.get(1));
        Preferences.setBoolean(this.getClass(), "directed", directed);
        Structure result;
        
        if (directed)
        	result = new DirectedGraph();
        else 
        	result = new UndirectedGraph();
        
        Vertex first = result.addVertex();

        first.setCoordinates(
                Math.sin(0 * 2 * Math.PI / n) * 0.5 * n + 3.5,
                Math.cos(0 * 2 * Math.PI / n) * 0.5 * n + 3.5
        );

        Vertex last = first;
        for (int i = 1; i < n; i++) {
            Vertex next = result.addVertex();
            next.setCoordinates(
                    Math.sin(i * 2 * Math.PI / n) * 0.5 * n + 3.5,
                    Math.cos(i * 2 * Math.PI / n) * 0.5 * n + 3.5
            );
            result.addEdge(last, next);

            last = next;
        }
        result.addEdge(last, first);

        return result;
    }
}
