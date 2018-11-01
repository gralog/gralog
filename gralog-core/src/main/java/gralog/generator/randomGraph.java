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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 *
 */
@GeneratorDescription(
        name = "Grid",
        text = "Generates a Grid-Graph",
        url = "https://en.wikipedia.org/wiki/Lattice_graph"
)
public class randomGraph extends Generator {

    @Override
    public AlgorithmParameters getParameters() {
        ArrayList<String> initialValues = new ArrayList<>(3);
        String n = Preferences.getInteger(this.getClass(), "randomGraphVertexNumber", 15).toString();
        String p = Preferences.getDouble(this.getClass(), "edgeProbability", 0.5).toString();
        String directed = Preferences.getBoolean(this.getClass(), "directed", false).toString();
        initialValues.set(0, n);
        initialValues.set(1, p);
        initialValues.set(2, directed);

        return new RandomGraphParameters(initialValues);

    }

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
        Iterator<Vertex> vIt = result.getVertices().iterator();
        while (vIt.hasNext()) {
            Vertex v = vIt.next();
            vIt.forEachRemaining(w -> {
                    int precision = 10000;
                    Random r = new Random();
                    double random = precision*p / 2;
                    if (random < precision/2)
                        result.addEdge(v,w);
            });
        }
        return result;
    }
}

