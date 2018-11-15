/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.structure.Edge;
import gralog.structure.Structure;
import gralog.structure.Vertex;

import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
@ExportFilterDescription(
    name = "Trivial Graph Format",
    text = "",
    url = "https://en.wikipedia.org/wiki/Trivial_Graph_Format",
    fileExtension = "tgf"
)
public class TrivialGraphFormatExport extends ExportFilter {

    public static String exportToString(Structure structure) {
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i = 1;
        String separator = System.getProperty("line.separator");

        String retString = "";

        Collection<Vertex> setV = structure.getVertices();
        for (Vertex v : setV) {
            i = v.getId();
            nodeIndex.put(v, i);
            retString += Integer.toString(i) + separator;
            // stream.write(i + linefeed);
        }

        retString += "#" + separator;

        // stream.write("#" + linefeed);

        Set<Edge> setE = (Set<Edge>) structure.getEdges();
        for (Edge e : setE) {
            retString += nodeIndex.get(e.getSource()).toString()
                    + " " + nodeIndex.get(e.getTarget()).toString() + separator;
        }
        // stream.write("#" + linefeed);
        retString += "#" + separator;
        return retString;
    }

    public void export(Structure structure, OutputStreamWriter stream,
                       ExportFilterParameters params) throws Exception {
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i = 1;
        String linefeed = System.getProperty("line.separator");

        Collection<Vertex> setV = structure.getVertices();
        for (Vertex v : setV) {
            i = v.getId();
            nodeIndex.put(v, i);
            stream.write(i + linefeed);
        }

        stream.write("#" + linefeed);

        Set<Edge> setE = (Set<Edge>) structure.getEdges();
        for (Edge e : setE)
            stream.write(nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString() + linefeed);

        stream.write("#" + linefeed);
    }

    @Override
    public Map<String, Vertex> getVertexNames(Structure structure,
                                              ExportFilterParameters params) throws Exception {
        Map<String, Vertex> result = new HashMap<>();
        Integer i = 1;
        Collection<Vertex> setV = structure.getVertices();
        for (Vertex v : setV)
            result.put("" + v.getId(), v);
        return result;
    }

    @Override
    public Map<String, Edge> getEdgeNames(Structure structure,
                                          ExportFilterParameters params) throws Exception {
        Map<String, Edge> result = new HashMap<>();
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();

        Integer i = 1;
        Collection<Vertex> setV = structure.getVertices();
        for (Vertex v : setV)
            nodeIndex.put(v, v.getId());

        Set<Edge> setE = (Set<Edge>) structure.getEdges();
        for (Edge e : setE)
            result.put(nodeIndex.get(e.getSource()) + ":" + nodeIndex.get(e.getTarget()), e);

        return result;
    }
}
