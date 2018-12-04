/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.GridParameters;
import gralog.preferences.Preferences;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        String length = Preferences.getInteger(this.getClass(), "gridlength", 5).toString();
        String width= Preferences.getInteger(this.getClass(), "gridwidth", 6).toString();
        List<String> initialValues = Arrays.asList(length,width);

        return new GridParameters(initialValues);

    }

    @Override
    public Structure generate(AlgorithmParameters p) {
        int length = Integer.parseInt(((GridParameters)p).parameters.get(0));
        Preferences.setInteger(this.getClass(), "gridlength", length);
        int width= Integer.parseInt(((GridParameters)p).parameters.get(1));
        Preferences.setInteger(this.getClass(), "gridwidth", width);

        UndirectedGraph result = new UndirectedGraph();

        ArrayList<Vertex> last = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            Vertex temp = result.addVertex();
            last.add(temp);
            temp.setCoordinates(1d, 2d * j + 1d);
            if (j > 0)
                result.addEdge(last.get(j - 1), temp);
        }

        for (int i = 1; i < width; i++) {
            ArrayList<Vertex> next = new ArrayList<>();
            Vertex lasttemp = null;
            for (int j = 0; j < length; j++) {
                Vertex temp = result.addVertex();
                next.add(temp);
                temp.setCoordinates(2d * i + 1d, 2d * j + 1d);
                if (j > 0)
                    result.addEdge(lasttemp, temp);
                result.addEdge(last.get(j), temp);

                lasttemp = temp;
            }

            last = next;
        }

        return result;
    }
}
