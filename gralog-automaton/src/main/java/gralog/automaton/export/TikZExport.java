/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.export;


import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.automaton.Transition;
import gralog.exportfilter.ExportFilter;
import gralog.exportfilter.ExportFilterDescription;
import gralog.exportfilter.ExportFilterParameters;
import gralog.exportfilter.IndentedWriter;
import gralog.rendering.Vector2D;
import gralog.structure.EdgeIntermediatePoint;

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

    public void export(Automaton structure, OutputStreamWriter stream,
        ExportFilterParameters params) throws Exception {
        IndentedWriter out = new IndentedWriter(stream, 4);

        out.writeLine("%\\documentclass {article}");
        out.writeLine("%\\usepackage {pgf}");
        out.writeLine("%\\usepackage {tikz}");
        out.writeLine("%\\usepackage {amsmath, amssymb}");
        out.writeLine("%\\usetikzlibrary {arrows.meta,automata}");
        out.writeLine("%\\usepackage[utf8] {inputenc}");

        out.writeLine("%\\begin {document}");
        out.increaseIndent();
        out.writeLine("\\begin {tikzpicture}[scale=0.8,auto]");
        out.increaseIndent();
        out.writeLine("\\tikzset {>=Stealth}");
        out.writeLine("\\tikzstyle {every path}=[->,thick]");
        out.writeLine("\\tikzstyle {every state}=[circle,fill=white,draw=black,text=black,thin]");

        HashMap<State, Integer> nodeIndex = new HashMap<>();
        int i = 1;
        for (State s : structure.getVertices()) {
            nodeIndex.put(s, i);
            out.write("\\node[state");
            if (s.finalState) {
                out.writeNoIndent(",accepting");
            }
            if (s.startState) {
                out.writeNoIndent(",initial");
            }
            final String label = s.label.isEmpty() ? "" : "$" + s.label + "$";
            out.writeLineNoIndent("] (q" + i + ") at ("
                + s.getCoordinates().getX() + "cm,"
                + (-s.getCoordinates().getY()) + "cm) {" + label + "};");
            ++i;
        }

        out.writeLine("");

        for (Transition t : structure.getEdges()) {
            double halfLength = t.length() / 2.0;
            Vector2D from = t.getSource().getCoordinates();
            double distance = 0.0;

            out.write("\\draw (q" + nodeIndex.get(t.getSource()) + ")");
            for (EdgeIntermediatePoint c : t.intermediatePoints) {
                Vector2D betw = new Vector2D(c.getX(), c.getY());
                Double segmentlength = betw.minus(from).length();

                out.writeNoIndent(" --");
                if (distance < halfLength && halfLength <= distance + segmentlength) {
                    out.writeNoIndent(" node {$" + (t.symbol.isEmpty() ? "\\varepsilon" : t.symbol) + "$}");
                }
                out.writeNoIndent(" (" + c.getX() + "cm," + (-c.getY()) + "cm)");

                distance += segmentlength;
                from = betw;
            }

            out.writeNoIndent(" --");
            if (distance < halfLength) {
                out.writeNoIndent(" node {$" + (t.symbol.isEmpty() ? "\\varepsilon" : t.symbol) + "$}");
            }
            out.writeLineNoIndent(" (q" + nodeIndex.get(t.getTarget()) + ");");
        }

        out.decreaseIndent();
        out.writeLine("\\end {tikzpicture}");
        out.decreaseIndent();
        out.writeLine("%\\end {document}");
    }
}
