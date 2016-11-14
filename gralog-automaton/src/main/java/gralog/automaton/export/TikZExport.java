/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.automaton.export;

import gralog.structure.*;
import gralog.automaton.*;
import gralog.exportfilter.*;
import gralog.rendering.Vector2D;

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

    public void export(Automaton structure, OutputStreamWriter stream,
        ExportFilterParameters params) throws Exception {
        String linefeed = System.getProperty("line.separator");

        stream.write("%\\documentclass{article}" + linefeed);
        stream.write("%\\usepackage{pgf}" + linefeed);
        stream.write("%\\usepackage{tikz}" + linefeed);
        stream.write("%\\usepackage{amsmath, amssymb}" + linefeed);
        stream.write("%\\usetikzlibrary{arrows,automata}" + linefeed);
        stream.write("%\\usepackage[utf8]{inputenc}" + linefeed + linefeed);

        stream.write("%\\begin{document}" + linefeed);
        stream.write("    \\begin{tikzpicture}[->]" + linefeed);
        stream.write("        \\tikzstyle{every state}=[fill=white,draw=black,text=black]" + linefeed + linefeed);

        HashMap<State, Integer> nodeIndex = new HashMap<>();
        int i = 1;
        Set<State> V = structure.getVertices();
        for (Vertex v : V) {
            State s = (State) v;
            nodeIndex.put(s, i);
            stream.write("        \\node[state");
            if (s.finalState)
                stream.write(",accepting");
            if (s.startState)
                stream.write(",initial");
            stream.write("] (q" + i + ") at (" + s.coordinates.get(0) + "cm," + s.coordinates.get(1) + "cm) {$q_{" + i + "}$};" + linefeed);
            ++i;
        }

        stream.write("        " + linefeed);

        Set<Transition> E = structure.getEdges();
        for (Edge e : E) {
            Transition t = (Transition) e;

            Double halfLength = t.length() / 2.0;
            Vector2D from = t.getSource().coordinates;
            Vector2D to = t.getTarget().coordinates;
            Double distance = 0.0;

            stream.write("        \\draw (q" + nodeIndex.get(e.getSource()) + ")");
            for (EdgeIntermediatePoint c : e.intermediatePoints) {
                Vector2D betw = new Vector2D(c.getX(), c.getY());
                Double segmentlength = betw.minus(from).length();

                stream.write(" --");
                if (distance < halfLength && halfLength <= distance + segmentlength)
                    stream.write(" node[above] {$" + (t.symbol.equals("") ? "\\varepsilon" : t.symbol) + "$}");
                stream.write(" (" + c.getX() + "cm," + c.getY() + "cm)");

                distance += segmentlength;
                from = betw;
            }

            stream.write(" --");
            if (distance < halfLength)
                stream.write(" node[above] {$" + (t.symbol.equals("") ? "\\varepsilon" : t.symbol) + "$}");
            stream.write(" (q" + nodeIndex.get(e.getTarget()) + ");" + linefeed);
        }

        stream.write("    \\end{tikzpicture}" + linefeed);
        stream.write("%\\end{document}" + linefeed);
    }
}
