/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.exportfilter;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
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
    fileExtension = "tex"
)
public class TikZExport extends ExportFilter {

    public void export(Structure<Vertex, Edge> structure,
                       OutputStreamWriter stream,
                       ExportFilterParameters params) throws Exception {
        IndentedWriter out = new IndentedWriter(stream, 4);

        out.writeLine("\\documentclass[crop,tikz, border=5pt]{standalone}");
        out.writeLine("\\usepackage{pgf}");
        out.writeLine("\\usepackage{tikz}");
        out.writeLine("\\usepackage{amsmath, amssymb}");
        out.writeLine("\\usetikzlibrary{arrows.meta}");
        out.writeLine("\\usetikzlibrary{arrows}");
        out.writeLine("\\usetikzlibrary{calc}");
        out.writeLine("\\usetikzlibrary{shapes}");
        out.writeLine("\\usepackage[utf8]{inputenc}");
        out.writeLine("");
        out.writeLine("");
        
        
        out.writeLine("\\begin{document}");
        out.increaseIndent();
        out.writeLine("\\begin{tikzpicture}[auto]");
        out.increaseIndent();
        out.writeLine("\\tikzset{>=Stealth}");
        out.writeLine("\\tikzstyle{every path}=[->]");
        out.writeLine("\\tikzstyle{every node}=[ellipse,fill=white,draw=black,"
                + "text=black,thin,minimum width=20,minimum height=20, line width=0.26]");
        out.writeLine("\\tikzset{quadratic bezier/.style={ to path={(\\tikztostart) .. controls($#1!1/3!(\\tikztostart)$)");
        out.writeLine("and ($#1!1/3!(\\tikztotarget)$).. (\\tikztotarget)}}}");
        out.writeLine("");

        HashMap<Vertex, Integer> nodeIndex = new HashMap<>();
        String gfillcolor = "";
        String gstrokecolor = "";
        for (Vertex v : structure.getVertices()) {
            nodeIndex.put(v, v.id);
            final String label = v.label.isEmpty() ? "" : "$" + v.label + "$";

            String properties = "";
            if (!v.shape.getClass().getSimpleName().toLowerCase().equals("ellipse")) {
            	properties = properties + v.shape.getClass().getSimpleName().toLowerCase() + ", ";
            }
            if (!v.shape.sizeBox.width.equals(1.0)) {
            	properties = properties + "minimum width = " + Math.round(1000.0 * v.shape.sizeBox.width) / 100.0 + ", ";
            }
            if (!v.shape.sizeBox.height.equals(1.0)) {
            	properties = properties + "minimum height = " + Math.round(1000.0 * v.shape.sizeBox.height) / 100.0 + ", ";
            }
            if (!v.strokeWidth.equals(2.54 / 96)) {
            	properties = properties + "line width=" + Math.round(1000.0 * v.strokeWidth) / 100.0 + ", ";
            }
            if (!v.fillColor.equals(GralogColor.WHITE)) {
            	if (!gfillcolor.equals(v.fillColor.toHtmlString().substring(1))) {
            		out.writeLine("\\definecolor{g-fillcolor}{HTML}{" + v.fillColor.toHtmlString().substring(1) + "}");
            		gfillcolor = v.fillColor.toHtmlString().substring(1);
            	}
            	properties = properties + "fill=g-fillcolor, ";
        	}
            if (!v.strokeColor.equals(GralogColor.BLACK)) {
            	if (!gstrokecolor.equals(v.fillColor.toHtmlString().substring(1))) {
                	out.writeLine("\\definecolor{g-strokecolor}{HTML}{" + v.strokeColor.toHtmlString().substring(1) + "}");
            		gstrokecolor = v.strokeColor.toHtmlString().substring(1);
            	}
                properties = properties + "draw=g-strokecolor, ";
            }
            if(properties.length()>0) {
            	properties = properties.substring(0, properties.length()-2);
            }
            out.writeLine("\\node [" + properties + "] " + "(n" + v.id + ") at ("
                    + Math.round(1000.0 * v.getCoordinates().getX()) / 1000.0 + ","
                    + (-Math.round(1000.0 * v.getCoordinates().getY()) / 1000.0) + ") {" + label + "};");

//          ++i;
        }

        out.writeLine("");

        for (Edge e : structure.getEdges()) {
            double halfLength = e.length() / 2.0;
            Vector2D from = e.getSource().getCoordinates();
            double distance = 0.0;
            // Tikz implements quadratic Bezier curves as cubic curves where both
            // middle control points coincide, which leads to wrong curves.
            // Hence the quadratic case (one control point) is a special case.
            String controlPointCoord = "";
            if (e.controlPoints.size() == 1 && e.edgeType == Edge.EdgeType.BEZIER) {
                ControlPoint c = e.controlPoints.get(0);
                controlPointCoord = "crtl" + nodeIndex.get(e.getSource());
                out.writeLine("\\coordinate (" + controlPointCoord + ")  at ("
                        + Math.round(1000.0 * c.position.getX()) / 1000.0 + "," + -Math.round(1000.0 * c.position.getY()) / 1000.0 + ");");
            }
            String properties = "";
            if (!e.isDirected)
                properties = properties + "-, ";
            if (!e.color.equals(GralogColor.BLACK)) {
            	if (!gfillcolor.equals(e.color.toHtmlString().substring(1))) {
            		out.writeLine("\\definecolor{g-fillcolor}{HTML}{" + e.color.toHtmlString().substring(1) + "}");
            		gfillcolor = e.color.toHtmlString().substring(1);
            	}
            	properties = properties + "g-fillcolor, ";
        	}
            if (!e.type.equals(GralogGraphicsContext.LineType.PLAIN)) {
            	properties = properties + e.type.toString().toLowerCase() + ", ";
            }
            if (!e.thickness.equals(2.54/96)) {
            	properties = properties + "line width=" + Math.round(1000.0 * e.thickness) / 100.0 + ", ";
            }
            if(properties.length()>0) {
            	properties = properties.substring(0, properties.length()-2);
            }
            out.write("\\draw [" + properties + "]");
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
                            + ")$)..");
                } else if (e.controlPoints.size() == 2){
                    out.writeNoIndent(" .. controls ("
                            + Math.round(1000.0 * e.controlPoints.get(0).position.getX()) / 1000.0
                            + ",-" + Math.round(1000.0 * e.controlPoints.get(0).position.getY()) / 1000.0 + ") and ("
                            + Math.round(1000.0 * e.controlPoints.get(1).position.getX()) / 1000.0
                            + ",-" + Math.round(1000.0 * e.controlPoints.get(1).position.getY()) / 1000.0 + ") .. ");
                } else{
                    out.writeNoIndent(" to");
                }
            } else {
                for (ControlPoint c : e.controlPoints) {
                    out.writeNoIndent(" to (" + Math.round(1000.0 * c.position.getX()) / 1000.0 + "," + (-Math.round(1000.0 * c.position.getY()) / 1000.0) + ")");
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
        out.writeLine("\\end{document}");
    }
}
