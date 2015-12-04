/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.export;

import gralog.automaton.*;
import gralog.structure.Edge;
import gralog.structure.Vertex;
import gralog.exportfilter.*;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author viktor
 */
@ExportFilterDescription(
  name="TikZ ist kein Zeichenprogramm",
  text="",
  url="http://www.texample.net/tikz/",
  fileextension="tikz"
)
public class TikZExport extends ExportFilter {
    
    public void Export(Automaton structure, OutputStreamWriter stream, ExportFilterParameters params) throws Exception
    {
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

        
        
        HashMap<State, Integer> NodeIndex = new HashMap<State,Integer>();
        Integer i = 1;
        Set<Vertex> V = structure.getVertices();
        for(Vertex v : V){
            State s = (State)v;
            NodeIndex.put(s, i);
            stream.write("        \\node[state");
            if(s.FinalState)
                stream.write(",accepting");
            if(s.StartState)
                stream.write(",initial");
            stream.write("] (q" + i + ") at (" + s.Coordinates.get(0) + "," + s.Coordinates.get(1) + ") {$q_" + i + "$};" + linefeed);
            i++;
        }        

        
        
        stream.write("        " + linefeed);

        
        
        Set<Edge> E = structure.getEdges();
        for(Edge e : E){
            Transition t = (Transition)e;
            
            stream.write("        \\path (q" + NodeIndex.get(e.source).toString() + ") "
                                + "edge node {$" + (t.Symbol.equals("") ? "\\varepsilon" : t.Symbol) + "$} "
                                + "(q" + NodeIndex.get(e.target).toString() + ");" + linefeed);
        }

        
        
        stream.write("    \\end{tikzpicture}" + linefeed);
        stream.write("%\\end{document}" + linefeed);
        stream.write(" " + linefeed);

    }    
    
}
