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

import java.util.ArrayList;

/**
 *
 */
@GeneratorDescription(
        name = "Grid",
        text = "Generates a Grid-Graph",
        url = "https://en.wikipedia.org/wiki/Lattice_graph"
)
public class Grid extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        return new StringAlgorithmParameter(
                "Size",
                Preferences.getInteger(this.getClass(), "size", 5).toString(),
                new IntSyntaxChecker(1, Integer.MAX_VALUE),
                "");
    }

    @Override
    public Structure generate(AlgorithmParameters p) {
        int n = Integer.parseInt(((StringAlgorithmParameter) p).parameter);
        Preferences.setInteger(this.getClass(), "size", n);

        UndirectedGraph result = new UndirectedGraph();

        ArrayList<Vertex> last = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            Vertex temp = result.addVertex();
            last.add(temp);
            temp.setCoordinates(1d, 2d * j + 1d);
            if (j > 0)
                result.addEdge(result.createEdge(last.get(j - 1), temp));
        }

        for (int i = 1; i < n; i++) {
            ArrayList<Vertex> next = new ArrayList<>();
            Vertex lasttemp = null;
            for (int j = 0; j < n; j++) {
                Vertex temp = result.addVertex();
                next.add(temp);
                temp.setCoordinates(2d * i + 1d, 2d * j + 1d);
                if (j > 0)
                    result.addEdge(result.createEdge(lasttemp, temp));
                result.addEdge(result.createEdge(last.get(j), temp));

                lasttemp = temp;
            }

            last = next;
        }

        return result;
    }
}
