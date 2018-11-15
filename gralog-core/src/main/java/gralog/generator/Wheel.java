/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.parser.IntSyntaxChecker;
import gralog.preferences.Preferences;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;

/**
 *
 */
@GeneratorDescription(
        name = "Wheel",
        text = "Generates a Wheel-Graph",
        url = "https://en.wikipedia.org/wiki/Wheel_graph"
)
public class Wheel extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
                "Number of rim vertices",
                Preferences.getInteger(this.getClass(), "size", 5).toString(),
                new IntSyntaxChecker(1, Integer.MAX_VALUE),
                "");
    }

    @Override
    public Structure generate(AlgorithmParameters p) {
        int n = Integer.parseInt(((StringAlgorithmParameter) p).parameter);
        Preferences.setInteger(this.getClass(), "size", n);

        UndirectedGraph result = new UndirectedGraph();

        Vertex center = result.addVertex();
        center.setCoordinates(3.5, 3.5);


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

            result.addEdge(result.createEdge(last, next));
            result.addEdge(result.createEdge(last, center));

            last = next;

        }
        result.addEdge(result.createEdge(last, first));
        result.addEdge(result.createEdge(last, center));

        return result;
    }
}
