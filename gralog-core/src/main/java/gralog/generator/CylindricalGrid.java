/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.generator;

import gralog.algorithm.AlgorithmParameters;
import gralog.algorithm.GridParameters;
import gralog.preferences.Preferences;
import gralog.structure.DirectedGraph;
import gralog.structure.Structure;
import gralog.structure.Vertex;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
@GeneratorDescription(
        name = "Cylindrical Grid",
        text = "Generates a Cylindrical Grid (the directed equivalent of an undirected grid)",
        url = ""
)
public class CylindricalGrid extends Generator {

    // null means it has no parameters
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

        DirectedGraph result = new DirectedGraph();

        ArrayList<Vertex> first = new ArrayList<>();
        for (int j = 0; j < length; j++) {
            Vertex temp = result.addVertex();
            first.add(temp);
            temp.setCoordinates(
                    Math.sin(0 * 2 * Math.PI / width) * 2 * (j + 2),
                    Math.cos(0 * 2 * Math.PI / width) * 2 * (j + 2)
            );
            if (j > 0)
                result.addEdge(first.get(j - 1), temp);
        }

        ArrayList<Vertex> last = first;
        for (int i = 1; i < width; i++) {
            ArrayList<Vertex> next = new ArrayList<>();
            Vertex lasttemp = null;
            for (int j = 0; j < length; j++) {
                Vertex temp = result.addVertex();
                next.add(temp);
                temp.setCoordinates(
                        Math.sin(i * 2 * Math.PI / width) * 2 * (j + 2),
                        Math.cos(i * 2 * Math.PI / width) * 2 * (j + 2)
                );
                if (lasttemp != null)
                    if (i % 2 == 0)
                        result.addEdge(lasttemp, temp);
                    else
                        result.addEdge(temp, lasttemp);

                result.addEdge(last.get(j), temp);

                lasttemp = temp;
            }

            last = next;
        }

        for (int i = 0; i < length; i++)
            result.addEdge(last.get(i), first.get(i));

        return result;
    }
}
