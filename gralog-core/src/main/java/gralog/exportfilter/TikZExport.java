/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import gralog.rendering.GralogColor;
import gralog.rendering.Vector2D;
import gralog.structure.*;

import java.io.OutputStreamWriter;
import java.util.HashMap;

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
        IndentedWriter out = new IndentedWriter(stream, 4);

        out.writeLine("%\\documentclass{article}");
        out.writeLine("%\\usepackage{pgf}");
        out.writeLine("%\\usepackage{tikz}");
        out.writeLine("%\\usepackage{amsmath, amssymb}");
        out.writeLine("%\\usetikzlibrary{arrows.meta}");
        out.writeLine("%\\usepackage[utf8]{inputenc}");

        out.writeLine("%\\begin{document}");
        out.increaseIndent();
        out.writeLine("\\begin{tikzpicture}[scale=0.6,auto]");
        out.increaseIndent();
        out.writeLine("\\tikzset{>=Stealth}");
        out.writeLine("\\tikzstyle{every path}=[->,thick]");
        out.writeLine("\\tikzstyle{every node}=[circle,fill=white,draw=black,text=black,thin,minimum size=16pt,inner sep=1.5pt]");
        out.writeLine("");

        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        int i = 1;
        for (Vertex v : structure.getVertices()) {
            nodeIndex.put(v, i);
            final String label = v.label.isEmpty() ? "" : "$" + v.label + "$";
            String color = "";
            if (!v.fillColor.equals(GralogColor.WHITE)) {
                out.writeLine("\\definecolor{gralog-fill-color}{HTML}{" + v.fillColor.toHtmlString().substring(1) + "}");
                color = "[fill=gralog-fill-color] ";
            }
            out.writeLine("\\node " + color + "(n" + i + ") at ("
                + v.coordinates.getX() + "cm,"
                + (-v.coordinates.getY()) + "cm) {" + label + "};");
            ++i;
        }

        out.writeLine("");

        for (Edge e : structure.getEdges()) {
            double halfLength = e.length() / 2.0;
            Vector2D from = e.getSource().coordinates;
            double distance = 0.0;

            if (e.isDirected)
                out.write("\\draw");
            else
                out.write("\\draw [-]");

            out.writeNoIndent("(n" + nodeIndex.get(e.getSource()) + ")");
            for (EdgeIntermediatePoint c : e.intermediatePoints) {
                Vector2D betw = new Vector2D(c.getX(), c.getY());
                double segmentlength = betw.minus(from).length();

                out.writeNoIndent(" to");
                if (distance < halfLength && halfLength <= distance + segmentlength && !e.label.isEmpty())
                    out.writeNoIndent(" node [draw=none,fill=none] {$" + e.label + "$}");
                out.writeNoIndent(" (" + c.getX() + "cm," + (-c.getY()) + "cm)");

                distance += segmentlength;
                from = betw;
            }

            out.writeNoIndent(" to");
            if (distance < halfLength && !e.label.isEmpty())
                out.writeNoIndent(" node [draw=none,fill=none] {$" + e.label + "$}");
            out.writeLineNoIndent(" (n" + nodeIndex.get(e.getTarget()) + ");");
        }

        out.decreaseIndent();
        out.writeLine("\\end{tikzpicture}");
        out.decreaseIndent();
        out.writeLine("%\\end{document}");
    }
}
