/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.exportfilter;

import gralog.importfilter.*;
import gralog.plugins.FileFormatDescription;
import gralog.structure.*;

import java.util.HashMap;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;


/**
 *
 * @author viktor
 */
@FileFormatDescription(
  name="Trivial Graph Format",
  text="",
  url="https://en.wikipedia.org/wiki/Trivial_Graph_Format",
  fileextension="tgf"
)
public class TrivialGraphFormatExport extends ExportFilter {
    
    public void Export(OutputStreamWriter stream, Structure structure, ExportParameters params) throws Exception
    {
        HashMap<Vertex, Integer> NodeIndex = new HashMap<Vertex,Integer>();
        Integer i = 1;
        String linefeed = System.getProperty("line.separator");
        
        Set<Vertex> V = structure.getVertices();
        for(Vertex v : V){
            NodeIndex.put(v, i);
            i++;
            stream.write(i + linefeed);
        }
        
        stream.write("#" + linefeed);
        
        Set<Edge> E = structure.getEdges();
        for(Edge e : E){
            stream.write(NodeIndex.get(e.source).toString() + " " + NodeIndex.get(e.target).toString() + linefeed);
        }            
    }

}
