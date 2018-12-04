/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.RandomGraphParameters;
import gralog.preferences.Preferences;
import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.UndirectedGraph;
import gralog.structure.Vertex;

import java.util.*;

/**
 *
 */
@GeneratorDescription(
        name = "Random graph",
        text = "Generates a random graph according to the Erdős–Rényi model",
        url = "https://en.wikipedia.org/wiki/Erd%C5%91s%E2%80%93R%C3%A9nyi_model"
)
public class RandomGraph extends Generator {

    @Override
    public AlgorithmParameters getParameters() {

        String n = Preferences.getInteger(this.getClass(), "randomGraphVertexNumber", 15).toString();
        String p = Preferences.getDouble(this.getClass(), "edgeProbability", 0.5).toString();
        String directed = Preferences.getBoolean(this.getClass(), "directed", false).toString();
        List<String> initialValues = Arrays.asList(n,p,directed);

        return new RandomGraphParameters(initialValues);

    }

    /*
    *
     */
    @Override
    public Structure generate(AlgorithmParameters param) {
        int n = Integer.parseInt(((RandomGraphParameters)param).parameters.get(0));
        Preferences.setInteger(this.getClass(), "randomGraphVertexNumber", n);
        double p = Double.parseDouble(((RandomGraphParameters)param).parameters.get(1));
        Preferences.setDouble(this.getClass(), "edgeProbability", p);
        boolean directed = Boolean.parseBoolean(((RandomGraphParameters)param).parameters.get(2));
        Preferences.setBoolean(this.getClass(), "directed", directed);


        Structure result;
        if (directed)
            result = new DirectedGraph();
        else
            result = new UndirectedGraph();


        for (int i = 0; i < n; i++) {
            Vertex v = result.addVertex();
            v.setCoordinates(
                    Math.sin(i * 2 * Math.PI / n) * 0.5 * n + 3.5,
                    Math.cos(i * 2 * Math.PI / n) * 0.5 * n + 3.5
            );
        }

        ArrayList<Vertex> vertices = new ArrayList<>(result.getVertices());

        int PRECISION = 10000;
        Random r = new Random();

        for (int i = 0; i < vertices.size(); i++) {
            int j = 0;
            if (!directed)
                j = i +1;
            for (; j < vertices.size(); j++) {
                int next_random = r.nextInt(PRECISION);
                if (next_random < PRECISION * p)
                    result.addEdge(vertices.get(i), vertices.get(j));
            }
        }
        return result;
    }
}

