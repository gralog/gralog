/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.exportfilter;

import gralog.rendering.GralogColor;
import gralog.rendering.Vector2D;
import gralog.structure.Edge;
import gralog.structure.EdgeIntermediatePoint;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import gralog.structure.controlpoints.ControlPoint;

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

    public void export(Structure<Vertex, Edge> structure,
                       OutputStreamWriter stream,
                       ExportFilterParameters params) throws Exception {
        IndentedWriter out = new IndentedWriter(stream, 4);

        out.writeLine("%\\documentclass{article}");
        out.writeLine("%\\usepackage{pgf}");
        out.writeLine("%\\usepackage{tikz}");
        out.writeLine("%\\usepackage{amsmath, amssymb}");
        out.writeLine("%\\usetikzlibrary{arrows.meta}");
        out.writeLine("%\\usetikzlibrary{arrows}");
        out.writeLine("%\\usetikzlibrary{calc}");
        out.writeLine("%\\usetikzlibrary{shapes}");
        out.writeLine("%\\usepackage[utf8]{inputenc}");

        out.writeLine("%\\begin{document}");
        out.increaseIndent();
        out.writeLine("\\begin{tikzpicture}[auto]");
        out.increaseIndent();
        out.writeLine("\\tikzset{>=Stealth}");
        out.writeLine("\\tikzstyle{every path}=[->,thick]");
        out.writeLine("\\tikzstyle{every node}=[circle,fill=white,draw=black,"
                + "text=black,thin,minimum size=5pt,inner sep=1.5pt]");
        out.writeLine("\\tikzset{quadratic bezier/.style={ to path={(\\tikztostart) .. controls($#1!1/3!(\\tikztostart)$)");
        out.writeLine("and ($#1!1/3!(\\tikztotarget)$).. (\\tikztotarget)}}}");
        out.writeLine("");

        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();

        for (Vertex v : structure.getVertices()) {
            nodeIndex.put(v, v.id);
            final String label = v.label.isEmpty() ? "" : "$" + v.label + "$";

            //String properties = "ellipse, minimum width = " + v.shape.sizeBox.width
            // + "cm, minimum height = " + v.shape.sizeBox.height;

            String properties = v.shape.getClass().getSimpleName().toLowerCase() + ", minimum width = "
                    + v.shape.sizeBox.width + "cm, minimum height = " + v.shape.sizeBox.height + "cm";
            out.writeLine("\\definecolor{gralog-fill-color}{HTML}{" + v.fillColor.toHtmlString().substring(1) + "}");
            properties = properties + ", fill=gralog-fill-color";
/*        if (!v.fillColor.equals(GralogColor.WHITE)) {
  //          out.writeLine("\\definecolor{gralog-fill-color}{HTML}{" + v.fillColor.toHtmlString().substring(1) + "}");
    //        properties = properties + ", fill=gralog-fill-color";
        }*/
            if (!v.strokeColor.equals(GralogColor.BLACK)) {
                out.writeLine("\\definecolor{gralog-stroke-color}{HTML}{"
                        + v.strokeColor.toHtmlString().substring(1) + "}");
                properties = properties + ", draw=gralog-stroke-color";
            }
            properties = properties + ", line width=" + v.strokeWidth + "cm";

            out.writeLine("\\node [" + properties + "] " + "(n" + v.id + ") at ("
                    + v.coordinates.getX() + "cm,"
                    + (-v.coordinates.getY()) + "cm) {" + label + "};");

//          ++i;
        }

        out.writeLine("");

        for (Edge e : structure.getEdges()) {
            double halfLength = e.length() / 2.0;
            Vector2D from = e.getSource().coordinates;
            double distance = 0.0;
            // Tikz implements quadratic Bezier curves as cubic curves where both
            // middle control points coincide, which leads to wrong curves.
            // Hence the quadratic case (one control point) is a special case.
            String controlPointCoord = "";
            if (e.controlPoints.size() == 1 && e.edgeType == Edge.EdgeType.BEZIER) {
                ControlPoint c = e.controlPoints.get(0);
                controlPointCoord = "crtl" + nodeIndex.get(e.getSource());
                out.writeLine("\\coordinate (" + controlPointCoord + ")  at ("
                        + c.position.getX() + "," + -c.position.getY() + ");");
            }
            if (e.isDirected)
                out.write("\\draw");
            else
                out.write("\\draw [-]");

            out.writeNoIndent(" (n" + nodeIndex.get(e.getSource()) + ")");


            if (e.edgeType == Edge.EdgeType.BEZIER) {
                if (e.controlPoints.size() == 1) {
                    out.writeNoIndent(" .. controls($("
                            + controlPointCoord + ")!1/3!(n"
                            + nodeIndex.get(e.getSource())
                            + ")$)"
                            + " and ($("
                            + controlPointCoord
                            + ")!1/3!(n"
                            + nodeIndex.get(e.getTarget())
                            + "1)$)..");
                } else if (e.controlPoints.size() == 2){
                    out.writeNoIndent(" .. controls ("
                            + e.controlPoints.get(0).position.getX()
                            + ",-" + e.controlPoints.get(0).position.getY() + ") and ("
                            + e.controlPoints.get(1).position.getX()
                            + ",-" + e.controlPoints.get(1).position.getY() + ") .. ");
                } else{
                    out.writeNoIndent(" to");
                }
            } else {
                for (ControlPoint c : e.controlPoints) {
                    out.writeNoIndent(" to (" + c.position.getX() + "cm," + (-c.position.getY()) + "cm)");
                }

                out.writeNoIndent(" to");
            }
            if (!e.label.isEmpty())
                out.writeNoIndent(" node [draw=none,fill=none,midway,sloped] {$" + e.label + "$}");
            out.writeLineNoIndent(" (n" + nodeIndex.get(e.getTarget()) + ");");
        }

        out.decreaseIndent();
        out.writeLine("\\end{tikzpicture}");
        out.decreaseIndent();
        out.writeLine("%\\end{document}");
    }
}
