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
    name = "Gralog Trivial Graph Format",
    text = "",
    url = "https://en.wikipedia.org/wiki/Trivial_Graph_Format",
    fileExtension = "gtgf"
)
public class GralogTrivialGraphFormatExport extends ExportFilter {

    public void export(Structure structure, OutputStreamWriter stream,
        ExportFilterParameters params) throws Exception {
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i;
        String linefeed = System.getProperty("line.separator");

        Collection<Vertex> V = structure.getVertices();
        for (Vertex v : V) {
            i = v.getId();
            nodeIndex.put(v, i);
            stream.write(i + linefeed);
        }

        stream.write("#" + linefeed);

        Set<Edge> E = (Set<Edge>)structure.getEdges();
        for (Edge e : E)
            stream.write(nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString() + " " + Integer.toString(e.getId()) + linefeed);
        stream.write("#" + linefeed);
    }

    public static String exportToString(Structure structure){
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        Integer i;
        String separator = System.getProperty("line.separator");

        String retString = "";

        Collection<Vertex> vertices = structure.getVertices();
        for (Vertex v : vertices) {
            i = v.getId();
            nodeIndex.put(v, i);
            retString += Integer.toString(i) + separator;
        }

        retString += "#" + separator;

        // stream.write("#" + linefeed);

        Set<Edge> edges = (Set<Edge>) structure.getEdges();
        for (Edge e : edges) {
            retString += nodeIndex.get(e.getSource()).toString() + " " + nodeIndex.get(e.getTarget()).toString()
                    + " " + Integer.toString(e.getId()) + separator;
            // stream.write(nodeIndex.get(e.getSource()).toString() + " "
            // + nodeIndex.get(e.getTarget()).toString() + linefeed);
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
        Collection<Vertex> vertices = structure.getVertices();
        for (Vertex v : vertices)
            result.put("" + Integer.toString(v.getId()), v);
        return result;
    }

    @Override
    public Map<String, Edge> getEdgeNames(Structure structure,
                                          ExportFilterParameters params) throws Exception {
        Map<String, Edge> result = new HashMap<>();
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();

        Integer i = 1;
        Collection<Vertex> vertices = structure.getVertices();
        for (Vertex v : vertices)
            nodeIndex.put(v, v.getId());

        Set<Edge> edges = (Set<Edge>) structure.getEdges();
        for (Edge e : edges)
            result.put("id:" + Integer.toString(e.getId()) + "; " + nodeIndex.get(e.getSource()) + ":"
                    + nodeIndex.get(e.getTarget()), e);

        return result;
    }
}
