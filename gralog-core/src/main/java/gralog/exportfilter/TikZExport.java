/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.exportfilter;

import gralog.structure.*;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

/**
 *
 */
@ExportFilterDescription(
        name = "TikZ ist kein Zeichenprogramm",
        text = "",
        url = "http://www.texample.net/tikz/",
        fileExtension = "tikz"
)
public class TikZExport extends ExportFilter {

    public void export(DirectedGraph structure, OutputStreamWriter stream,
            ExportFilterParameters params) throws Exception {
        String linefeed = System.getProperty("line.separator");

        stream.write("%\\documentclass{article}" + linefeed);
        stream.write("%\\usepackage{pgf}" + linefeed);
        stream.write("%\\usepackage{tikz}" + linefeed);
        stream.write("%\\usepackage{amsmath, amssymb}" + linefeed);
        stream.write("%\\usetikzlibrary{arrows}" + linefeed);
        stream.write("%\\usepackage[utf8]{inputenc}" + linefeed + linefeed);

        stream.write("%\\begin{document}" + linefeed);
        stream.write("    \\begin{tikzpicture}[scale=1.0]" + linefeed);
        stream.write("        \\tikzstyle{every node}=[circle,fill=blue!20,draw=black,text=black]" + linefeed + linefeed);

        HashMap<Vertex, Integer> NodeIndex = new HashMap<>();
        int i = 1;
        Set<Vertex> V = structure.getVertices();
        for (Vertex v : V) {
            NodeIndex.put(v, i);
            stream.write("        \\node (n" + i + ") at ("
                         + v.coordinates.getX() + "cm,"
                         + v.coordinates.getY() + "cm) {};" + linefeed);
            ++i;
        }

        stream.write("        " + linefeed);

        Set<Edge> E = structure.getEdges();
        for (Edge e : E) {
            stream.write("        \\draw (n" + NodeIndex.get(e.getSource()) + ")");
            for (EdgeIntermediatePoint c : e.intermediatePoints)
                stream.write(" edge[-] (" + c.getX() + "cm," + c.getY() + "cm)");
            stream.write(" edge[-] (n" + NodeIndex.get(e.getTarget()) + ");" + linefeed);
        }

        stream.write("    \\end{tikzpicture}" + linefeed);
        stream.write("%\\end{document}" + linefeed);
    }
}