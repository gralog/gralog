/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
            stream.write("] (q" + i + ") at (" + s.Coordinates.get(0) + "cm," + s.Coordinates.get(1) + "cm) {$q_{" + i + "}$};" + linefeed);
            i++;
        }

        
        
        stream.write("        " + linefeed);

        
        
        Set<Edge> E = structure.getEdges();
        for(Edge e : E){
            Transition t = (Transition)e;

            Double halfLength = t.Length()/2.0;
            Vector2D from = new Vector2D(t.getSource().Coordinates);
            Vector2D to = new Vector2D(t.getTarget().Coordinates);
            Double distance = 0.0;
            
            stream.write("        \\draw (q" + NodeIndex.get(e.getSource()) + ")" );
            for(EdgeIntermediatePoint c : e.intermediatePoints)
            {
                Vector2D betw = new Vector2D(c.get(0), c.get(1));
                Double segmentlength = betw.Minus(from).Length();
                
                stream.write(" --");
                if(distance < halfLength && halfLength <= distance + segmentlength)
                    stream.write(" node[above] {$" + (t.Symbol.equals("") ? "\\varepsilon" : t.Symbol) + "$}");
                stream.write(" (" + c.get(0) + "cm," + c.get(1) + "cm)");
                
                distance += segmentlength;
                from = betw;
            }
            
            stream.write(" --");
            if(distance < halfLength)
                stream.write(" node[above] {$" + (t.Symbol.equals("") ? "\\varepsilon" : t.Symbol) + "$}");
            stream.write(" (q" + NodeIndex.get(e.getTarget()) + ");" + linefeed);
        }

        
        
        stream.write("    \\end{tikzpicture}" + linefeed);
        stream.write("%\\end{document}" + linefeed);

    }    
    
}
