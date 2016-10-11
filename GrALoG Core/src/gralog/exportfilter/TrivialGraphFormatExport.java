/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.structure.*;

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
        HashMap<Vertex, Integer> NodeIndex = new HashMap<>();
        Integer i = 1;
        String linefeed = System.getProperty("line.separator");

        Set<Vertex> V = structure.getVertices();
        for (Vertex v : V) {
            NodeIndex.put(v, i);
            stream.write(i + linefeed);
            i++;
        }

        stream.write("#" + linefeed);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E)
            stream.write(NodeIndex.get(e.getSource()).toString() + " " + NodeIndex.get(e.getTarget()).toString() + linefeed);

        stream.write("#" + linefeed);
    }

    @Override
    public Map<String, Vertex> getVertexNames(Structure structure,
            ExportFilterParameters params) throws Exception {
        Map<String, Vertex> result = new HashMap<>();
        Integer i = 1;
        Set<Vertex> V = structure.getVertices();
        for (Vertex v : V)
            result.put("" + (i++), v);
        return result;
    }

    @Override
    public Map<String, Edge> getEdgeNames(Structure structure,
            ExportFilterParameters params) throws Exception {
        Map<String, Edge> result = new HashMap<>();
        HashMap<Vertex, Integer> NodeIndex = new HashMap<>();

        Integer i = 1;
        Set<Vertex> V = structure.getVertices();
        for (Vertex v : V)
            NodeIndex.put(v, i++);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E)
            result.put(NodeIndex.get(e.getSource()) + ":" + NodeIndex.get(e.getTarget()), e);

        return result;
    }
}
