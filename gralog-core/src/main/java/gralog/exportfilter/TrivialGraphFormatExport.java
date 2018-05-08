/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.structure.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.io.OutputStreamWriter;
import java.util.Map;

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

    public void export(Structure structure, OutputStreamWriter stream,
        ExportFilterParameters params) throws Exception {
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i = 1;
        String linefeed = System.getProperty("line.separator");

        Collection<Vertex> V = structure.getVertices();
        for (Vertex v : V) {
            nodeIndex.put(v, i);
            stream.write(i + linefeed);
            i++;
        }

        stream.write("#" + linefeed);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E)
            stream.write(nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString() + linefeed);

        stream.write("#" + linefeed);
    }

    public static String exportToString(Structure structure){
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i = 1;
        String separator = System.getProperty("line.separator");

        String retString = "";

        Collection<Vertex> V = structure.getVertices();
        for (Vertex v : V) {
            nodeIndex.put(v, i);
            retString += Integer.toString(i) + separator;
            // stream.write(i + linefeed);
            i++;
        }

        retString += "#" + separator;

        // stream.write("#" + linefeed);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E){
            retString += nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString() + separator;
            // stream.write(nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString() + linefeed);
        }
        // stream.write("#" + linefeed);
        retString += "#" + separator;
        return retString;
    }

    @Override
    public Map<String, Vertex> getVertexNames(Structure structure,
        ExportFilterParameters params) throws Exception {
        Map<String, Vertex> result = new HashMap<>();
        Integer i = 1;
        Collection<Vertex> V = structure.getVertices();
        for (Vertex v : V)
            result.put("" + (i++), v);
        return result;
    }

    @Override
    public Map<String, Edge> getEdgeNames(Structure structure,
        ExportFilterParameters params) throws Exception {
        Map<String, Edge> result = new HashMap<>();
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();

        Integer i = 1;
        Collection<Vertex> V = structure.getVertices();
        for (Vertex v : V)
            nodeIndex.put(v, i++);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E)
            result.put(nodeIndex.get(e.getSource()) + ":" + nodeIndex.get(e.getTarget()), e);

        return result;
    }
}
