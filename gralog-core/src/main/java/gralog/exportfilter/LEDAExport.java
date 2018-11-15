/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.structure.DirectedGraph;
import gralog.structure.Edge;
import gralog.structure.Vertex;

import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 *
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
        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        String linefeed = System.getProperty("line.separator");

        stream.write("LEDA.GRAPH" + linefeed);
        stream.write("void" + linefeed);
        stream.write("void" + linefeed);
        stream.write("-1" + linefeed);
        stream.write("# -1 = directed, -2 = undirected" + linefeed + linefeed);

        stream.write("# nodes" + linefeed);
        Collection<Vertex> vertices = structure.getVertices();
        stream.write("" + vertices.size() + linefeed);
        int idx = 0;
        for (Vertex v : vertices) {
            stream.write("|{}|" + linefeed);
            nodeIndex.put(v, ++idx);
        }
        stream.write(linefeed);

        stream.write("# edges" + linefeed);
        Set<Edge> edges = structure.getEdges();
        int m = edges.size();
        stream.write("" + m + linefeed);
        for (Edge e : edges)
            stream.write(nodeIndex.get(e.getSource()) + " " + nodeIndex.get(e.getTarget()) + " 0 |{}|" + linefeed);
        stream.write(linefeed);
    }
}
