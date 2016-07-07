/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.structure.*;
import gralog.exportfilter.*;
import gralog.rendering.Vector2D;

import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

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
    
    public void Export(DirectedGraph structure, OutputStreamWriter stream, ExportFilterParameters params) throws Exception
    {
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

        
        
        HashMap<Vertex, Integer> NodeIndex = new HashMap<Vertex,Integer>();
        Integer i = 1;
        Set<Vertex> V = structure.getVertices();
        for(Vertex v : V){
            NodeIndex.put(v, i);
            stream.write("        \\node (n" + i + ") at ("
                                  + v.Coordinates.get(0) + "cm,"
                                  + v.Coordinates.get(1) + "cm) {};" + linefeed);
            i++;
        }

        
        
        stream.write("        " + linefeed);

        
        
        Set<Edge> E = structure.getEdges();
        for(Edge e : E){

            Vector2D from = new Vector2D(e.getSource().Coordinates);
            Vector2D to = new Vector2D(e.getTarget().Coordinates);
            
            stream.write("        \\draw (n" + NodeIndex.get(e.getSource()) + ")" );
            for(EdgeIntermediatePoint c : e.intermediatePoints)
                stream.write(" edge[-] (" + c.get(0) + "cm," + c.get(1) + "cm)");
            
            stream.write(" edge[-] (n" + NodeIndex.get(e.getTarget()) + ");" + linefeed);
        }

        
        
        stream.write("    \\end{tikzpicture}" + linefeed);
        stream.write("%\\end{document}" + linefeed);
    }    
    
}
