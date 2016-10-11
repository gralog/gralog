/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.structure.*;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author viktor
 */
@ExportFilterDescription(
        name = "Library of Efficient Data types and Algorithms (LEDA)",
        text = "",
        url = "http://www.algorithmic-solutions.info/leda_guide/graphs/leda_native_graph_fileformat.html",
        fileExtension = "lgr"
)
public class LEDAExport extends ExportFilter {

    public void export(DirectedGraph structure, OutputStreamWriter stream,
            ExportFilterParameters params) throws Exception {
        HashMap<Vertex, Integer> NodeIndex = new HashMap<Vertex, Integer>();
        Integer idx = 1;
        String linefeed = System.getProperty("line.separator");

        stream.write("LEDA.GRAPH" + linefeed);
        stream.write("void" + linefeed);
        stream.write("void" + linefeed);
        stream.write("-1" + linefeed);
        stream.write("# -1 = directed, -2 = undirected" + linefeed + linefeed);

        stream.write("# nodes" + linefeed);
        Set<Vertex> V = structure.getVertices();
        int n = V.size();
        stream.write("" + n + linefeed);
        for (Vertex v : V) {
            stream.write("|{}|" + linefeed);
            NodeIndex.put(v, idx++);
        }
        stream.write(linefeed);

        stream.write("# edges" + linefeed);
        Set<Edge> E = structure.getEdges();
        int m = E.size();
        stream.write("" + m + linefeed);
        for (Edge e : E)
            stream.write(NodeIndex.get(e.getSource()) + " " + NodeIndex.get(e.getTarget()) + " 0 |{}|" + linefeed);
        stream.write(linefeed);

    }
}
